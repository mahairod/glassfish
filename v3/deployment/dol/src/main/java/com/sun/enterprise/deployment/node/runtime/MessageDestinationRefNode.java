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

package com.sun.enterprise.deployment.node.runtime;

import com.sun.enterprise.deployment.MessageDestinationReferenceDescriptor;
import com.sun.enterprise.deployment.node.DeploymentDescriptorNode;
import com.sun.enterprise.deployment.node.XMLElement;
import com.sun.enterprise.deployment.node.runtime.web.WebBundleRuntimeNode;
import com.sun.enterprise.deployment.types.MessageDestinationReferenceContainer;
import com.sun.enterprise.deployment.util.DOLUtils;
import com.sun.enterprise.deployment.xml.RuntimeTagNames;
import org.w3c.dom.Node;

import java.util.Iterator;
import java.util.Map;

/**
 * This node is responsible for handling runtime descriptor
 * message-destination-ref tag
 *
 */
public class MessageDestinationRefNode extends DeploymentDescriptorNode {

    private MessageDestinationReferenceDescriptor descriptor;
    
       /**
    * @return the descriptor instance to associate with this XMLNode
    */    
    public Object getDescriptor() {
        return descriptor;
    }   
    
    /**
     * all sub-implementation of this class can use a dispatch table to map 
     * xml element to method name on the descriptor class for setting the 
     * element value. 
     *  
     * @return the map with the element name as a key, the setter method 
     * as a value
     */    
    protected Map getDispatchTable() {    
        Map table = super.getDispatchTable();
        table.put(RuntimeTagNames.JNDI_NAME, "setJndiName");
        return table;
    }
    
    /**
     * receives notiification of the value for a particular tag
     * 
     * @param element the xml element
     * @param value it's associated value
     */
    public void setElementValue(XMLElement element, String value) {
        if (RuntimeTagNames.MESSAGE_DESTINATION_REFERENCE_NAME.equals(
            element.getQName())) {
            Object parentNode = getParentNode();
            Object parentDesc = null;
            // in case of web
            if (parentNode instanceof WebBundleRuntimeNode) {
                parentDesc = ((WebBundleRuntimeNode) parentNode).getWebBundleDescriptor();
            // in case of appclient and ejb
            } else {
                parentDesc = getParentNode().getDescriptor();
            }

            if (parentDesc instanceof MessageDestinationReferenceContainer) {
                try {
                    descriptor = ((MessageDestinationReferenceContainer) parentDesc).getMessageDestinationReferenceByName(value);
                } catch (IllegalArgumentException iae) {
                    DOLUtils.getDefaultLogger().warning(iae.getMessage());
                }

            }
        } else super.setElementValue(element, value);
    }
    
    /**
     * write the descriptor class to a DOM tree and return it
     *
     * @param parent node for the DOM tree
     * @param node name for the descriptor
     * @param the descriptor to write
     * @return the DOM tree top node
     */    
    public Node writeDescriptor(Node parent, String nodeName, 
        MessageDestinationReferenceDescriptor msgDestRef) {          
        Node msgDestRefNode = super.writeDescriptor(parent, nodeName, 
            msgDestRef);
        appendTextChild(msgDestRefNode, 
            RuntimeTagNames.MESSAGE_DESTINATION_REFERENCE_NAME, 
            msgDestRef.getName());
        appendTextChild(msgDestRefNode, RuntimeTagNames.JNDI_NAME, 
            msgDestRef.getJndiName());
        return msgDestRefNode;
    }  
    
    /**
     * writes all the runtime information for JMS destination references
     * 
     * @param parent node to add the runtime xml info
     * @param the J2EE component containing message destination references
     */        
    public static void writeMessageDestinationReferences(Node parent, 
        MessageDestinationReferenceContainer descriptor) {
        // message-destination-ref*
        Iterator msgDestRefs = 
            descriptor.getMessageDestinationReferenceDescriptors().iterator();
        if (msgDestRefs.hasNext()) {
            MessageDestinationRefNode messageDestinationRefNode = 
                new MessageDestinationRefNode();
            while (msgDestRefs.hasNext()) {
                messageDestinationRefNode.writeDescriptor(parent, 
                    RuntimeTagNames.MESSAGE_DESTINATION_REFERENCE, 
                    (MessageDestinationReferenceDescriptor) msgDestRefs.next());
            }
        }       
    }
}
