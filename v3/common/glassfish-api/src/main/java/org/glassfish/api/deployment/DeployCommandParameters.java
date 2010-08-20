/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.api.deployment;

import org.glassfish.api.Param;
import org.glassfish.api.I18n;

import java.io.File;
import java.util.Properties;

/**
 * Parameters passed by the user to a deployment request. 
 */
public class DeployCommandParameters extends OpsParams {
    
    @Param(optional=true)
    public String name = null;

    @Param(name = ParameterNames.CONTEXT_ROOT, optional=true)
    public String contextroot = null;

    @Param(name = ParameterNames.VIRTUAL_SERVERS, optional=true)
    @I18n("virtualservers")
    public String virtualservers = null;

    @Param(name=ParameterNames.LIBRARIES, optional=true)
    public String libraries = null;

    @Param(optional=true, defaultValue="false")
    public Boolean force = false;

    @Param(name=ParameterNames.PRECOMPILE_JSP, optional=true, defaultValue="false")
    public Boolean precompilejsp = false;

    @Param(optional=true, defaultValue="false")
    public Boolean verify = false;

    @Param(optional=true)
    public String retrieve = null;

    @Param(optional=true)
    public String dbvendorname = null;

    //mutually exclusive with dropandcreatetables
    @Param(optional=true)
    public Boolean createtables;

    //mutually exclusive with createtables
    @Param(optional=true)
    public Boolean dropandcreatetables;

    @Param(optional=true)
    public Boolean uniquetablenames;

    @Param(name=ParameterNames.DEPLOYMENT_PLAN, optional=true)
    public File deploymentplan = null;

    @Param(name=ParameterNames.ENABLED, optional=true, defaultValue="true")
    public Boolean enabled=true;

    @Param(optional=true, defaultValue="false")
    public Boolean generatermistubs = false;

    @Param(optional=true, defaultValue="false")
    public Boolean availabilityenabled = false;

    @Param(optional=true, defaultValue="true")
    public Boolean asyncreplication = true;

    @Param(optional=true)
    public String target = "server";

    @Param(optional=true, defaultValue="false")
    public Boolean keepreposdir = false;

    @Param(optional=true, defaultValue="false")
    public Boolean keepfailedstubs = false;

    @Param(optional=true, defaultValue="true")
    public Boolean logReportedErrors = false;

    @Param(primary=true)
    public File path;

    @Param(optional=true)
    public String description;

    @Param(optional=true, name="properties", separator=':')
    public Properties properties;

    @Param(optional=true, name="property", separator=':')
    public Properties property;

    @Param(optional=true)
    public String type = null;

    @Param(optional=true)
    public Boolean keepstate;

    @Param(optional=true, defaultValue="true")
    public Boolean lbenabled = true;

    // todo : why is this not a param ?
    public Boolean clientJarRequested = true;

    public String previousContextRoot = null;

    public String name() {
        return name;
    }

    public String libraries() {
        return libraries;
    }

    public DeployCommandParameters() {
    }

    public DeployCommandParameters(File path) {
        this.path = path;
        if (path.getName().lastIndexOf('.')!=-1) {
            name=path.getName().substring(0, path.getName().lastIndexOf('.'));
        } else {
            name=path.getName();            
        }
    }

    public static class ParameterNames {

        public static final String COMPONENT = "component";
        public static final String VIRTUAL_SERVERS = "virtualservers";
        public static final String CONTEXT_ROOT = "contextroot";
        public static final String LIBRARIES = "libraries";
        public static final String DIRECTORY_DEPLOYED = "directorydeployed";
        public static final String LOCATION = "location";
        public static final String ENABLED = "enabled";
        public static final String PRECOMPILE_JSP = "precompilejsp";
        public static final String DEPLOYMENT_PLAN = "deploymentplan";
    }
    
}
