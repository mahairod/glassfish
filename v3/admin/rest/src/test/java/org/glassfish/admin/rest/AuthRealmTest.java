/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
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

package org.glassfish.admin.rest;

import java.util.List;
import com.sun.jersey.api.client.ClientResponse;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

public class AuthRealmTest extends RestTestBase {
    public static final String URL_LIST_GROUP_NAMES = BASE_URL + "/configs/config/server-config/security-service/auth-realm/admin-realm/list-group-names?userName=admin";
    public static final String URL_SUPPORTS_USER_MANAGEMENT = BASE_URL + "/configs/config/server-config/security-service/auth-realm/admin-realm/supports-user-management";
    public static final String URL_LIST_ADMIN_REALM_USERS = BASE_URL + "/configs/config/server-config/security-service/auth-realm/admin-realm/list-users";
    public static final String URL_LIST_FILE_USERS = BASE_URL + "/configs/config/server-config/security-service/auth-realm/file/list-users";
    public static final String URL_CREATE_USER = BASE_URL + "/configs/config/server-config/security-service/auth-realm/file/create-user";
    public static final String URL_DELETE_USER = BASE_URL + "/configs/config/server-config/security-service/auth-realm/file/delete-user";
    public static final String URL_AUTH_REALM_CLASS_NAMES = BASE_URL + "/configs/config/server-config/security-service/auth-realm/list-predefined-authrealm-classnames";
    @Test
    public void testListGroupNames() {
        List<String> groups = getCommandResults(get(URL_LIST_GROUP_NAMES));
        assertTrue(groups.size() == 2);
    }

    @Test
    public void testSupportsUserManagement() {
        List<String> groups = getCommandResults(get(URL_SUPPORTS_USER_MANAGEMENT));
        assertEquals("true", groups.get(0));
    }

    @Test
    public void testUserManagement() {
        final String userName = "user" + generateRandomString();
        Map<String, String> newUser = new HashMap<String, String>() {{
           put ("id", userName);
           put ("AS_ADMIN_USERPASSWORD", "password");
        }};

        ClientResponse response = create(URL_CREATE_USER, newUser);
        assertTrue(isSuccess(response));

        List<String> values = getCommandResults(get(URL_LIST_FILE_USERS));
        assertTrue(values.contains(userName));

        response = delete(URL_DELETE_USER, newUser);
        assertTrue(isSuccess(response));

        values = getCommandResults(get(URL_LIST_FILE_USERS));
        assertFalse(values.contains(userName));
    }

    @Test
    public void testListAuthRealmClassNames() {
        List<String> classNameList = getCommandResults(get(URL_AUTH_REALM_CLASS_NAMES));
        // Overkill? Too fragile?
        assertTrue(classNameList.contains("com.sun.enterprise.security.auth.realm.certificate.CertificateRealm"));
        assertTrue(classNameList.contains("com.sun.enterprise.security.auth.realm.jdbc.JDBCRealm"));
        assertTrue(classNameList.contains("com.sun.enterprise.security.auth.realm.file.FileRealm"));
        assertTrue(classNameList.contains("com.sun.enterprise.security.auth.realm.pam.PamRealm"));
        assertTrue(classNameList.contains("com.sun.enterprise.security.auth.realm.ldap.LDAPRealm"));
    }
}
