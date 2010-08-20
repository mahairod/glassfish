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

package amxtest;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.glassfish.admin.amx.core.*;
import org.glassfish.admin.amx.base.*;
import org.glassfish.admin.amx.intf.config.*;
import org.glassfish.admin.amx.intf.config.grizzly.*;
import org.glassfish.admin.amx.monitoring.*;
import org.glassfish.admin.amx.j2ee.*;
import org.glassfish.admin.amx.util.CollectionUtil;
import org.glassfish.admin.amx.util.ExceptionUtil;
import org.glassfish.admin.amx.logging.Logging;


/** Maps types to interface to maximize explicit testing by introspecting the interface */
class Interfaces {
    private final Map<String, Class<? extends AMXProxy>> mInterfaces;
    public Interfaces()
    {
        mInterfaces = new HashMap<String, Class<? extends AMXProxy>>();
        
        add(
            DomainRoot.class,
            Ext.class,
            Pathnames.class,
            Query.class,
            BulkAccess.class,
            Realms.class,
            RuntimeRoot.class,
            SystemInfo.class,
            Sample.class,
            Tools.class,
            SystemStatus.class,
            ConnectorRuntimeAPIProvider.class,
            Logging.class,
            MonitoringRoot.class,
            ServerMon.class,
            
            // config MBeans
            AmxPref.class,
            AccessLog.class,
            Action.class,
            AdminObjectResource.class,
            AdminService.class,
            Application.class,
            ApplicationConfig.class,
            ApplicationRef.class,
            ApplicationRefReferent.class,
            Applications.class,
            AuditModule.class,
            AuthRealm.class,
            AvailabilityService.class,
            BackendPrincipal.class,
            Cluster.class,
            ClusterRef.class,
            ClusterRefConfigReferent.class,
            ClusteredServer.class,
            Clusters.class,
            Config.class,
            ConfigElement.class,
            ConfigTools.class,
            Configs.class,
            ConnectorConnectionPool.class,
            ConnectorResource.class,
            ConnectorService.class,
            ContextParam.class,
            CustomResource.class,
            DasConfig.class,
            Domain.class,
            EjbContainer.class,
            EjbContainerAvailability.class,
            EjbTimerService.class,
            Engine.class,
            EnvEntry.class,
            Event.class,
            GroupManagementService.class,
            HttpAccessLog.class,
            HealthChecker.class,
            HttpService.class,
            IiopListener.class,
            IiopService.class,

            JaccProvider.class,
            JdbcConnectionPool.class,
            org.glassfish.admin.amx.intf.config.JdbcResource.class,
            JmsAvailability.class,
            JmsHost.class,
            JmsService.class,
            JmxConnector.class,
            org.glassfish.admin.amx.intf.config.JndiResource.class,
            JavaConfig.class,
            KeepAlive.class,
            LogService.class,
            MdbContainer.class,
            MailResource.class,
            ManagementRule.class,
            ManagementRules.class,
            ManagerProperties.class,
            MessageSecurityConfig.class,
            Module.class,
            ModuleLogLevels.class,
            ModuleMonitoringLevels.class,
            MonitoringService.class,
            NodeAgent.class,
            NodeAgents.class,
            Orb.class,
            Profiler.class,
            Property.class,
            ProviderConfig.class,
            RegistryLocation.class,
            RequestPolicy.class,
            Resource.class,
            org.glassfish.admin.amx.intf.config.ResourceAdapter.class,
            ResourceRef.class,
            ResourceRefReferent.class,
            Resources.class,
            ResponsePolicy.class,

            SecurityMap.class,
            SecurityService.class,
            Server.class,
            ServerRef.class,
            Servers.class,
            SessionConfig.class,
            SessionManager.class,
            SessionProperties.class,
            StandaloneServer.class,
            StoreProperties.class,
            SystemApplications.class,
            SystemProperty.class,
            ThreadPool.class,
            ThreadPools.class,
            TransactionService.class,
            TransformationRule.class,
            VirtualServer.class,
            WebContainer.class,
            WebContainerAvailability.class,
            WebServiceEndpoint.class,
            
            // grizzly
            FileCache.class,
            Http.class,
            NetworkConfig.class,
            NetworkListener.class,
            NetworkListeners.class,
            PortUnification.class,
            Protocol.class,
            ProtocolChain.class,
            ProtocolChainInstanceHandler.class,
            ProtocolFilter.class,
            ProtocolFinder.class,
            Protocols.class,
            SelectionKeyHandler.class,
            Ssl.class,
            Transport.class,
            Transports.class
        );
    }

        public final void
    add( final Class<? extends AMXProxy>...  args )
    {
        for( final Class<? extends AMXProxy> clazz : args )
        {
            add( clazz );
        }
    }
    
    public final void add( final Class<? extends AMXProxy> clazz )
    {
        final String type = Util.deduceType(clazz);
        if ( mInterfaces.get(type) != null )
        {
            throw new IllegalArgumentException("Interface already exists for type " + type );
        }
        
        mInterfaces.put( type, clazz );
    }
    
    public List<Class<? extends AMXProxy>> all()
    {
        return new ArrayList( mInterfaces.values() );
    }
    
    public Class<? extends AMXProxy> get(final String type)
    {
        Class<? extends AMXProxy> intf = mInterfaces.get(type);
        
        if ( intf == null )
        {
            // a type is not required to have an interface
            //System.out.println( "No AMXProxy interface for type: " + type + " (undesirable, but OK)" );
            intf = AMXProxy.class;
        }
        
        return intf;
    }
}


































