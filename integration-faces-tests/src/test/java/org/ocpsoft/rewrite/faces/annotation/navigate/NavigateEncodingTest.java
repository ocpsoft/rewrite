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
package org.ocpsoft.rewrite.faces.annotation.navigate;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.junit.Assert.assertThat;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.faces.annotation.RewriteFacesAnnotationsTest;
import org.ocpsoft.rewrite.test.RewriteTest;
import org.ocpsoft.rewrite.test.RewriteTestBase;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

@RunWith(Arquillian.class)
public class NavigateEncodingTest extends RewriteTestBase
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteTest.getDeployment()
               .addAsLibrary(RewriteFacesAnnotationsTest.getRewriteAnnotationArchive())
               .addAsLibrary(RewriteFacesAnnotationsTest.getRewriteFacesArchive())
               .addClass(NavigateEncodingBean.class)
               .addAsWebResource("navigate-encoding.xhtml");
   }

   @Test
   public void testNavigateWithSimpleString() throws Exception
   {

      HtmlPage firstPage = getWebClient("/navigate").getPage();
      HtmlPage secondPage = firstPage.getHtmlElementById("form:redirectSimpleString").click();

      assertThat(secondPage.getUrl().toString(), endsWith("/navigate?q=foo"));

      String secondPageContent = secondPage.getWebResponse().getContentAsString();
      assertThat(secondPageContent, containsString("Value = [foo]"));

   }

   @Test
   public void testNavigateStringWithSpace() throws Exception
   {

      HtmlPage firstPage = getWebClient("/navigate").getPage();
      HtmlPage secondPage = firstPage.getHtmlElementById("form:redirectStringWithSpace").click();

      assertThat(secondPage.getUrl().toString(), endsWith("/navigate?q=foo+bar"));

      String secondPageContent = secondPage.getWebResponse().getContentAsString();
      assertThat(secondPageContent, containsString("Value = [foo bar]"));

   }

   @Test
   public void testNavigateProblematicCharacters() throws Exception
   {

      HtmlPage firstPage = getWebClient("/navigate").getPage();
      HtmlPage secondPage = firstPage.getHtmlElementById("form:redirectProblematicCharacters").click();

      assertThat(secondPage.getUrl().toString(), endsWith("/navigate?q=foo%3Dbar"));

      String secondPageContent = secondPage.getWebResponse().getContentAsString();
      assertThat(secondPageContent, containsString("Value = [foo=bar]"));

   }

}
