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

package org.glassfish.api.monitoring;

import org.jvnet.hk2.config.Attribute;
import org.jvnet.hk2.config.Configured;
import org.jvnet.hk2.config.ConfigBeanProxy;

import javax.validation.constraints.NotNull;
import java.beans.PropertyVetoException;
import org.jvnet.hk2.config.Configured;
import org.glassfish.api.admin.config.Named;

/**
 * Default monitoring configuration for containers. Containers can provide their
 * configuration through the ContainerMonitoring interface.
 *
 * @author Nandini Ektare
 */
@Configured
public interface ContainerMonitoring extends ConfigBeanProxy, Named {

    public static final String LEVEL_OFF = "OFF";
    public static final String LEVEL_LOW = "LOW";
    public static final String LEVEL_HIGH = "HIGH";

    public static final String CONNECTOR_CONNECTION_POOL = "connector-connection-pool";
    public static final String CONNECTOR_SERVICE = "connector-service";
    public static final String EJB_CONTAINER = "ejb-container";
    public static final String HTTP_SERVICE = "http-service";
    public static final String JDBC_CONNECTION_POOL = "jdbc-connection-pool";
    public static final String JMS_SERVICE = "jms-service";
    public static final String JVM = "jvm";
    public static final String ORB = "orb";
    public static final String THREAD_POOL = "thread-pool";
    public static final String TRANSACTION_SERVICE = "transaction-service";
    public static final String WEB_CONTAINER = "web-container";
    public static final String SECURITY = "security";
    public static final String WEB_SERVICES_CONTAINER = "web-services-container";
    public static final String JPA = "jpa";
    public static final String JERSEY = "jersey";

    /**
     * The monitoring level of this monitoring item 
     * @return String with values HIGH/LOW/OFF
     */
    @Attribute (defaultValue="OFF")
    @NotNull
    public String getLevel();

    /**
     * Set the level of this monitoring module
     * @param level new monitoring level
     */

    public void setLevel(String level) throws PropertyVetoException;

}
