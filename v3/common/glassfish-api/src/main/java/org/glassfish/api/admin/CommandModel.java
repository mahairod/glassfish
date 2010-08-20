/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2008-2010 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.api.admin;

import org.glassfish.api.Param;
import org.glassfish.api.I18n;

import java.util.Collection;
import java.util.ArrayList;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.beans.Introspector;

import com.sun.hk2.component.InjectionResolver;

/**
 * Model for an administrative command
 *
 * @author Jerome Dochez
 */
public abstract class CommandModel {

    public abstract String getCommandName();

    public abstract I18n getI18n();

    public abstract ParamModel getModelFor(String paramName);

    public abstract Collection<String> getParametersNames();

    /**
     * Return the cluster parameters for this command  or null if none are
     * specified and defaults should be used.
     *
     * @return a {@link Cluster} annotation instance or null
     */
    public abstract Cluster getClusteringAttributes();

    public Collection<ParamModel> getParameters() {
        ArrayList<ParamModel> copy = new ArrayList<ParamModel>();
        for (String name : getParametersNames()) {
            copy.add(getModelFor(name));
        }
        return copy;
    }

    /**
     * Get the Param name.  First it checks if the annotated Param
     * includes a name, if not then it gets the name from the field.
     * If the parameter is a password, add the prefix and change the
     * name to upper case.
     *
     * @param param class annotation
     * @param annotated annotated field or method
     * @return the name of the param
     */
    public static String getParamName(Param param, AnnotatedElement annotated) {
        String name = param.name();
        if (name.equals("")) {
            if (annotated instanceof Field) {
                name = ((Field) annotated).getName();
            }
            if (annotated instanceof Method) {
                name = ((Method) annotated).getName().substring(3);
                name = Introspector.decapitalize(name);
            }
        }
        if (param.password()) {
            name = ASADMIN_CMD_PREFIX + name.toUpperCase();
        }
        return name;
    }    

    public static abstract class ParamModel {

        public abstract String getName();

        public abstract Param getParam();

        public abstract I18n getI18n();

        public abstract Class getType();

        public boolean isParamId(String key) {
            if (getParam().primary()) {
                return "DEFAULT".equals(key) || getName().equalsIgnoreCase(key);
            }
            if (getParam().password()) {
                return key.startsWith(ASADMIN_CMD_PREFIX);
            }
            return getName().equalsIgnoreCase(key) ||
		    getParam().shortName().equals(key) ||
		    getParam().alias().equalsIgnoreCase(key);
        }

    }

    /**
     * Should an unknown option be considered an operand by asadmin?
     */
    public boolean unknownOptionsAreOperands() {
	return false;	// default implementation
    }

    private static final String ASADMIN_CMD_PREFIX = "AS_ADMIN_";
    
}
