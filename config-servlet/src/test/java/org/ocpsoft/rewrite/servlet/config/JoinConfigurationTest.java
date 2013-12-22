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
package org.ocpsoft.rewrite.servlet.config;

import org.apache.http.client.methods.HttpGet;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class JoinConfigurationTest extends RewriteTest
{
   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      WebArchive deployment = RewriteTest
               .getDeployment()
               .addPackages(true, ConfigRoot.class.getPackage())
               .addAsServiceProvider(ConfigurationProvider.class, JoinConfigurationProvider.class);
      return deployment;
   }

   @Test
   public void testUrlMappingConfiguration() throws Exception
   {
      HttpAction<HttpGet> action = get("/p/rewrite");
      Assert.assertEquals(203, action.getResponse().getStatusLine().getStatusCode());

      Assert.assertEquals("rewrite", action.getResponseHeaderValues("Project").get(0));
      Assert.assertEquals(action.getContextPath() + "/p/rewrite", action.getResponseHeaderValues("Encoded-URL").get(0));
   }

   @Test
   public void testUrlMappingConfigurationWithoutInboundCorrection() throws Exception
   {
      HttpAction<HttpGet> action = get("/viewProject.xhtml");
      Assert.assertEquals(404, action.getResponse().getStatusLine().getStatusCode());

      Assert.assertEquals("/viewProject.xhtml", action.getCurrentContextRelativeURL());
   }

   @Test
   public void testUrlMappingConfigurationWithInboundCorrection() throws Exception
   {
      HttpAction<HttpGet> action = get("/list.xhtml?p1=foo&p2=bar");
      Assert.assertEquals(204, action.getResponse().getStatusLine().getStatusCode());

      Assert.assertEquals("/foo/bar", action.getCurrentContextRelativeURL());
   }

   @Test
   public void testSubstitutionWithExtraQueryParams() throws Exception
   {
      HttpAction<HttpGet> action = get("/1/querypath/2/?in=out");
      Assert.assertEquals(207, action.getResponse().getStatusLine().getStatusCode());

      Assert.assertEquals("/1/querypath/2/?in=out", action.getCurrentContextRelativeURL());
      Assert.assertEquals(getContextPath() + "/1-query.xhtml?in=out",
               action.getResponseHeaderValues("InboundURL").get(0));
      Assert.assertEquals("/12345/querypath/cab/?foo=bar&kitty=meow", action.getResponseHeaderValues("OutboundURL")
               .get(0));
   }

}