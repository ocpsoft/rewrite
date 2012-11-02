/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.jsfunit.arquillian.client;

import java.net.URL;

import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.arquillian.test.spi.TestEnricher;
import org.jboss.jsfunit.arquillian.container.JSFUnitCleanupTestTreadFilter;
import org.jboss.jsfunit.arquillian.container.JSFUnitRemoteExtension;
import org.jboss.jsfunit.arquillian.container.JSFUnitTestEnricher;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

/**
 * JSFUnitArchiveAppender
 * 
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @author <a href="http://community.jboss.org/people/spinner)">Jose Rodolfo freitas</a>
 * @author Stan Silvert
 * @version $Revision: $
 */
public class JSFUnitArchiveAppender implements AuxiliaryArchiveAppender {
    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.arquillian.spi.AuxiliaryArchiveAppender#createAuxiliaryArchive()
     */
    public Archive<?> createAuxiliaryArchive() {
        return ShrinkWrap
                .create(JavaArchive.class, "arquillian-jsfunit.jar")
                .addClass(JSFUnitCleanupTestTreadFilter.class)
                .addPackages(
                        true,
                        org.jboss.arquillian.container.spi.client.protocol.metadata.HTTPContext.class.getPackage(),
                        org.jboss.arquillian.protocol.servlet.Processor.class.getPackage(),
                        org.jboss.jsfunit.jsfsession.JSFSession.class.getPackage(),
                        org.jboss.jsfunit.framework.WebClientSpec.class.getPackage(),
                        org.jboss.jsfunit.context.JSFUnitFacesContext.class.getPackage(),
                        org.jboss.jsfunit.seam.SeamUtil.class.getPackage(),
                        org.jboss.jsfunit.api.JSFUnitResource.class.getPackage(), // Arquillian JSFunit API
                        org.jboss.jsfunit.arquillian.container.JSFUnitTestEnricher.class.getPackage(), // Support package for
                                                                                                       // incontainer enrichment
                        org.apache.http.HttpEntity.class.getPackage(), // HTTPClient
                        org.apache.james.mime4j.MimeException.class.getPackage(), // Apache Mime4j, used by HTTP client
                        com.gargoylesoftware.htmlunit.BrowserVersion.class.getPackage(),
                        org.apache.commons.codec.Decoder.class.getPackage(), org.apache.commons.io.IOUtils.class.getPackage(),
                        org.apache.commons.lang.StringUtils.class.getPackage(),
                        net.sourceforge.htmlunit.corejs.javascript.EvaluatorException.class.getPackage(),
                        org.w3c.css.sac.CSSException.class.getPackage(),
                        com.steadystate.css.dom.CSSOMObject.class.getPackage(),
                        com.steadystate.css.parser.CSSOMParser.class.getPackage(),
                        com.steadystate.css.sac.TestCSSParseException.class.getPackage(),
                        com.steadystate.css.userdata.UserDataConstants.class.getPackage(),

                        org.apache.commons.logging.LogFactory.class.getPackage(),
                        org.apache.xerces.xni.XNIException.class.getPackage(),
                        org.apache.commons.collections.Transformer.class.getPackage(),
                        org.apache.xerces.dom.AttrImpl.class.getPackage(), org.apache.xerces.impl.Constants.class.getPackage(),
                        org.apache.xerces.jaxp.DocumentBuilderFactoryImpl.class.getPackage(),
                        org.apache.xerces.parsers.AbstractDOMParser.class.getPackage(),
                        org.apache.xerces.util.AttributesProxy.class.getPackage(),
                        org.apache.xerces.xinclude.MultipleScopeNamespaceSupport.class.getPackage(),
                        org.apache.xerces.xpointer.XPointerHandler.class.getPackage(),
                        org.apache.xerces.xs.AttributePSVI.class.getPackage(),
                        
                        org.apache.xml.dtm.Axis.class.getPackage(),
                        org.apache.xml.res.XMLErrorResources.class.getPackage(),
                        org.apache.xml.utils.AttList.class.getPackage(),
                        org.apache.xpath.XPath.class.getPackage(),
                        org.apache.xalan.Version.class.getPackage(),


                        org.cyberneko.html.HTMLComponent.class.getPackage(), org.cyberneko.html.HTMLEntities.class.getPackage())
//                .addAsResource("com/gargoylesoftware/htmlunit/javascript/configuration/FF2.properties")
                .addAsResource("com/gargoylesoftware/htmlunit/javascript/configuration/FF3.properties")
                .addAsResource("com/gargoylesoftware/htmlunit/javascript/configuration/FF3.6.properties")
                .addAsResource("com/gargoylesoftware/htmlunit/javascript/configuration/IE6.properties")
                .addAsResource("com/gargoylesoftware/htmlunit/javascript/configuration/IE7.properties")
                .addAsResource("com/gargoylesoftware/htmlunit/javascript/configuration/IE8.properties")
                .addAsResource("com/gargoylesoftware/htmlunit/javascript/configuration/JavaScriptConfiguration.xml")
                .addAsResource("com/gargoylesoftware/htmlunit/javascript/configuration/JavaScriptConfiguration.xsd")
                .addAsResource("net/sourceforge/htmlunit/corejs/javascript/resources/Messages.properties")
                .addAsResource("net/sourceforge/htmlunit/corejs/javascript/resources/Messages_fr.properties")
                .addAsResource("org/cyberneko/html/res/HTMLlat1.properties")
                .addAsResource("org/cyberneko/html/res/HTMLspecial.properties")
                .addAsResource("org/cyberneko/html/res/HTMLsymbol.properties")
                .addAsResource("org/cyberneko/html/res/XMLbuiltin.properties")
                .addAsResource("com/steadystate/css/parser/SACParserMessages.properties")
                .addAsResource("com/steadystate/css/parser/SACParserMessages_en.properties")
                .addAsResource("com/steadystate/css/parser/SACParserMessages_de.properties")
                /**
                 * TODO use faces-config and web-fragment from jsfunit jar. for now it's not possible because shrinkwrap could
                 * get a different META-INF/faces-config .addAsManifestResource(this.jsfunitFacesConfigXml(),
                 * "faces-config.xml")
                 */
                .addAsManifestResource("org/jboss/arquillian/jsfunit/internals/faces-config.xml", "faces-config.xml")
                .addAsManifestResource("org/jboss/arquillian/jsfunit/internals/web-fragment.xml", "web-fragment.xml")
                .addAsServiceProvider(TestEnricher.class, JSFUnitTestEnricher.class)
                .addAsServiceProvider(RemoteLoadableExtension.class, JSFUnitRemoteExtension.class);
    }

    private URL jsfunitFacesConfigXml() {
        return org.jboss.jsfunit.context.JSFUnitFacesContext.class.getResource("/META-INF/faces-config.xml");
    }
}
