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
package com.ocpsoft.pretty.faces.test.encoding;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.faces.context.FacesContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.jsfunit.jsfsession.JSFClientSession;
import org.jboss.jsfunit.jsfsession.JSFServerSession;
import org.jboss.jsfunit.jsfsession.JSFSession;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.test.PrettyFacesTestBase;

@RunWith(Arquillian.class)
public class URLEncodingTest extends PrettyFacesTestBase
{
   @Deployment
   public static WebArchive getDeployment()
   {
      return PrettyFacesTestBase.getDeployment()
               .addClass(EncodingBean.class)
               .addAsWebResource("encoding/encoding.xhtml", "encoding.xhtml")
               .addAsWebInfResource("encoding/encoding-pretty-config.xml", "pretty-config.xml");
   }

   /**
    * Test a rewrite rule using the 'substitute' attribute to modify the URL.
    * 
    * @see http://code.google.com/p/prettyfaces/issues/detail?id=76
    */
   @Test
   public void testRewriteEncodingSubstitute() throws Exception
   {
      String target = "/virtual/rewrite/substitute";
      String expected = "/virtuální";

      JSFSession jsfSession = new JSFSession(target);
      JSFServerSession server = jsfSession.getJSFServerSession();

      JSFClientSession client = jsfSession.getJSFClientSession();
      String action = client.getElement("form").getAttribute("action");

      FacesContext context = server.getFacesContext();
      PrettyContext prettyContext =
               PrettyContext.getCurrentInstance(context);

      assertEquals(expected, prettyContext.getRequestURL().toString());
      assertEquals(prettyContext.getContextPath() + expected, action);
   }

   /**
    * Test a rewrite rule using the 'url' attribute to create a completely new URL.
    * 
    * @see http://code.google.com/p/prettyfaces/issues/detail?id=76
    */
   @Test
   public void testRewriteEncodingUrl() throws Exception
   {
      String target = "/virtual/rewrite/url";
      String expected = "/virtuální";

      JSFSession jsfSession = new JSFSession(target);
      JSFServerSession server = jsfSession.getJSFServerSession();

      JSFClientSession client = jsfSession.getJSFClientSession();
      String action = client.getElement("form").getAttribute("action");

      FacesContext context = server.getFacesContext();
      PrettyContext prettyContext =
               PrettyContext.getCurrentInstance(context);

      assertEquals(expected, prettyContext.getRequestURL().toString());
      assertEquals(prettyContext.getContextPath() + expected, action);
   }

   @Test
   public void testPrettyFacesFormActionURLEncodesProperly() throws Exception
   {
      String expected = "/custom/form";

      JSFSession jsfSession = new JSFSession(expected);
      JSFServerSession server = jsfSession.getJSFServerSession();

      JSFClientSession client = jsfSession.getJSFClientSession();
      String action = client.getElement("form").getAttribute("action");

      FacesContext context = server.getFacesContext();
      PrettyContext prettyContext = PrettyContext.getCurrentInstance(context);

      assertEquals(expected, prettyContext.getRequestURL().toString());
      assertEquals(prettyContext.getContextPath() + expected, action);
   }

   @Test
   // http://code.google.com/p/prettyfaces/issues/detail?id=64
   public void testPrettyFacesFormActionURLEncodesProperlyWithCustomRegexAndMultiplePathSegments() throws Exception
   {
      String expected = "/foo/bar/baz/car/";

      JSFSession jsfSession = new JSFSession(expected);
      JSFServerSession server = jsfSession.getJSFServerSession();

      JSFClientSession client = jsfSession.getJSFClientSession();
      String action = client.getElement("form").getAttribute("action");

      FacesContext context = server.getFacesContext();
      PrettyContext prettyContext = PrettyContext.getCurrentInstance(context);

      assertEquals(expected, prettyContext.getRequestURL().toString());
      assertEquals(prettyContext.getContextPath() + expected, action);

      String value = (String) server.getManagedBeanValue("#{encodingBean.pathText}");
      assertEquals("foo/bar/baz/car", value);
   }

   @Test
   public void testNonMappedRequestRendersRewrittenURL() throws Exception
   {
      String expected = "/custom/form";

      JSFSession jsfSession = new JSFSession("/encoding.jsf");
      JSFServerSession server = jsfSession.getJSFServerSession();

      JSFClientSession client = jsfSession.getJSFClientSession();
      String action = client.getElement("form").getAttribute("action");

      FacesContext context = server.getFacesContext();
      PrettyContext prettyContext = PrettyContext.getCurrentInstance(context);

      assertEquals("/encoding.jsf", prettyContext.getRequestURL().toString());
      assertEquals(prettyContext.getContextPath() + expected, action);
   }

   @Test
   public void testURLDecoding() throws Exception
   {
      JSFSession jsfSession = new JSFSession("/encoding/Vračar?dis=Fooo Bar");
      JSFServerSession server = jsfSession.getJSFServerSession();

      assertEquals("/encoding.xhtml", server.getCurrentViewID());

      // Test a managed bean
      Object val1 = server.getManagedBeanValue("#{encodingBean.pathText}");
      assertEquals("Vračar", val1);
   }

   @Test
   public void testQueryDecoding() throws Exception
   {
      JSFSession jsfSession = new JSFSession("/encoding/Vračar?dis=Fooo%20Bar");
      JSFServerSession server = jsfSession.getJSFServerSession();

      assertEquals("/encoding.xhtml", server.getCurrentViewID());

      Object val2 = server.getManagedBeanValue("#{encodingBean.queryText}");
      assertEquals("Fooo Bar", val2);
   }

   @Test
   public void testQueryWithGermanUmlaut() throws Exception
   {

      // query parameter contains a German 'ü' encoded with UTF8
      JSFSession jsfSession = new JSFSession("/encoding/Vračar?dis=%C3%BC");
      JSFServerSession server = jsfSession.getJSFServerSession();

      assertEquals("/encoding.xhtml", server.getCurrentViewID());

      Object val2 = server.getManagedBeanValue("#{encodingBean.queryText}");
      assertEquals("\u00fc", val2);
   }

   @Test
   public void testPatternDecoding() throws Exception
   {
      JSFSession jsfSession = new JSFSession("/hard encoding/Vračar");
      JSFServerSession server = jsfSession.getJSFServerSession();

      String currentViewID = server.getCurrentViewID();
      assertEquals("/encoding.xhtml", currentViewID);
   }

   @Test
   public void testEncodedURLMatchesNonEncodedPattern() throws IOException
   {
      new JSFSession("/URL%20ENCODED");

      PrettyContext prettyContext = PrettyContext.getCurrentInstance();
      assertEquals(prettyContext.getRequestURL().toURL(), "/url decoded");
   }

   public void testNoDecodeOnSubmitDoesNotCrash() throws Exception
   {
      JSFSession jsfSession = new JSFSession("/decodequery");
      JSFServerSession server = jsfSession.getJSFServerSession();
      JSFClientSession client = jsfSession.getJSFClientSession();
      assertEquals("encoding.jsf", server.getCurrentViewID());

      client.setValue("input1", "%");
      client.click("submit");
   }
}
