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

package org.glassfish.appclient.server.core.jws;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import org.glassfish.api.deployment.archive.ReadableArchive;
import org.glassfish.appclient.server.core.AppClientDeployerHelper;
import org.glassfish.appclient.server.core.jws.servedcontent.DynamicContent;
import org.glassfish.appclient.server.core.jws.servedcontent.StaticContent;
import org.glassfish.appclient.server.core.jws.servedcontent.TokenHelper;
import org.jvnet.hk2.annotations.Inject;
import org.jvnet.hk2.annotations.Scoped;
import org.jvnet.hk2.annotations.Service;
import org.jvnet.hk2.component.PerLookup;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Processes developer-provided content in (or directly or indirectly
 * referenced from) an optional JNLP file included in the client or the EAR.
 *
 * @author tjquinn
 */
@Service
@Scoped(PerLookup.class)
public class DeveloperContentHandler {

    @Inject
    DeveloperContentService dcs;

    private ClassLoader loader;
    private ReadableArchive appClientArchive;
    private Map<String,StaticContent> staticContent;
    private Map<String,DynamicContent> dynamicContent;
    private TokenHelper tHelper;
    private File appRootDir;
    private URI appRootURI;

    private LSSerializer lsSerializer = null;
    private LSOutput lsOutput = null;

    private static DocumentBuilderFactory dbf = documentBuilderFactory();
    private static DocumentBuilder db = documentBuilder();

    private Document developerDOM = null;
    private boolean noDeveloperDOM = false;

    private AppClientDeployerHelper helper;

    public void init(
            final ClassLoader loader,
            final TokenHelper tHelper,
            final File appRootDir,
            final ReadableArchive appClientArchive,
            final Map<String,StaticContent> staticContent,
            final Map<String,DynamicContent> dynamicContent,
            final AppClientDeployerHelper helper) {

        this.loader = loader;
        this.tHelper = tHelper;
        this.appRootDir = appRootDir;
        this.appRootURI = appRootDir.toURI();
        this.appClientArchive = appClientArchive;
        this.staticContent = staticContent;
        this.dynamicContent = dynamicContent;
        this.helper = helper;
     }

    /**
     * Combines the developer-provided JNLP in the client with the JNLP
     * generated by the server.
     *
     * @param generatedJNLPTemplate JNLP generated by the server
     * @return combined JNLP; if the developer provided no customized JNLP then
     * the generated JNLP, unchanged
     */
    String combineJNLP(
            final String generatedJNLPTemplate,
            final String developerJNLP) {

        final Document devDOM;
        try {
            devDOM = developerDOMFromPath(developerJNLP);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        if (devDOM == null) {
            return generatedJNLPTemplate;
        }

        /*
         * Get the generated main JNLP document.
         */
        final InputSource generatedJNLPSource = new InputSource(
                new StringReader(generatedJNLPTemplate));

        /*
         * The result document starts as the developer-provided document.  Then
         * override the parts that the server insists on providing itself,
         * then merge in other parts that the server wants to add to
         * whatever the developer provided there.
         */
        Document generatedJNLPDOM;
        try {
            generatedJNLPDOM = db.parse(generatedJNLPSource);

            /*
             * Each CombinedXPath object knows how to combine the generated and
             * the developer-provided content whether defaulted, overridden, or
             * merged.
             */
            for (CombinedXPath combinedXPath : dcs.xPathsToCombinedContent()) {
                combinedXPath.process(devDOM, generatedJNLPDOM);
            }
            return toXML(generatedJNLPDOM);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private synchronized Document developerDOMFromPath(final String devJNLPDoc) throws SAXException, IOException {
        Document result = null;
        if (devJNLPDoc != null) {
            final InputStream devJNLPStream = JavaWebStartInfo.openEntry(appClientArchive, devJNLPDoc);
            if (devJNLPStream != null) {
                result = db.parse(devJNLPStream);
            }
        }
        return result;
    }

    private synchronized Document developerDOMFromContent(final String devContent) throws SAXException, IOException {
        final InputSource is = new InputSource(new StringReader(devContent));
        Document result = db.parse(is);
        return result;
    }

    private String toXML(final Document dom)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        Writer writer = new StringWriter();
        writeXML(dom, writer);
        return writer.toString();
    }

    private synchronized void writeXML(final Node node, final Writer writer)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (lsSerializer == null) {
            final DOMImplementation domImpl = DOMImplementationRegistry.newInstance().
                    getDOMImplementation("");
            final DOMImplementationLS domLS = (DOMImplementationLS) domImpl.getFeature("LS", "3.0");
            lsOutput = domLS.createLSOutput();
            lsOutput.setEncoding("UTF-8");
            lsSerializer = domLS.createLSSerializer();
        }
        lsOutput.setCharacterStream(writer);
        lsSerializer.write(node, lsOutput);
    }

    /**
     * Adds all developer-provided content that falls within the code base to
     * the static or dynamic content.
     * <p>
     * We need to do this so that the Grizzly adapter that serves the content
     * knows that it is OK to serve this content.  Otherwise a hostile user or
     * app could conduct "fishing expeditions" for content on the server that
     * should not be exposed simply by using the Java Web Start-related URLs
     * and varying the path part to browse for files.
     */
    void addDeveloperContentFromPath(final String devJNLPDocPath) {
        /*
         * There is no work to do unless the developer specified a JNLP
         * document.
         */
        if (devJNLPDocPath == null || (devJNLPDocPath.length() == 0)) {
            return;
        }

        final Document devDOM;
        try {
            devDOM = developerDOMFromPath(devJNLPDocPath);
            addDeveloperContent(devJNLPDocPath, devDOM);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    void addDeveloperContent(final String devJNLPDocPath, final String devJNLP) {
        try {
            final Document devDOM = developerDOMFromContent(devJNLP);
            addDeveloperContent(devJNLPDocPath, devDOM);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void addDeveloperContent(
            final String contentPath,
            final Document devDOM) throws XPathExpressionException, URISyntaxException, IOException {
        /*
         * Search for hrefs to other content.  Add each that falls within
         * the codebase to the relevant content.
         */
        final URI codebaseURI = new URI(tHelper.appCodebasePath());
        for (XPathToDeveloperProvidedContentRefs c : dcs.xPathsToDevContentRefs()) {
            NodeList nodes = (NodeList) c.xPathExpr().evaluate(devDOM, XPathConstants.NODESET);
            if (nodes.getLength() > 0) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    final String href = nodes.item(i).getNodeValue();
                    /*
                     * Tokens have not been substituted at this point in the processing,
                     * and developer-provided content should not use tokens for
                     * hrefs.  So don't process an href starting with ${.
                     */
                    if ( ! href.startsWith("${")) {
                        c.addToContentIfInApp(this, helper, contentPath,
                                codebaseURI, href, loader, staticContent,
                                dynamicContent, appRootURI, appClientArchive);
                    }
                }
            }
        }
    }

    private static DocumentBuilderFactory documentBuilderFactory() {
        final DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        try {
            /*
             * Turn off deferred expansion or the adoptNode method - which
             * we use to migrate parts of the generated document into the
             * result document - will copy the unexpanded content!
             */
            f.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", false);
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException(ex);
        }
        return f;
    }

    private static DocumentBuilder documentBuilder() {
        try {
            final DocumentBuilder b = dbf.newDocumentBuilder();
            return b;
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException(ex);
        }
    }

}
