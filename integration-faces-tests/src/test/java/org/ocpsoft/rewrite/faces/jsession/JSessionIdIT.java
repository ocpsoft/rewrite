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
package org.ocpsoft.rewrite.faces.jsession;

import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.category.IgnoreForAS7;
import org.ocpsoft.rewrite.category.IgnoreForGlassfish3;
import org.ocpsoft.rewrite.category.IgnoreForWildfly;
import org.ocpsoft.rewrite.faces.annotation.RewriteFacesAnnotationsTest;
import org.ocpsoft.rewrite.test.RewriteIT;
import org.ocpsoft.rewrite.test.RewriteITBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.gargoylesoftware.htmlunit.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@Category({
         // For some reason both containers don't add the jsessionid at all to form postback URLs.
         // Not sure why, but it seems to have nothing to do with #147.
         IgnoreForAS7.class,
         IgnoreForWildfly.class,
         IgnoreForGlassfish3.class
})
public class JSessionIdIT extends RewriteITBase
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteIT.getDeployment()
               .addAsLibrary(RewriteFacesAnnotationsTest.getRewriteAnnotationArchive())
               .addAsLibrary(RewriteFacesAnnotationsTest.getRewriteFacesArchive())
               .addClass(JSessionIdBean.class)
               .addAsWebResource("jsessionid.xhtml", "test.xhtml");
   }

   @ArquillianResource
   private URL baseUrl;

   @Test
   public void withCookiesAndStandardOutcome() throws Exception
   {

      // GIVEN a browser with cookies enabled
      WebDriver driver = createBrowserAndLoadPage(true);
      assertThat(driver.getPageSource()).doesNotContain("jsessionid");

      // WHEN a JSF action redirects with a standard outcome
      driver.findElement(By.id("form:standardOutcome")).click();

      // THEN the browser should be redirected
      assertThat(driver.getCurrentUrl()).contains("redirected=true");

      // AND there should be no jsessionid in the URL
      assertThat(driver.getCurrentUrl()).doesNotContain("jsessionid");

   }

   @Test
   public void withCookiesAndNavigateOutcome() throws Exception
   {

      // GIVEN a browser with cookies enabled
      WebDriver driver = createBrowserAndLoadPage(true);
      assertThat(driver.getPageSource()).doesNotContain("jsessionid");

      // WHEN a JSF action redirects with a standard outcome
      driver.findElement(By.id("form:navigateOutcome")).click();

      // THEN the browser should be redirected
      assertThat(driver.getCurrentUrl()).contains("redirected=true");

      // AND there should be no jsessionid in the URL
      assertThat(driver.getCurrentUrl()).doesNotContain("jsessionid");

   }

   @Test
   public void withoutCookiesAndStandardOutcome() throws Exception
   {

      // GIVEN a browser with cookies disabled
      WebDriver driver = createBrowserAndLoadPage(false);
      assertThat(driver.getPageSource()).contains("jsessionid");

      // WHEN a JSF action redirects with a standard outcome
      driver.findElement(By.id("form:standardOutcome")).click();

      // THEN the browser should be redirected
      assertThat(driver.getCurrentUrl()).contains("redirected=true");

      // AND there should be a jsessionid in the URL
      assertThat(driver.getCurrentUrl()).contains("jsessionid");

   }

   @Test
   public void withoutCookiesAndNavigateOutcome() throws Exception
   {

      // GIVEN a browser with cookies disabled
      WebDriver driver = createBrowserAndLoadPage(false);
      assertThat(driver.getPageSource()).contains("jsessionid");

      // WHEN a JSF action redirects with a standard outcome
      driver.findElement(By.id("form:navigateOutcome")).click();

      // THEN the browser should be redirected
      assertThat(driver.getCurrentUrl()).contains("redirected=true");

      // AND there should be a jsessionid in the URL
      assertThat(driver.getCurrentUrl()).contains("jsessionid");

   }

   private WebDriver createBrowserAndLoadPage(final boolean cookies)
   {

      // custom HtmlUnitDriver with cookies enabled or disabled
      WebDriver driver = new HtmlUnitDriver() {
         @Override
         protected WebClient modifyWebClient(WebClient client)
         {
            client.getCookieManager().setCookiesEnabled(cookies);
            return client;
         }
      };

      // load the page twice to get rid of jsessionid if cookies are enabled
      driver.get(baseUrl + "test");
      driver.get(baseUrl + "test");

      return driver;

   }

}