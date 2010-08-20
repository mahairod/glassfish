/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.enterprise.v3.admin.cluster;

import com.sun.enterprise.util.StringUtils;
import com.sun.enterprise.config.serverbeans.Node;
import com.sun.enterprise.config.serverbeans.Nodes;
import com.sun.enterprise.config.serverbeans.SshConnector;
import com.sun.enterprise.config.serverbeans.SshAuth;
import org.glassfish.api.ActionReport;
import org.glassfish.api.I18n;
import org.glassfish.api.Param;
import org.glassfish.api.admin.*;
import org.glassfish.api.admin.CommandValidationException;
import org.glassfish.api.admin.CommandRunner.CommandInvocation;
import org.jvnet.hk2.annotations.*;
import org.jvnet.hk2.component.*;
import org.glassfish.cluster.ssh.launcher.SSHLauncher;
import java.util.logging.Logger;

/**
 * Remote AdminCommand to update an ssh node.  This command is run only on DAS.
 *
 * @author Joe Di Pol
 */
@Service(name = "update-node-ssh")
@I18n("update.node.ssh")
@Scoped(PerLookup.class)
@Cluster({RuntimeType.DAS})
public class UpdateNodeSshCommand implements AdminCommand  {

    @Inject
    private CommandRunner cr;

    @Inject
    Habitat habitat;

    @Inject
    private Nodes nodes;

    @Param(name="name", primary = true)
    private String name;

    @Param(name="nodehost", optional=true)
    private String nodehost;

    @Param(name = "installdir", optional=true)
    private String installdir;

    @Param(name = "nodedir", optional=true)
    private String nodedir;

    @Param(name = "sshport", optional=true)
    private String sshport;

    @Param(name = "sshuser", optional = true)
    private String sshuser;

    @Param(name = "sshkeyfile", optional = true)
    private String sshkeyfile;

    @Param(name = "sshpassword", optional = true, password=true)
    private String sshpassword;

    @Param(name = "sshkeypassphrase", optional = true, password=true)
    private String sshkeypassphrase;

    @Param(name =  "force", optional = true, defaultValue = "false")
    private boolean force;

    private static final String NL = System.getProperty("line.separator");

    private Logger logger = null;

    @Override
    public void execute(AdminCommandContext context) {
        ActionReport report = context.getActionReport();
        StringBuilder msg = new StringBuilder();
        SSHLauncher sshL=habitat.getComponent(SSHLauncher.class);
        Node node = null;

        logger = context.getLogger();

        // Make sure Node is valid
        node = nodes.getNode(name);
        if (node == null) {
            String m = Strings.get("noSuchNode", node);
            logger.warning(m);
            report.setActionExitCode(ActionReport.ExitCode.FAILURE);
            report.setMessage(m);
            return;
        }

        // First create a map that holds the parameters and reflects what
        // the user passed on the command line.
        ParameterMap map = new ParameterMap();
        map.add("DEFAULT", name);
        map.add(NodeUtils.PARAM_INSTALLDIR, installdir);
        map.add(NodeUtils.PARAM_NODEHOST, nodehost);
        map.add(NodeUtils.PARAM_NODEDIR, nodedir);
        map.add(NodeUtils.PARAM_SSHPORT, sshport);
        map.add(NodeUtils.PARAM_SSHUSER, sshuser);
        map.add(NodeUtils.PARAM_SSHKEYFILE, sshkeyfile);
        map.add(NodeUtils.PARAM_SSHPASSWORD, sshpassword);
        map.add(NodeUtils.PARAM_SSHKEYPASSPHRASE, sshkeypassphrase);
        map.add(NodeUtils.PARAM_TYPE, "SSH");

        // Now init any parameters that weren't passed into the command
        // using the values from the config
        initFromConfig(node);

        // Finally, anything that still isn't set, use the defaults.
        // These should likely come from config -- but they don't
        // as of now
        setDefaults();

        // validateMap holds the union of what the user passed and what was
        // in the config so we have all the settings needed to validate what
        // the node will look like after we update it.
        ParameterMap validateMap = new ParameterMap();
        validateMap.add(NodeUtils.PARAM_INSTALLDIR, installdir);
        validateMap.add(NodeUtils.PARAM_NODEHOST, nodehost);
        validateMap.add(NodeUtils.PARAM_NODEDIR, nodedir);
        validateMap.add(NodeUtils.PARAM_SSHPORT, sshport);
        validateMap.add(NodeUtils.PARAM_SSHUSER, sshuser);
        validateMap.add(NodeUtils.PARAM_SSHKEYFILE, sshkeyfile);
        validateMap.add(NodeUtils.PARAM_SSHPASSWORD, sshpassword);
        validateMap.add(NodeUtils.PARAM_SSHKEYPASSPHRASE, sshkeypassphrase);

        // Validate the settings
        try {
            NodeUtils nodeUtils = new NodeUtils(habitat, logger);
            nodeUtils.validate(validateMap, sshL);
        } catch (CommandValidationException e) {
            String m1 = Strings.get("node.ssh.invalid.params");
            if (!force) {
                String m2 = Strings.get("update.node.ssh.not.updated");
                msg.append(StringUtils.cat(NL, m1, m2, e.getMessage()));
                report.setMessage(msg.toString());
                report.setActionExitCode(ActionReport.ExitCode.FAILURE);
                return;
            } else {
                String m2 = Strings.get("update.node.ssh.continue.force");
                msg.append(StringUtils.cat(NL, m1, e.getMessage(), m2));
            }
        }

        // Settings are valid. Now use the generic update-node command to
        // update the node.
        CommandInvocation ci = cr.getCommandInvocation("_update-node", report);
        ci.parameters(map);
        ci.execute();

        if (StringUtils.ok(report.getMessage())) {
            if (msg.length() > 0) {
                msg.append(NL);
            }
            msg.append(report.getMessage());
        }

        report.setMessage(msg.toString());
    }

    /**
     * Initialize any parameters not provided by the user from the
     * configuration.
     */
    private void initFromConfig(Node node) {
        if (nodehost == null) {
            nodehost = node.getNodeHost();
        }

        if (installdir == null) {
            installdir = node.getInstallDir();
        }

        if (nodedir == null) {
            nodedir = node.getNodeDir();
        }

        SshConnector sshc = node.getSshConnector();
        if (sshc == null) {
            return;
        }

        if (sshport == null) {
            sshport = sshc.getSshPort();
        }

        SshAuth ssha = sshc.getSshAuth();
        if (ssha == null) {
            return;
        }

        if (sshuser == null) {
            sshuser = ssha.getUserName();
        }

        if (sshkeyfile == null) {
            sshkeyfile = ssha.getKeyfile();
        }

        if (sshpassword == null) {
            sshpassword = ssha.getPassword();
        }

        if (sshkeypassphrase == null) {
            sshkeypassphrase = ssha.getPassword();
        }
    }

   private void setDefaults() {
        if (sshport == null) {
            sshport = NodeUtils.NODE_DEFAULT_SSH_PORT;
        }
        if (sshuser == null) {
            sshuser = NodeUtils.NODE_DEFAULT_SSH_USER;
        }
        if (installdir == null) {
            installdir = NodeUtils.NODE_DEFAULT_INSTALLDIR;
        }
    }
}
