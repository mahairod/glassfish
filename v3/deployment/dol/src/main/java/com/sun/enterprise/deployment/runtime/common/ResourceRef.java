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

package com.sun.enterprise.deployment.runtime.common;

import com.sun.enterprise.deployment.runtime.RuntimeDescriptor;

/**
* this class is a shameful duplication of info found in normal descriptors
* it was kept to be backward compatible with the schema2beans descriptors
* generated by iAS 7.0 engineering team.
*
* @author Jerome Dochez
*/
public class ResourceRef extends RuntimeDescriptor
{
    static public final String RES_REF_NAME = "ResRefName";	// NOI18N
    static public final String JNDI_NAME = "JndiName";	// NOI18N
    static public final String DEFAULT_RESOURCE_PRINCIPAL = "DefaultResourcePrincipal";	// NOI18N
    
    // This attribute is mandatory
    public void setResRefName(String value)
    {
	this.setValue(RES_REF_NAME, value);
    }
    
    //
    public String getResRefName()
    {
	return (String)this.getValue(RES_REF_NAME);
    }
    
    // This attribute is mandatory
    public void setJndiName(String value)
    {
	this.setValue(JNDI_NAME, value);
    }
    
    //
    public String getJndiName()
    {
	return (String)this.getValue(JNDI_NAME);
    }
    
    // This attribute is optional
    public void setDefaultResourcePrincipal(DefaultResourcePrincipal value)
    {
	this.setValue(DEFAULT_RESOURCE_PRINCIPAL, value);
    }
    
    //
    public DefaultResourcePrincipal getDefaultResourcePrincipal()
    {
	return (DefaultResourcePrincipal)this.getValue(DEFAULT_RESOURCE_PRINCIPAL);
    }
    
    // This method verifies that the mandatory properties are set
    public boolean verify()
    {
	return true;
    }
}
