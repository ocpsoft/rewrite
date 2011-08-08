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
package com.ocpsoft.rewrite.servlet.config.parameters;

import junit.framework.Assert;

import org.apache.http.client.methods.HttpGet;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ocpsoft.rewrite.servlet.ServletRoot;
import com.ocpsoft.rewrite.test.HttpAction;
import com.ocpsoft.rewrite.test.RewriteTestBase;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class ParameterConfigurationTest extends RewriteTestBase
{
   @Deployment(testable = true)
   public static WebArchive getDeployment()
   {
      WebArchive deployment = RewriteTestBase
               .getDeployment()
               .addPackages(true, ServletRoot.class.getPackage())
               .addAsResource(
                        new StringAsset(
                                 "com.ocpsoft.rewrite.servlet.config.parameters.ParameterConfigurationProvider"),
                        "/META-INF/services/com.ocpsoft.rewrite.config.ConfigurationProvider");
      return deployment;
   }

   @Test
   public void testPathParameterRequestBinding()
   {
      HttpAction<HttpGet> action = get("/lincoln/order/3");
      Assert.assertEquals(200, action.getResponse().getStatusLine().getStatusCode());
      Assert.assertEquals("lincoln", action.getResponseHeaderValues("User-Name").get(0));
      Assert.assertEquals("3", action.getResponseHeaderValues("Order-ID").get(0));
   }

   @Test
   public void testTestPathParameterNotMatchingRegexes()
   {
      HttpAction<HttpGet> action = get("/lincoln3/order/z42");
      Assert.assertEquals(404, action.getResponse().getStatusLine().getStatusCode());
   }

   @Test
   public void testTestPathAndForwardUseEvaluationContextByDefault()
   {
      HttpAction<HttpGet> action = get("/p/rewrite/story/50");
      Assert.assertEquals(200, action.getResponse().getStatusLine().getStatusCode());
   }

   @Test
   public void testFailedBindingRaisesException()
   {
      HttpAction<HttpGet> action = get("/lincoln/profile");
      Assert.assertEquals(500, action.getResponse().getStatusLine().getStatusCode());
   }
}
