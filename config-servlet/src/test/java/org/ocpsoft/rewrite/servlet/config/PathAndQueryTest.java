/*
 * Copyright 2016 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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
package org.ocpsoft.rewrite.servlet.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.test.RewriteTest;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 * @see https://github.com/ocpsoft/rewrite/issues/243
 */
@RunWith(Arquillian.class)
public class PathAndQueryTest extends RewriteTest
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteTest.getDeployment()
              .addClass(PathAndQueryProvider.class)
              .addAsServiceProvider(ConfigurationProvider.class, PathAndQueryProvider.class);
   }

   @ArquillianResource
   private java.net.URL baseUrl;

   @Test
   public void testWithQuery() throws Exception
   {
      WebDriver driver = new HtmlUnitDriver();
      driver.get(baseUrl.toString() + "pathAndQuery/pathWithQuery?p1=foo&p2=bar");
      assertThat(driver.getCurrentUrl()).endsWith("/resultingPath/pathWithQuery%3Fp1=foo&p2=bar");
   }

   @Test
   public void testWithoutQuery() throws Exception
   {
      WebDriver driver = new HtmlUnitDriver();
      driver.get(baseUrl.toString() + "pathAndQuery/pathWithoutQuery");
      assertThat(driver.getCurrentUrl()).endsWith("/resultingPath/pathWithoutQuery");
   }
}
