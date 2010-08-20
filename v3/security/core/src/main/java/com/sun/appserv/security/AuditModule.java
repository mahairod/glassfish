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

/*
 * AuditModule.java
 *
 * Created on July 27, 2003, 11:32 PM
 */

package com.sun.appserv.security;

import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
/**
 * Base class that should be extended by all classes that wish to provide their
 * own Audit support.
 * @author  Harpreet Singh
 * @version
 */
public abstract class AuditModule {
    protected Properties props = null;
    /** 
     * Method is invoked at server startup, during AuditModule initialization.
     * If method returns without any exception then S1AS assumes that the module
     * is ready to serve any requests.
     * @param props the properties for the AuditModule. These properties are
     * defined in the domain.xml
     */
    public void init(Properties props) {
        this.props = props;
    }
    
    /**
     * Invoked post authentication request for a user in a given realm
     * @param user username for whom the authentication request was made
     * @param realm the realm name under which the user is authenticated.
     * @param success the status of the authentication
     */
    public void authentication(String user, String realm, boolean success) {
    }
    
    /**
     * Invoked post web authorization request. 
     * @param user the username for whom the authorization was performed
     * @param req the HttpRequest object for the web request
     * @param type the permission type, hasUserDataPermission 
     * or hasResourcePermission.
     * @param success the status of the web authorization request
     */
    public void webInvocation(String user, HttpServletRequest req, 
            String type, boolean success) {
    }
    /**
     * Invoked post ejb authorization request.
     * @param user the username for whom the authorization was performed
     * @param ejb the ejb name for which this authorization was performed
     * @param method the method name for which this authorization was performed
     * @param success the status of the ejb authorization request
     */
    public void ejbInvocation(String user, String ejb, String method, boolean success) {
    }
    
    /**
     * Invoked during validation of the web service request
     * @param uri The URL representation of the web service endpoint
     * @param endpoint The name of the endpoint representation
     * @param success the status of the web service request validation
     */
    public void webServiceInvocation(String uri, String endpoint, boolean success) {
    }
    
    /**
     * Invoked during validation of the web service request
     * @param endpoint The representation of the web service endpoint
     * @param success the status of the web service request validation
     */
    public void ejbAsWebServiceInvocation(String endpoint, boolean success) {
    }

    /**
     * Invoked upon completion of the server startup
     */
    public void serverStarted() {
    }

    /**
     * Invoked upon completion of the server shutdown
     */
    public void serverShutdown() {
    }

}
