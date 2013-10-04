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
package org.ocpsoft.rewrite.faces.navigate;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.faces.test.FacesBase;
import org.ocpsoft.rewrite.test.RewriteTestBase;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

@RunWith(Arquillian.class)
public class NavigateOutcomeTest extends RewriteTestBase
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return FacesBase.getDeployment()
               .addClass(NavigateOutcomeBean.class)
               .addAsServiceProviderAndClasses(ConfigurationProvider.class, NavigateOutcomeConfig.class)
               .addAsWebResource("navigate-outcome.xhtml", "navigate.xhtml");
   }

   @Test
   public void testRedirectSimpleString() throws Exception
   {

      HtmlPage firstPage = getWebClient("/navigate").getPage();
      HtmlPage secondPage = firstPage.getHtmlElementById("form:redirectSimpleString").click();

      assertThat(secondPage.getWebResponse().getWebRequest().getHttpMethod(), is(HttpMethod.GET));
      assertThat(secondPage.getUrl().toString(), endsWith("/navigate?q=foo"));

      String secondPageContent = secondPage.getWebResponse().getContentAsString();
      assertThat(secondPageContent, containsString("Value = [foo]"));

   }

   @Test
   public void testRedirectWithSpace() throws Exception
   {

      HtmlPage firstPage = getWebClient("/navigate").getPage();
      HtmlPage secondPage = firstPage.getHtmlElementById("form:redirectWithSpace").click();

      assertThat(secondPage.getWebResponse().getWebRequest().getHttpMethod(), is(HttpMethod.GET));
      assertThat(secondPage.getUrl().toString(), endsWith("/navigate?q=foo+bar"));

      String secondPageContent = secondPage.getWebResponse().getContentAsString();
      assertThat(secondPageContent, containsString("Value = [foo bar]"));

   }

   @Test
   public void testRedirectWithAmpersand() throws Exception
   {

      HtmlPage firstPage = getWebClient("/navigate").getPage();
      HtmlPage secondPage = firstPage.getHtmlElementById("form:redirectWithAmpersand").click();

      assertThat(secondPage.getWebResponse().getWebRequest().getHttpMethod(), is(HttpMethod.GET));
      assertThat(secondPage.getUrl().toString(), endsWith("/navigate?q=foo%26bar"));

      String secondPageContent = secondPage.getWebResponse().getContentAsString();
      assertThat(secondPageContent, containsString("Value = [foo&amp;bar]"));

   }

   @Test
   public void testRedirectWithEquals() throws Exception
   {

      HtmlPage firstPage = getWebClient("/navigate").getPage();
      HtmlPage secondPage = firstPage.getHtmlElementById("form:redirectWithEquals").click();

      assertThat(secondPage.getWebResponse().getWebRequest().getHttpMethod(), is(HttpMethod.GET));
      assertThat(secondPage.getUrl().toString(), endsWith("/navigate?q=foo%3Dbar"));

      String secondPageContent = secondPage.getWebResponse().getContentAsString();
      assertThat(secondPageContent, containsString("Value = [foo=bar]"));

   }

   @Test
   public void testRedirectWithChinese() throws Exception
   {

      HtmlPage firstPage = getWebClient("/navigate").getPage();
      HtmlPage secondPage = firstPage.getHtmlElementById("form:redirectWithChinese").click();

      assertThat(secondPage.getWebResponse().getWebRequest().getHttpMethod(), is(HttpMethod.GET));
      assertThat(secondPage.getUrl().toString(), endsWith("/navigate?q=%E6%BC%A2%E5%AD%97"));

   }

   @Test
   public void testNavigateNoParams() throws Exception
   {

      HtmlPage firstPage = getWebClient("/navigate").getPage();
      HtmlPage secondPage = firstPage.getHtmlElementById("form:navigateNoParams").click();

      assertThat(secondPage.getWebResponse().getWebRequest().getHttpMethod(), is(HttpMethod.POST));
      assertThat(secondPage.getUrl().toString(), Matchers.containsString(getContextPath() + "/navigate"));

   }

}
