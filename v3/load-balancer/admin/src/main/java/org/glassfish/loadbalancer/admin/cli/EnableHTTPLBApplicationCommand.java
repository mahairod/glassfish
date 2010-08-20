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

package org.glassfish.loadbalancer.admin.cli;

import java.util.logging.Logger;
import java.beans.PropertyVetoException;

import org.jvnet.hk2.annotations.*;
import org.jvnet.hk2.component.*;
import org.jvnet.hk2.config.*;
import org.glassfish.api.Param;
import org.glassfish.api.ActionReport;
import com.sun.enterprise.util.LocalStringManagerImpl;
import com.sun.enterprise.config.serverbeans.ApplicationRef;
import com.sun.enterprise.config.serverbeans.Domain;
import com.sun.enterprise.config.serverbeans.Server;
import com.sun.enterprise.config.serverbeans.Cluster;

import org.glassfish.api.admin.*;
import org.glassfish.config.support.TargetType;
import org.glassfish.config.support.CommandTarget;

/**
 * This is a remote command that enables lb-enabled attribute of an application
 * for cluster or instance
 * @author Yamini K B
 */
@Service(name = "enable-http-lb-application")
@Scoped(PerLookup.class)
@TargetType(value={CommandTarget.STANDALONE_INSTANCE, CommandTarget.CLUSTER})
@org.glassfish.api.admin.Cluster(RuntimeType.DAS)
public final class EnableHTTPLBApplicationCommand implements AdminCommand {

    @Param(primary=true)
    String target;

    @Param(optional=false)
    String name;

    @Inject
    Domain domain;

    final private static LocalStringManagerImpl localStrings =
        new LocalStringManagerImpl(EnableHTTPLBApplicationCommand.class);

    @Override
    public void execute(AdminCommandContext context) {
        ActionReport report = context.getActionReport();

        Logger logger = context.getLogger();
        
        report.setActionExitCode(ActionReport.ExitCode.SUCCESS);

        ApplicationRef appRef = domain.getApplicationRefInTarget(name, target);

        if (appRef == null) {
            String msg = localStrings.getLocalString("AppRefNotDefined",
                    "Application ref [{0}] does not exist in server [{1}]", name, target);
            report.setActionExitCode(ActionReport.ExitCode.FAILURE);
            report.setMessage(msg);
            return;
        }

        boolean appEnabled = Boolean.valueOf(appRef.getEnabled());

        if (appEnabled) {
            if (appRef.getLbEnabled().equals("true")) {
                String msg = localStrings.getLocalString("AppEnabled",
                        "Application [{0}] is already enabled for [{1}].", name, target);
                logger.warning(msg);
                report.setActionExitCode(ActionReport.ExitCode.FAILURE);
                report.setMessage(msg);
            } else {
                try {
                    updateLbEnabledForApp(name, target);
                } catch(TransactionFailure e) {
                    String msg = localStrings.getLocalString("FailedToUpdateAttr",
                            "Failed to update lb-enabled attribute for {0}", name);
                    logger.warning(msg);
                    report.setActionExitCode(ActionReport.ExitCode.FAILURE);
                    report.setMessage(msg);
                    report.setFailureCause(e);
                }
            }
        }
    }

    public void updateLbEnabledForApp(final String appName,
        final String target) throws TransactionFailure {
        ConfigSupport.apply(new SingleConfigCode() {
            @Override
            public Object run(ConfigBeanProxy param) throws PropertyVetoException, TransactionFailure {
                // get the transaction
                Transaction t = Transaction.getTransaction(param);
                if (t!=null) {
                    Server servr = ((Domain)param).getServerNamed(target);
                    if (servr != null) {
                        // update the application-ref from standalone
                        // server instance
                        for (ApplicationRef appRef :
                            servr.getApplicationRef()) {
                            if (appRef.getRef().equals(appName)) {
                                ConfigBeanProxy appRef_w = t.enroll(appRef);
                                ((ApplicationRef)appRef_w).setLbEnabled("true");
                                break;
                            }
                        }
                    }
                    Cluster cluster = ((Domain)param).getClusterNamed(target);
                    if (cluster != null) {
                        // update the application-ref from cluster
                        for (ApplicationRef appRef :
                            cluster.getApplicationRef()) {
                            if (appRef.getRef().equals(appName)) {
                                ConfigBeanProxy appRef_w = t.enroll(appRef);
                                ((ApplicationRef)appRef_w).setLbEnabled("true");
                                break;
                            }
                        }

                        // update the application-ref from cluster instances
                        for (Server svr : cluster.getInstances() ) {
                            for (ApplicationRef appRef :
                                svr.getApplicationRef()) {
                                if (appRef.getRef().equals(appName)) {
                                    ConfigBeanProxy appRef_w = t.enroll(appRef);
                                    ((ApplicationRef)appRef_w).setLbEnabled("true");
                                    break;
                                }
                            }
                        }
                    }
             }
             return Boolean.TRUE;
            }
        }, domain);
    }
}
