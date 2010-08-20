/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-2010 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.osgihttp;

import org.osgi.framework.Bundle;
import org.osgi.service.http.HttpContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

/**
 * Default implementation of {@link HttpContext}.
 * As per the spec (OSGi R4 Compendium, section #102.4):
 * 1. the default implementation of getResource() must map the resource request
 * to the Bundle.getResource(String).
 * 2. the getMime(String) implementation of the default HttpContext object
 * should return a reasonable mapping.
 * 3. Its handleSecurity(HttpServlet Request,HttpServletResponse)
 * may implement an authentication mecha-nism that is implementation-dependent.
 * <p/>
 * {@code org.osgi.service.http.HttpService#createDefaultHttpContext()}
 *
 * @author Sanjeeb.Sahoo@Sun.COM
 */
public class DefaultHttpContext implements HttpContext {

    /*
    * TODO(Sahoo): getMimeType() to use default-web.xml.
    */

    /**
     * Bundle for which this context is created.
     */
    private Bundle registeringBundle;

    public DefaultHttpContext(Bundle registeringBundle) {
        this.registeringBundle = registeringBundle;
    }

    public boolean handleSecurity(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        return true;// TODO(Sahoo):
    }

    /**
     * @param s
     * @return URL
     */
    public URL getResource(String s) {
        /*
         * As per the spec (OSGi R4 Compendium, section #102.4):
         * the default implementation must map the resource request to the
         * bundle's resource, using Bundle.getResource(String).
         * The internal name must specify the full path to the directory
         * containing the resource files in the bundle. No automatic prefixing
         * of the package name is done.
         */
        return registeringBundle.getResource(s); // TODO(Sahoo): doPrivileged()
    }

    public String getMimeType(String s) {
        return null;
    }
}
