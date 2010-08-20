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

package com.sun.enterprise.tools.verifier.tests.ejb.entity.pksinglefield;

import com.sun.enterprise.tools.verifier.tests.ejb.EjbTest;
import java.lang.reflect.Field;
import java.lang.ClassLoader;
import com.sun.enterprise.tools.verifier.tests.*;
import com.sun.enterprise.deployment.*;
import com.sun.enterprise.tools.verifier.*;
import com.sun.enterprise.tools.verifier.tests.ejb.EjbCheck;

/** 
 * The type of the primkey-field must be the same as the primary key type.
 */
public class PrimekeyFieldPrimaryKeyType extends EjbTest implements EjbCheck { 


    /** 
     * The type of the primkey-field must be the same as the primary key type.
     *
     * @param descriptor the Enterprise Java Bean deployment descriptor
     *   
     * @return <code>Result</code> the results for this assertion
     */
    public Result check(EjbDescriptor descriptor) {

	Result result = getInitializedResult();
	ComponentNameConstructor compName = getVerifierContext().getComponentNameConstructor();

	// The type of the primkey-field must be the same as the primary key type.
	if (descriptor instanceof EjbEntityDescriptor) {
	    String persistence =
		((EjbEntityDescriptor)descriptor).getPersistenceType();

	    if (EjbEntityDescriptor.CONTAINER_PERSISTENCE.equals(persistence)) {
		try {
		    VerifierTestContext context = getVerifierContext();
		ClassLoader jcl = context.getClassLoader();
		    Class c = Class.forName(descriptor.getEjbClassName(), false, getVerifierContext().getClassLoader());
		    try {
			if (((EjbCMPEntityDescriptor)descriptor).getPrimaryKeyFieldDesc() != null) {
			    Field pkf = c.getDeclaredField(((EjbCMPEntityDescriptor)descriptor).getPrimaryKeyFieldDesc().getName());
			    Class pkfType = pkf.getType();
			    try {
				String primkey =
				    ((EjbEntityDescriptor)descriptor).getPrimaryKeyClassName();
    
				boolean foundMatch = false;
				if (primkey.equals(pkfType.getName())) {
				    foundMatch = true;
				} else {
				    foundMatch = false;
				}
      
				if (foundMatch) {
				    result.addGoodDetails(smh.getLocalString
							  ("tests.componentNameConstructor",
							   "For [ {0} ]",
							   new Object[] {compName.toString()}));
				    result.passed(smh.getLocalString
						  (getClass().getName() + ".passed",
						   "The type of the primkey-field [ {0} ] is the same as the primary key type [ {1} ] for bean [ {2} ]",
						   new Object[] {((EjbCMPEntityDescriptor)descriptor).getPrimaryKeyFieldDesc().getName(),primkey,descriptor.getName()}));
				} else {
				    result.addErrorDetails(smh.getLocalString
							   ("tests.componentNameConstructor",
							    "For [ {0} ]",
							    new Object[] {compName.toString()}));
				    result.failed(smh.getLocalString
						  (getClass().getName() + ".failed",
						   "The type of the primkey-field [ {0} ] is not the same as the primary key type [ {1} ] for bean [ {2} ]",
						   new Object[] {((EjbCMPEntityDescriptor)descriptor).getPrimaryKeyFieldDesc().getName(),primkey,descriptor.getName()}));
				}
			    } catch (NullPointerException e) {
				result.addNaDetails(smh.getLocalString
						    ("tests.componentNameConstructor",
						     "For [ {0} ]",
						     new Object[] {compName.toString()}));
				result.notApplicable(smh.getLocalString
						     (getClass().getName() + ".notApplicable2",
						      "Primkey field not defined for [ {0} ] bean.",
						      new Object[] {descriptor.getName()}));
			    }
			} else {
			    result.addNaDetails(smh.getLocalString
						("tests.componentNameConstructor",
						 "For [ {0} ]",
						 new Object[] {compName.toString()}));
			    result.notApplicable(smh.getLocalString
						 (getClass().getName() + ".notApplicable2",
						  "Primkey field not defined for [ {0} ] bean.",
						  new Object[] {descriptor.getName()}));
			}
		    } catch (NullPointerException e) {
			result.addNaDetails(smh.getLocalString
					    ("tests.componentNameConstructor",
					     "For [ {0} ]",
					     new Object[] {compName.toString()}));
			result.notApplicable(smh.getLocalString
					     (getClass().getName() + ".notApplicable3",
					      "Primkey field not defined within [ {0} ] bean.",
					      new Object[] {descriptor.getName()}));
		    } catch (NoSuchFieldException e) {
			result.addNaDetails(smh.getLocalString
					    ("tests.componentNameConstructor",
					     "For [ {0} ]",
					     new Object[] {compName.toString()}));
			result.notApplicable(smh.getLocalString
					     (getClass().getName() + ".notApplicable2",
					      "Primkey field [ {0} ] not defined within [ {1} ] bean.",
					      new Object[] {((EjbCMPEntityDescriptor)descriptor).getPrimaryKeyFieldDesc().getName(),descriptor.getName()}));
		    }
		} catch (ClassNotFoundException e) {
		    Verifier.debug(e);
		    result.addErrorDetails(smh.getLocalString
					   ("tests.componentNameConstructor",
					    "For [ {0} ]",
					    new Object[] {compName.toString()}));
		    result.failed(smh.getLocalString
				  (getClass().getName() + ".failedException",
				   "Error: EJB class [ {0} ] does not exist or is not loadable within bean [ {1} ]",
				   new Object[] {descriptor.getEjbClassName(),descriptor.getName()}));
		}
	    } else {
		result.addNaDetails(smh.getLocalString
				    ("tests.componentNameConstructor",
				     "For [ {0} ]",
				     new Object[] {compName.toString()}));
		result.notApplicable(smh.getLocalString
				     (getClass().getName() + ".notApplicable1",
				      "Expected persistence type [ {0} ], but bean [ {1} ] has persistence type [ {2} ]",
				      new Object[] {EjbEntityDescriptor.CONTAINER_PERSISTENCE,descriptor.getName(),persistence}));
	    }
	} else {
	    result.addNaDetails(smh.getLocalString
				("tests.componentNameConstructor",
				 "For [ {0} ]",
				 new Object[] {compName.toString()}));
	    result.notApplicable(smh.getLocalString
				 (getClass().getName() + ".notApplicable",
				  "{0} expected \n {1} bean, but called with {2} bean",
				  new Object[] {getClass(),"Entity","Session"}));
	}

	return result;
    }
}
