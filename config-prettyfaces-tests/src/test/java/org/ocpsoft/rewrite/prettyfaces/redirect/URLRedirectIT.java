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
package org.ocpsoft.rewrite.prettyfaces.redirect;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.prettyfaces.PrettyFacesITBase;
import org.ocpsoft.rewrite.test.RewriteITBase;
import org.ocpsoft.urlbuilder.util.Encoder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class URLRedirectIT extends RewriteITBase
{
   @Deployment(testable = false)
   public static WebArchive createDeployment()
   {
      return PrettyFacesITBase.getDeployment()
               .addClass(RedirectBean.class)
               .addAsWebResource("redirect/redirect.xhtml", "redirect.xhtml")
               .addAsWebInfResource("redirect/redirect-pretty-config.xml", "pretty-config.xml");
   }

   @Drone
   WebDriver browser;

   @Test
   public void testRefreshEncodesValuesPropertly() throws Exception
   {
      browser.get(getBaseURL() + getContextPath() + "/1 1/2 2");
      String expected = browser.getCurrentUrl();
      browser.findElement(By.id("refresh")).click();
      assertThat(browser.getCurrentUrl()).isEqualTo(expected);
   }

   @Test
   public void testRedirectEncodesValuesPropertly() throws Exception
   {
      browser.get(getBaseURL() + getContextPath() + "/foo/%20%3F%20?que=ora.+es");
      String action = browser.findElement(By.id("form")).getAttribute("action");
      browser.findElement(By.id("redirect")).click();

      // doesn't seem to work. But I think our code is correct. Perhaps a HTTPUnit problem?
      // String browserURL = client.getContentPage().getUrl().toString();
      // assertTrue(browserURL.contains(requestURL));

      assertThat(action).contains("/foo/" + Encoder.path(RedirectBean.PATH_VALUE));
      assertThat(browser.getPageSource()).contains("/foo/" + Encoder.path(RedirectBean.PATH_VALUE));
      assertThat(browser.getPageSource()).contains("?que=" + Encoder.query(RedirectBean.QUERY_VALUE));
   }
}
