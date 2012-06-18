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
package org.ocpsoft.rewrite.transform.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.http.client.methods.HttpGet;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.config.ConfigurationCacheProvider;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;

/**
 * 
 * Integration test for {@link Less}.
 * 
 * @author Christian Kaltepoth
 * 
 */
@RunWith(Arquillian.class)
public class TransformationCacheTest extends RewriteTest
{
   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteTest.getDeployment()
               .addAsWebResource(new StringAsset("some content"), "test.txt")
               .addClasses(TransformationCacheTestProvider.class, ServletContextConfigurationCacheProvider.class, SlowTransformer.class)
               .addAsServiceProvider(ConfigurationProvider.class, TransformationCacheTestProvider.class)
               .addAsServiceProvider(ConfigurationCacheProvider.class, ServletContextConfigurationCacheProvider.class);
   }

   @Test
   public void testTransformationCache() throws Exception
   {

      // the first request takes at least 1000ms
      long start = System.currentTimeMillis();
      HttpAction<HttpGet> firstAction = get("/test.txt");
      assertEquals(200, firstAction.getResponse().getStatusLine().getStatusCode());
      assertEquals(SlowTransformer.RESULT, firstAction.getResponseContent());
      assertTrue(System.currentTimeMillis() - start > 1000);

      // subsequent requests are much faster
      start = System.currentTimeMillis();
      for (int i = 0; i < 10; i++) {
         HttpAction<HttpGet> subsequentActions = get("/test.txt");
         assertEquals(200, subsequentActions.getResponse().getStatusLine().getStatusCode());
         assertEquals(SlowTransformer.RESULT, subsequentActions.getResponseContent());
      }
      assertTrue(System.currentTimeMillis() - start < 500);

   }

}
