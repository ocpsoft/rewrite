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
package org.ocpsoft.rewrite.config;

import org.apache.http.client.methods.HttpGet;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.ocpsoft.rewrite.Root;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public class RelocatingConfigurationLoaderTest extends RewriteTest
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      WebArchive deployment = RewriteTest
               .getDeployment()
               .addPackages(true, Root.class.getPackage())
               .addAsServiceProvider(ConfigurationProvider.class,
                        RelocatingConfigurationProvider1.class, RelocatingConfigurationProvider2.class,
                        RelocatingConfigurationProvider3.class);
      return deployment;
   }

   @Test
   public void testRelocatedRuleExecutesInNewOrderUp() throws Exception
   {
      HttpAction<HttpGet> action = get("/priority");
      Assert.assertEquals(201, action.getResponse().getStatusLine().getStatusCode());
   }

   @Test
   public void testRelocatedRuleExecutesInNewOrderDown() throws Exception
   {
      HttpAction<HttpGet> action = get("/priority2");
      Assert.assertEquals(202, action.getResponse().getStatusLine().getStatusCode());
   }

   @Test
   public void testRelocatedRuleExecutesInNewOrderUpUp() throws Exception
   {
      HttpAction<HttpGet> action = get("/priority3");
      Assert.assertEquals(203, action.getResponse().getStatusLine().getStatusCode());
   }

   @Test
   public void testRelocatedRulePriorityOverlapFollowsProviderPriorityOrder() throws Exception
   {
      HttpAction<HttpGet> action = get("/priority4");
      Assert.assertEquals(202, action.getResponse().getStatusLine().getStatusCode());
   }

}
