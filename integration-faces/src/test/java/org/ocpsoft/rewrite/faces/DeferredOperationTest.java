package org.ocpsoft.rewrite.faces;

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

import org.junit.Assert;

import org.apache.http.client.methods.HttpGet;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.faces.test.FacesBase;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class DeferredOperationTest extends RewriteTest
{
   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      WebArchive deployment = FacesBase
               .getDeployment()
               .addClasses(DeferredOperationTestConfigurationProvider.class)
               .addAsServiceProvider(ConfigurationProvider.class, DeferredOperationTestConfigurationProvider.class)
               .addAsWebResource("empty.xhtml", "empty.xhtml");

      return deployment;
   }

   @Test
   public void testDeferOperationRedirectView() throws Exception
   {
      HttpAction<HttpGet> action = get("/redirect");
      String content = action.getResponseContent();
      Assert.assertTrue(content == null || content.isEmpty());
      Assert.assertEquals(201, action.getResponse().getStatusLine().getStatusCode());
      Assert.assertEquals("/redirect_result", action.getCurrentContextRelativeURL());
   }

   @Test
   public void testDeferOperationForward() throws Exception
   {
      HttpAction<HttpGet> action = get("/forward");
      String content = action.getResponseContent();
      Assert.assertTrue(content == null || content.isEmpty());
      Assert.assertEquals(202, action.getResponse().getStatusLine().getStatusCode());
      Assert.assertEquals("True", action.getResponseHeaderValues("Forward-Occurred").get(0));
      Assert.assertEquals("/forward", action.getCurrentContextRelativeURL());
   }
}