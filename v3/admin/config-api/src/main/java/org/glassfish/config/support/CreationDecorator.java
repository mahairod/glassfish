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

package org.glassfish.config.support;

import org.glassfish.api.admin.AdminCommandContext;
import org.jvnet.hk2.annotations.Scoped;
import org.jvnet.hk2.annotations.Service;
import org.jvnet.hk2.component.PerLookup;
import org.jvnet.hk2.config.ConfigBeanProxy;
import org.jvnet.hk2.config.TransactionFailure;

import java.beans.PropertyVetoException;

/**
 * An element decorator decorates a newly added configuration element, usually added
 * through the generic create command implementation.
 *
 * @author Jerome Dochez
 */
@Scoped(PerLookup.class)
public interface CreationDecorator<T extends ConfigBeanProxy> {

    /**
     * The element instance has been created and added to the parent, it can be
     * customized. This method is called within a {@link org.jvnet.hk2.config.Transaction}
     * and instance is therefore a writeable view on the configuration component.
     *
     * @param context administration command context
     * @param instance newly created configuration element
     * @throws TransactionFailure if the transaction should be rollbacked
     * @throws PropertyVetoException if one of the listener of <T> is throwing a veto exception
     */
    public void decorate(AdminCommandContext context, T instance) throws TransactionFailure, PropertyVetoException;

    /**
     * Default implementation of a decorator that does nothing.
     */
    @Service
    public class NoDecoration implements CreationDecorator<ConfigBeanProxy> {
        @Override
        public void decorate(AdminCommandContext context, ConfigBeanProxy instance) throws TransactionFailure, PropertyVetoException {
            // do nothing
        }
    }
}
