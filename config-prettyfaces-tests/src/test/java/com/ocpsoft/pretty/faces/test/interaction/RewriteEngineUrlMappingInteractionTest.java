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
package com.ocpsoft.pretty.faces.test.interaction;

import static org.junit.Assert.assertTrue;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.jsfunit.jsfsession.JSFClientSession;
import org.jboss.jsfunit.jsfsession.JSFSession;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ocpsoft.pretty.faces.test.PrettyFacesTestBase;

@RunWith(Arquillian.class)
public class RewriteEngineUrlMappingInteractionTest
{
   @Deployment
   public static WebArchive createDeployment()
   {
      return PrettyFacesTestBase.createDeployment()
               .addClass(InteractionDynaViewBean.class)
               .addAsWebResource("interaction/interaction-page.xhtml", "page.xhtml")
               .addAsWebInfResource("interaction/interaction-pretty-config.xml", "pretty-config.xml");
   }

   /**
    * Accessing the page using the URL mapping
    */
   @Test
   public void testSimpleUrlMapping() throws Exception
   {
      JSFSession jsfSession = new JSFSession("/page");
      JSFClientSession client = jsfSession.getJSFClientSession();
      assertTrue(client.getContentPage().getUrl().toString().endsWith("/page"));
      assertTrue(client.getPageAsText().contains("The page rendered fine!"));
   }

   /**
    * Accessing the page using a dynaview
    */
   @Test
   public void testDynaViewUrlMapping() throws Exception
   {
      JSFSession jsfSession = new JSFSession("/dyna/page");
      JSFClientSession client = jsfSession.getJSFClientSession();
      assertTrue(client.getContentPage().getUrl().toString().endsWith("/dyna/page"));
      assertTrue(client.getPageAsText().contains("The page rendered fine!"));
   }
   
   /**
    * Rewrite rule forwards to the URL mapping
    */
   @Test
   public void testRewriteForwardsToUrlMapping() throws Exception
   {
      JSFSession jsfSession = new JSFSession("/rewrite-forwards-to-page-mapping");
      JSFClientSession client = jsfSession.getJSFClientSession();
      assertTrue(client.getContentPage().getUrl().toString().endsWith("/rewrite-forwards-to-page-mapping"));
      assertTrue(client.getPageAsText().contains("The page rendered fine!"));
   }

   /**
    * Rewrite rule redirects to the URL mapping
    */
   @Test
   public void testRewriteRedirectsToUrlMapping() throws Exception
   {
      JSFSession jsfSession = new JSFSession("/rewrite-redirects-to-page-mapping");
      JSFClientSession client = jsfSession.getJSFClientSession();
      assertTrue(client.getContentPage().getUrl().toString().endsWith("/page"));
      assertTrue(client.getPageAsText().contains("The page rendered fine!"));
   }

   /**
    * Rewrite rule forwards to the dynaview
    */
   @Test
   public void testRewriteForwardsToDynaviewMapping() throws Exception
   {
      JSFSession jsfSession = new JSFSession("/rewrite-forwards-to-dynaview");
      JSFClientSession client = jsfSession.getJSFClientSession();
      assertTrue(client.getContentPage().getUrl().toString().endsWith("/rewrite-forwards-to-dynaview"));
      assertTrue(client.getPageAsText().contains("The page rendered fine!"));
   }

   /**
    * Rewrite rule redirects to the dynaview
    */
   @Test
   public void testRewriteRedirectsToDynaviewMapping() throws Exception
   {
      JSFSession jsfSession = new JSFSession("/rewrite-redirects-to-dynaview");
      JSFClientSession client = jsfSession.getJSFClientSession();
      assertTrue(client.getContentPage().getUrl().toString().endsWith("/dyna/page"));
      assertTrue(client.getPageAsText().contains("The page rendered fine!"));
   }
   
   /**
    * Directly accessing the view-id should redirect to the pretty URL
    */
   @Test
   public void testJsfViewIdRedirectsToMapping() throws Exception
   {
      JSFSession jsfSession = new JSFSession("/page.jsf");
      JSFClientSession client = jsfSession.getJSFClientSession();
      assertTrue(client.getContentPage().getUrl().toString().endsWith("/page"));
      assertTrue(client.getPageAsText().contains("The page rendered fine!"));
   }

}
