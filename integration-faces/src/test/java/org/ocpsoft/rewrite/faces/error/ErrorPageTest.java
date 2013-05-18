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
package org.ocpsoft.rewrite.faces.error;

import static org.junit.Assert.assertThat;

import org.apache.http.client.methods.HttpGet;
import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;
import org.ocpsoft.rewrite.test.RewriteTestBase;

@RunWith(Arquillian.class)
public class ErrorPageTest extends RewriteTestBase
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      // not using FacesBase.getDeployment() so we cat set a custom web.xml
      return RewriteTest.getDeploymentNoWebXml()
               .setWebXML("error-page-web.xml")
               .addAsWebInfResource("faces-config.xml", "faces-config.xml")
               .addClass(ErrorPageConfig.class)
               .addAsServiceProviderAndClasses(ConfigurationProvider.class, ErrorPageConfig.class)
               .addAsWebResource(EmptyAsset.INSTANCE, "some-page.xhtml")
               .addAsWebResource("error-page-404.xhtml", "404.xhtml");

   }

   @Test
   public void shouldRewriteOutboundLinksWithDirectAccess() throws Exception
   {
      HttpAction<HttpGet> action = get("/404.xhtml");
      assertThat(action.getResponseContent(), Matchers.containsString("/rewritten"));
   }

   @Test
   public void shouldRewriteOutboundLinksForErrorPage() throws Exception
   {
      HttpAction<HttpGet> action = get("/does-not-exist");
      assertThat(action.getResponseContent(), Matchers.containsString("/rewritten"));
   }

}