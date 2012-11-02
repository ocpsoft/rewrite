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

import javax.faces.context.FacesContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.jsfunit.jsfsession.JSFServerSession;
import org.jboss.jsfunit.jsfsession.JSFSession;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ocpsoft.pretty.PrettyContext;

@RunWith(Arquillian.class)
public class PathParametersTest extends PrettyFacesTestBase
{
   @Deployment
   public static WebArchive getDeployment()
   {
      return PrettyFacesTestBase.getDeployment()
               .addAsWebResource("basic/index.xhtml", "index.xhtml")
               .addAsWebInfResource("pretty-config.xml");
   }

   @Test
   public void testNamedPathParameterWithDefaultRegex() throws Exception
   {
      String initialPage = "/default/1234";
      JSFSession jsfSession = new JSFSession(initialPage);
      JSFServerSession server = jsfSession.getJSFServerSession();
      FacesContext context = server.getFacesContext();

      PrettyContext prettyContext = PrettyContext.getCurrentInstance(context);

      assertEquals(initialPage, prettyContext.getRequestURL().toString());
      assertEquals("1234", context.getExternalContext().getRequestParameterMap().get("digits"));
   }

   @Test
   public void testNamedPathParameterWithCustomRegex() throws Exception
   {
      String initialPage = "/digits/1234";
      JSFSession jsfSession = new JSFSession(initialPage);
      JSFServerSession server = jsfSession.getJSFServerSession();
      FacesContext context = server.getFacesContext();

      PrettyContext prettyContext = PrettyContext.getCurrentInstance(context);

      assertEquals(initialPage, prettyContext.getRequestURL().toString());
      assertEquals("1234", context.getExternalContext().getRequestParameterMap().get("digits"));
   }

}
