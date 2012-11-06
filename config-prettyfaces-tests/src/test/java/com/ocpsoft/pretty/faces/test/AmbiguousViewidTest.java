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
import static org.junit.Assert.assertTrue;

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
import com.ocpsoft.pretty.faces.test.redirect.RedirectBean;

@RunWith(Arquillian.class)
public class AmbiguousViewidTest
{
   @Deployment
   public static WebArchive createDeployment()
   {
      return PrettyFacesTestBase.createDeployment()
               .addClass(RedirectBean.class)
               .addClass(AmbiguousBean.class)
               .addAsWebResource("basic/ambiguousViewId.xhtml", "index.xhtml")
               .addAsWebInfResource("basic/ambiguous-pretty-config.xml", "pretty-config.xml");
   }

   @Test
   public void testSelectsCorrectViewOnNavigation() throws Exception
   {
      String expected = "/foo";

      JSFSession jsfSession = new JSFSession(expected);
      JSFServerSession server = jsfSession.getJSFServerSession();

      JSFClientSession client = jsfSession.getJSFClientSession();

      FacesContext context = server.getFacesContext();
      PrettyContext prettyContext = PrettyContext.getCurrentInstance(context);

      client.click("bar");

      prettyContext = PrettyContext.getCurrentInstance(server.getFacesContext());
      String actual = prettyContext.getRequestURL().toString();
      assertEquals("/bar", actual);
      assertTrue(prettyContext.getRequestQueryString().getParameterMap().isEmpty());

      client.click("foo");

      prettyContext = PrettyContext.getCurrentInstance(server.getFacesContext());
      actual = prettyContext.getRequestURL().toString();
      assertEquals("/foo", actual);
      assertTrue(prettyContext.getRequestQueryString().getParameterMap().isEmpty());
   }

   @Test
   public void testRendersCorrectURLForDynaview() throws Exception
   {
      String expected = "/baz";

      JSFSession jsfSession = new JSFSession(expected);
      JSFServerSession server = jsfSession.getJSFServerSession();

      JSFClientSession client = jsfSession.getJSFClientSession();

      FacesContext context = server.getFacesContext();
      PrettyContext prettyContext = PrettyContext.getCurrentInstance(context);

      client.click("baz");

      prettyContext = PrettyContext.getCurrentInstance(server.getFacesContext());
      String actual = prettyContext.getRequestURL().toString();
      assertEquals("/baz", actual);
      assertTrue(prettyContext.getRequestQueryString().getParameterMap().isEmpty());
      assertEquals(prettyContext.getContextPath() + "/baz", client.getElement("baz").getAttribute("href"));
   }
}
