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
package org.ocpsoft.rewrite.faces.outcome;

import static org.hamcrest.Matchers.endsWith;
import static org.junit.Assert.assertThat;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.faces.test.FacesBase;
import org.ocpsoft.rewrite.test.RewriteTestBase;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

@RunWith(Arquillian.class)
public class OutcomeEncodingTest extends RewriteTestBase
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return FacesBase.getDeployment()
               .addClass(OutcomeEncodingBean.class)
               .addAsServiceProviderAndClasses(ConfigurationProvider.class, OutcomeEncodingConfig.class)
               .addAsWebResource("outcome-encoding-start.xhtml", "start.xhtml")
               .addAsWebResource("outcome-encoding-page.xhtml", "page.xhtml");
   }

   @Test
   public void testOutcomeRedirectSimple() throws Exception
   {

      HtmlPage startPage = getWebClient("/start").getPage();
      HtmlPage secondPage = startPage.getHtmlElementById("form:redirectSimple").click();

      assertThat(secondPage.getUrl().toString(), endsWith("/page/foo?query=foo"));

   }

   @Test
   public void testOutcomeRedirectWithSpace() throws Exception
   {

      HtmlPage startPage = getWebClient("/start").getPage();
      HtmlPage secondPage = startPage.getHtmlElementById("form:redirectWithSpace").click();

      assertThat(secondPage.getUrl().toString(), endsWith("/page/foo%20bar?query=foo+bar"));

   }

   @Test
   @Ignore("It seems like JSF is actually failing to parse the returned action strings, "
            + "and is passing only the bare view-id to response.encodeURL()."
            + "I'm not sure what the rules on '=' as a value are in the http spec, but"
            + "this is JSF doing something bad here afaik. Perhaps something to do with:"
            + "'include-view-params=true' ?")
   public void testOutcomeRedirectWithEqual() throws Exception
   {

      HtmlPage startPage = getWebClient("/start").getPage();
      HtmlPage secondPage = startPage.getHtmlElementById("form:redirectWithEqual").click();

      assertThat(secondPage.getUrl().toString(), endsWith("/page/foo%3Dbar&query=foo%3Dbar"));

   }

   @Test
   @Ignore("Actually I think that this should not include ampersands in the rewritten values. "
            + "Parsing the redirect should treat '&bar' as a query parameter with null value. "
            + "JSF is still dropping 'bar' incorrectly, but I wouldn't expect it to be included "
            + "in the path param of the rewritten URL.")
   public void testOutcomeRedirectWithAmpersand() throws Exception
   {

      HtmlPage startPage = getWebClient("/start").getPage();
      HtmlPage secondPage = startPage.getHtmlElementById("form:redirectWithAmpersand").click();

      /*
       * Christian thinks:
       */
      // assertThat(secondPage.getUrl().toString(), endsWith("/page/foo%26bar&query=foo%26bar"));

      /*
       * Lincoln thinks:
       */
      assertThat(secondPage.getUrl().toString(), endsWith("/page/foo?bar&query=foo&bar"));

   }

}