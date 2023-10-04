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
import org.ocpsoft.rewrite.servlet.ServletRoot;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class RequestConstraintTransformTest extends RewriteTest
{
   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      WebArchive deployment = RewriteTest
               .getDeployment()
               .addPackages(true, ServletRoot.class.getPackage())
               .addAsServiceProvider(ConfigurationProvider.class, RequestConstraintTransformTestProvider.class);
      return deployment;
   }

   @Test
   public void testUnsatisfiedConstraintPreventsRuleExecution() throws Exception
   {
      HttpAction<HttpGet> action = get("/constraint/ONE/2");
      Assert.assertEquals(404, action.getResponse().getStatusLine().getStatusCode());
   }

   @Test
   public void testSatisfiedConstraintPreventsRuleExecution() throws Exception
   {
      HttpAction<HttpGet> action = get("/constraint/ONE/TWO");
      Assert.assertEquals(211, action.getResponse().getStatusLine().getStatusCode());
   }

   @Test
   public void testTransformModifiesIncomingValue() throws Exception
   {
      HttpAction<HttpGet> action = get("/constraint/ONE/TWO");
      Assert.assertEquals("ONE", action.getResponseHeaderValues("one").get(0));
      Assert.assertEquals("two", action.getResponseHeaderValues("two").get(0));
   }

   @Test
   public void testTransformModifiesOutboundValue() throws Exception
   {
      HttpAction<HttpGet> action = get("/constraint/ONE/TWO");
      Assert.assertEquals(action.getContextPath() + "/outbound/three", action.getResponseHeaderValues("three").get(0));
   }

}