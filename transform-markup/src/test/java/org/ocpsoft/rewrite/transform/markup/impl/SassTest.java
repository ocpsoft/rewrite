/*
 * Copyright 2011 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ocpsoft.rewrite.transform.markup.impl;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.jruby.embed.ScriptingContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.transform.markup.Sass;
import org.ocpsoft.rewrite.transform.markup.impl.JRubyTransformer;

@Ignore
public class SassTest
{

   private ServletContext context;
   private HttpServletRewrite event;

   @Before
   public void before()
   {
      context = Mockito.mock(ServletContext.class);
      Mockito.when(context.getAttribute(JRubyTransformer.CONTAINER_STORE_KEY))
               .thenReturn(new HashMap<Class<?>, ScriptingContainer>());

      event = Mockito.mock(HttpServletRewrite.class);
      Mockito.when(event.getServletContext()).thenReturn(context);
   }

   @After
   public void after()
   {
      new MarkupContextListener().contextDestroyed(new ServletContextEvent(context));
   }

   @Test
   public void testCalculations()
   {

      String sass = ".class { width: 1+1 }";
      String css = Sass.compiler().transform(event, sass);

      assertEquals(".class { width: 2; }", normalize(css));

   }

   @Test
   public void testNesting()
   {

      String sass = ".outer { margin: 2px; .inner { padding: 3px; } }";
      String css = Sass.compiler().transform(event, sass);

      assertEquals(".outer { margin: 2px; } .outer .inner { padding: 3px; }", normalize(css));

   }

   @Test
   public void testVariables()
   {

      String sass = "$mycolor: #123456; .class { color: $mycolor }";
      String css = Sass.compiler().transform(event, sass);

      assertEquals(".class { color: #123456; }", normalize(css));

   }

   @Test
   public void testMixins()
   {

      String sass = "@mixin invalid { color: red } .label { @include invalid }";
      String css = Sass.compiler().transform(event, sass);

      assertEquals(".label { color: red; }", normalize(css));

   }

   private static String normalize(String s)
   {
      return s.replaceAll("\n", "").replaceAll("[\t ]+", " ").trim();
   }

}
