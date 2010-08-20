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

package com.sun.enterprise.util;

import java.util.*;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author bnevins
 */
public class UtilityTest {

    public UtilityTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * VERY SIMPLE Test of getEnvOrProp method, of class Utility.
     */
    @Test
    public void testGetEnvOrProp() {
        System.out.println("getEnvOrProp");
        Map<String, String> env = System.getenv();
        Set<String> keys = env.keySet();
        String key = null;
        String value = null;

        // warning:  super-paranoid bullet-proof test ahead!!!
        for (String akey : keys) {
            // Make sure both key and value are kosher

            if(!StringUtils.ok(akey))
                continue;

            // make sure this name:value is NOT in System Properties!
            // empty string counts as a value!
            if(System.getProperty(akey) != null)
                continue;

            String avalue = env.get(akey);

            if(!StringUtils.ok(avalue))
                continue;

            key = akey;
            value = avalue;
            break;
        }

        // allow the case where there are no env. variables.  Probably impossible
        // but this test needs to run on many many many environments and we don't
        // want to fail in such a case.

        if(key == null)
            return;

        assertEquals(Utility.getEnvOrProp(key), value);
        String sysPropValue = "SYS_PROP" + value;
        System.setProperty(key, sysPropValue);
        assertEquals(Utility.getEnvOrProp(key), sysPropValue);
    }
}
