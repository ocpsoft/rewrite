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
package org.ocpsoft.rewrite.faces.outbound;

import static org.junit.Assert.assertEquals;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.faces.annotation.RewriteFacesAnnotationsTest;
import org.ocpsoft.rewrite.test.RewriteTest;
import org.ocpsoft.rewrite.test.RewriteTestBase;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

@RunWith(Arquillian.class)
public class OutboundSpecialCasesTest extends RewriteTestBase
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteTest.getDeployment()
               .addAsLibrary(RewriteFacesAnnotationsTest.getRewriteFacesArchive())
               .addAsWebInfResource("faces-config.xml")
               .addAsWebResource("outbound-special-cases.xhtml", "outbound.xhtml");
   }

   @Test
   public void testJavaScriptNotRewritten() throws Exception
   {
      HtmlPage page = getWebClient("/faces/outbound.xhtml").getPage();
      DomElement link = page.getElementById("javascript");
      assertEquals("javascript:void(0)", link.getAttribute("href"));
   }

   @Test
   public void testAnchorNotRewritten() throws Exception
   {
      HtmlPage page = getWebClient("/faces/outbound.xhtml").getPage();
      DomElement link = page.getElementById("anchor");
      assertEquals("#foobar", link.getAttribute("href"));
   }

   @Test
   @Ignore // issue #139
   public void testEmptyAnchorNotRewritten() throws Exception
   {
      HtmlPage page = getWebClient("/faces/outbound.xhtml").getPage();
      DomElement link = page.getElementById("emptyAnchor");
      assertEquals("#", link.getAttribute("href"));
   }

}
