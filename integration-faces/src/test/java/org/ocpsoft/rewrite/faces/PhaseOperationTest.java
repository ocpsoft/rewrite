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
public class PhaseOperationTest extends RewriteTest
{
   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      WebArchive deployment = FacesBase
               .getDeployment()
               .addClasses(PhaseOperationTestConfigurationProvider.class)
               .addAsServiceProvider(ConfigurationProvider.class, PhaseOperationTestConfigurationProvider.class)
               .addAsWebResource("empty.xhtml", "empty.xhtml");

      return deployment;
   }

   @Test
   public void testDeferOperationRestoreView() throws Exception
   {
      HttpAction<HttpGet> action = get("/empty.xhtml?adf=blah");
      String content = action.getResponseContent();
      Assert.assertTrue(content == null || content.isEmpty());
      Assert.assertEquals(203, action.getResponse().getStatusLine().getStatusCode());
   }

   @Test
   public void testDeferOperationRenderResponse() throws Exception
   {
      HttpAction<HttpGet> action = get("/render_response");
      String content = action.getResponseContent();
      Assert.assertTrue(content == null || content.isEmpty());
      Assert.assertEquals(204, action.getResponse().getStatusLine().getStatusCode());
   }

   @Test
   public void testPhaseBindingDefersValue() throws Exception
   {
      HttpAction<HttpGet> action = get("/binding/lincoln");
      String content = action.getResponseContent();
      Assert.assertTrue(content == null || content.isEmpty());
      Assert.assertEquals(205, action.getResponse().getStatusLine().getStatusCode());
      Assert.assertEquals("lincoln", action.getResponseHeaderValues("Value").get(0));
   }

   @Test
   public void testPhaseBindingDefersValidationAndConversion() throws Exception
   {
      HttpAction<HttpGet> action = get("/defer_validation/true");
      String content = action.getResponseContent();
      Assert.assertTrue(content.contains("Empty"));
      Assert.assertEquals(200, action.getResponse().getStatusLine().getStatusCode());
   }

   @Test
   public void testPhaseBindingDefersValidationAndConversionStillDisplays404Page() throws Exception
   {
      HttpAction<HttpGet> action = get("/defer_validation/false");
      action.getResponseContent();
      Assert.assertEquals(404, action.getResponse().getStatusLine().getStatusCode());
   }

   @Test
   public void testEagerValidationFailureDisplays404Page() throws Exception
   {
      HttpAction<HttpGet> action = get("/eager_validation/false");
      action.getResponseContent();
      Assert.assertEquals(500, action.getResponse().getStatusLine().getStatusCode());
   }
}