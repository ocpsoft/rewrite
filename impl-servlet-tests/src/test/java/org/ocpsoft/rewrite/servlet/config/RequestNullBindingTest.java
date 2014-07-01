/*
 * Copyright 2011 <a href="mailto:fabmars@gmail.com">Fabien Marsaud</a>
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
 * @author <a href="mailto:fabmars@gmail.com">Fabien Marsaud</a>
 */
@RunWith(Arquillian.class)
public class RequestNullBindingTest extends RewriteTest
{
   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      WebArchive deployment = RewriteTest
               .getDeployment()
               .addPackages(true, ServletRoot.class.getPackage())
               .addAsServiceProvider(ConfigurationProvider.class, RequestNullBindingTestProvider.class);
      return deployment;
   }

   //"123" should be converted to 123L, OK
   @Test
   public void testNotNullBinding() throws Exception
   {
      HttpAction<HttpGet> action = get("/foo/123");
      Assert.assertEquals(200, action.getResponse().getStatusLine().getStatusCode());
   }

   //"abc" can't be converted to Long so we're expecting an error 500 here.
   @Test
   public void testNonLongBinding() throws Exception
   {
      HttpAction<HttpGet> action = get("/foo/abc");
      Assert.assertEquals(500, action.getResponse().getStatusLine().getStatusCode());
   }
   
   // "" should be converted to null
   @Test
   public void testNullBinding1() throws Exception
   {
      HttpAction<HttpGet> action = get("/foo/");
      Assert.assertEquals(200, action.getResponse().getStatusLine().getStatusCode());
   }

   // idem
   @Test
   public void testNullBinding2() throws Exception
   {
      HttpAction<HttpGet> action = get("/bar//");
      Assert.assertEquals(200, action.getResponse().getStatusLine().getStatusCode());
   }
}