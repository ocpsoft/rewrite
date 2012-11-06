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
package com.ocpsoft.pretty.faces.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.faces.context.FacesContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.jsfunit.framework.Environment;
import org.jboss.jsfunit.jsfsession.JSFServerSession;
import org.jboss.jsfunit.jsfsession.JSFSession;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ocpsoft.pretty.PrettyContext;

@RunWith(Arquillian.class)
public class PrettyContextTest
{
   @Deployment
   public static WebArchive createDeployment()
   {
      return PrettyFacesTestBase.createDeployment()
               .addAsWebResource("basic/index.xhtml", "index.xhtml")
               .addAsWebInfResource("pretty-config.xml");
   }

   @Test
   public void testEnvironment() throws Exception
   {
      assertTrue(Environment.is12Compatible());
      assertTrue(Environment.is20Compatible());
      assertEquals(2, Environment.getJSFMajorVersion());
      assertEquals(0, Environment.getJSFMinorVersion());
   }

   @Test
   public void testPrettyFacesIndexPage() throws Exception
   {
      JSFSession jsfSession = new JSFSession("/");
      JSFServerSession server = jsfSession.getJSFServerSession();
      FacesContext context = server.getFacesContext();

      PrettyContext prettyContext = PrettyContext.getCurrentInstance(context);

      assertTrue(prettyContext.isPrettyRequest());
      assertEquals("/", prettyContext.getRequestURL().toString());
      assertNotNull(prettyContext.getConfig());
   }

   @Test
   public void testNonMappedRequest() throws Exception
   {
      JSFSession jsfSession = new JSFSession("/index.jsf");

      JSFServerSession server = jsfSession.getJSFServerSession();
      FacesContext context = server.getFacesContext();
      PrettyContext prettyContext = PrettyContext.getCurrentInstance(context);

      assertFalse(prettyContext.isPrettyRequest());
      assertEquals("/index.jsf", prettyContext.getRequestURL().toString());
      assertNotNull(prettyContext.getConfig());
   }

   @Test
   public void testJSessionIdRemovedAutomatically() throws Exception
   {
      JSFSession jsfSession = new JSFSession("/;jsessionid=F97KJHsf9876sdf?foo=bar");
      JSFServerSession server = jsfSession.getJSFServerSession();
      FacesContext context = server.getFacesContext();

      PrettyContext prettyContext = PrettyContext.getCurrentInstance(context);

      assertTrue(prettyContext.isPrettyRequest());
      assertEquals("/", prettyContext.getRequestURL().toString());
      assertEquals("bar", prettyContext.getRequestQueryString().getParameter("foo"));
      assertNotNull(prettyContext.getConfig());
   }

   @Test
   public void testClusterJSessionIdRemovedAutomatically() throws Exception
   {
       JSFSession jsfSession = new JSFSession("/;jsessionid=2437ae534134eeae.server1?foo=bar");
       JSFServerSession server = jsfSession.getJSFServerSession();
       FacesContext context = server.getFacesContext();

       PrettyContext prettyContext = PrettyContext.getCurrentInstance(context);

       assertTrue(prettyContext.isPrettyRequest());
       assertEquals("/", prettyContext.getRequestURL().toString());
       assertEquals("bar", prettyContext.getRequestQueryString().getParameter("foo"));
       assertNotNull(prettyContext.getConfig());
   }

   @Test
   public void testGAEJSessionIdRemovedAutomatically() throws Exception
   {
       JSFSession jsfSession = new JSFSession("/;jsessionid=1E-y6jzfx53ou9wymGmcfw?foo=bar");
       JSFServerSession server = jsfSession.getJSFServerSession();
       FacesContext context = server.getFacesContext();

       PrettyContext prettyContext = PrettyContext.getCurrentInstance(context);

       assertTrue(prettyContext.isPrettyRequest());
       assertEquals("/", prettyContext.getRequestURL().toString());
       assertEquals("bar", prettyContext.getRequestQueryString().getParameter("foo"));
       assertNotNull(prettyContext.getConfig());
   }

}
