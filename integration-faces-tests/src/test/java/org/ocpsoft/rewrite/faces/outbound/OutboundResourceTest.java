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
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.category.IgnoreForGlassfish4;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.faces.annotation.RewriteFacesAnnotationsTest;
import org.ocpsoft.rewrite.test.RewriteTest;
import org.ocpsoft.rewrite.test.RewriteTestBase;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * This test doesn't work on Glassfish 4.0 at all. For some reason Glassfish _always_ appends the JSESSIONID, even if
 * the href just contains JavaScript. This leads to links like this:
 * 
 * <pre>
 * href = &quot;javascript:void(0);jsessionid=fde6b122b14c03b5ad7dcf4c51b5&quot;
 * </pre>
 * 
 * This can be reproduced even in a simple sample application without Rewrite.
 */
@RunWith(Arquillian.class)
@Category(IgnoreForGlassfish4.class)
public class OutboundResourceTest extends RewriteTestBase
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteTest.getDeployment()
               .addAsLibrary(RewriteFacesAnnotationsTest.getRewriteFacesArchive())
               .addPackages(true, "org.ocpsoft.rewrite.servlet")
               .addClass(OutboundResourceTestProvider.class)
               .addAsWebInfResource("faces-config.xml")
               .addAsWebResource("outbound-resource.css", "resources/test/css/style.css")
               .addAsWebResource("outbound-resource.xhtml", "outbound.xhtml")
               .addAsServiceProvider(ConfigurationProvider.class, OutboundResourceTestProvider.class);
   }

   @Test
   public void testResourceUrlRewritten() throws Exception
   {
      HtmlPage page = getWebClient("/outbound").getPage();
      DomNodeList<DomElement> stylesheets = page.getElementsByTagName("link");
      assertEquals(1, stylesheets.size());
      Assert.assertTrue(stylesheets.get(0).getAttribute("href").contains("/css/style.css"));
      Assert.assertTrue(stylesheets.get(0).getAttribute("href").contains("?ln=test"));
   }

}
