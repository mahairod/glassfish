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

package org.glassfish.connectors.admin.cli;

import static org.glassfish.resource.common.ResourceConstants.*;

import org.glassfish.admin.cli.resources.ResourceManager;
import org.glassfish.admin.cli.resources.ResourceUtil;
import org.glassfish.resource.common.ResourceStatus;
import org.glassfish.api.I18n;
import org.jvnet.hk2.annotations.Inject;
import org.jvnet.hk2.annotations.Scoped;
import org.jvnet.hk2.component.PerLookup;
import org.jvnet.hk2.config.types.Property;

import com.sun.enterprise.config.serverbeans.*;
import com.sun.enterprise.util.LocalStringManagerImpl;

import org.jvnet.hk2.annotations.Service;
import org.jvnet.hk2.config.ConfigSupport;
import org.jvnet.hk2.config.SingleConfigCode;
import org.jvnet.hk2.config.TransactionFailure;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.beans.PropertyVetoException;


@Service(name= ServerTags.CUSTOM_RESOURCE)
@Scoped(PerLookup.class)
@I18n("create.custom.resource")
public class CustomResourceManager implements ResourceManager {

    final private static LocalStringManagerImpl localStrings =
            new LocalStringManagerImpl(CustomResourceManager.class);
    private static final String DESCRIPTION = ServerTags.DESCRIPTION;

    @Inject
    private ResourceUtil resourceUtil;

    private String resType = null;
    private String factoryClass = null;
    private String enabled = null;
    private String enabledValueForTarget = null;
    private String description = null;
    private String jndiName = null;

    public String getResourceType() {
        return ServerTags.CUSTOM_RESOURCE;
    }

    public ResourceStatus create(Resources resources, HashMap attributes, final Properties properties,
                                 String target, boolean requiresNewTransaction, boolean createResourceRef,
                                 boolean requiresValidation)
            throws Exception {
        setAttributes(attributes, target);

        if (resType == null) {
            String msg = localStrings.getLocalString(
                    "create.custom.resource.noResType",
                    "No type defined for Custom Resource.");
            return new ResourceStatus(ResourceStatus.FAILURE, msg, true);
        }

        if (factoryClass == null) {
            String msg = localStrings.getLocalString(
                    "create.custom.resource.noFactoryClassName",
                    "No Factory class name defined for Custom Resource.");
            return new ResourceStatus(ResourceStatus.FAILURE, msg, true);
        }

        // ensure we don't already have one of this name
        if (resources.getResourceByName(BindableResource.class, jndiName) != null) {
            String msg = localStrings.getLocalString(
                    "create.custom.resource.duplicate",
                    "A Custom Resource named {0} already exists.",
                    jndiName);
            return new ResourceStatus(ResourceStatus.FAILURE, msg, true);
        }

        if (requiresNewTransaction) {
            try {
                ConfigSupport.apply(new SingleConfigCode<Resources>() {

                    public Object run(Resources param) throws PropertyVetoException,
                            TransactionFailure {

                        return createResource(param, properties);
                    }
                }, resources);

                if(createResourceRef){
                    resourceUtil.createResourceRef(jndiName, enabledValueForTarget, target);
                }
            } catch (TransactionFailure tfe) {
                String msg = localStrings.getLocalString(
                        "create.custom.resource.fail",
                        "Unable to create custom resource {0}.", jndiName) +
                        " " + tfe.getLocalizedMessage();
                return new ResourceStatus(ResourceStatus.FAILURE, msg, true);
            }
        } else {
            createResource(resources, properties);
        }

        String msg = localStrings.getLocalString(
                "create.custom.resource.success",
                "Custom Resource {0} created.", jndiName);
        return new ResourceStatus(ResourceStatus.SUCCESS, msg, true);
    }

    private void setAttributes(HashMap attributes, String target) {
        jndiName = (String) attributes.get(JNDI_NAME);
        resType =  (String) attributes.get(RES_TYPE);
        factoryClass = (String) attributes.get(FACTORY_CLASS);
        if(target != null){
            enabled = resourceUtil.computeEnabledValueForResourceBasedOnTarget((String)attributes.get(ENABLED), target);
        }else{
            enabled = (String) attributes.get(ENABLED);
        }
        enabledValueForTarget = (String) attributes.get(ENABLED);
        description = (String) attributes.get(DESCRIPTION);
    }

    private Object createResource(Resources param, Properties properties) throws PropertyVetoException,
            TransactionFailure {
        CustomResource newResource = createConfigBean(param, properties);
        param.getResources().add(newResource);
        return newResource;
    }

    private CustomResource createConfigBean(Resources param, Properties properties) throws PropertyVetoException,
            TransactionFailure {
        CustomResource newResource = param.createChild(CustomResource.class);
        newResource.setJndiName(jndiName);
        newResource.setFactoryClass(factoryClass);
        newResource.setResType(resType);
        newResource.setEnabled(enabled);
        if (description != null) {
            newResource.setDescription(description);
        }
        if (properties != null) {
            for (Map.Entry e : properties.entrySet()) {
                Property prop = newResource.createChild(Property.class);
                prop.setName((String) e.getKey());
                prop.setValue((String) e.getValue());
                newResource.getProperty().add(prop);
            }
        }
        return newResource;
    }

    public Resource createConfigBean(Resources resources, HashMap attributes, Properties properties) throws Exception{
        setAttributes(attributes, null);
        return createConfigBean(resources, properties);
    }
}
