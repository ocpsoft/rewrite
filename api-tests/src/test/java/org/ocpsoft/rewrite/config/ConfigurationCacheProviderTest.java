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
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.ocpsoft.rewrite.Root;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTestBase;
import org.ocpsoft.rewrite.util.Timer;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public class ConfigurationCacheProviderTest extends RewriteTestBase
{
   @Deployment(testable = true)
   public static WebArchive getDeployment()
   {
      WebArchive deployment = RewriteTestBase.getDeployment()
               .addPackages(true, Root.class.getPackage())
               .addAsResource(new StringAsset("org.ocpsoft.rewrite.config.ConfigurationCacheProviderConfig1\n" +
                        "org.ocpsoft.rewrite.config.ConfigurationCacheProviderConfig2"),
                        "/META-INF/services/org.ocpsoft.rewrite.config.ConfigurationProvider")
               .addAsResource(new StringAsset("org.ocpsoft.rewrite.config.ConfigurationCacheProviderMock"),
                        "/META-INF/services/org.ocpsoft.rewrite.config.ConfigurationCacheProvider");
      return deployment;
   }

   volatile int votes = 0;

   @Test
   public void testCachingConfiguration()
   {
      HttpAction<HttpGet> action = get("/cache1");
      Assert.assertEquals(201, action.getResponse().getStatusLine().getStatusCode());
   }

   @Test
   public void testCachingConfigurationPerformance()
   {
      Timer timer = Timer.getTimer().start();

      int MAX = 1000;
      for (int i = 0; i < MAX; i++) {
         new Thread(request).start();
      }

      while (votes < MAX)
      {
         try {
            Thread.sleep(10);
         }
         catch (InterruptedException e) {}
      }

      timer.stop();
      long elapsedMilliseconds = timer.getElapsedMilliseconds();
      System.out.println(elapsedMilliseconds + "ms for " + MAX + " requests");
   }

   Runnable request = new Runnable() {
      @Override
      public void run()
      {
         get("/cache1");
         vote();
      }
   };

   protected synchronized void vote()
   {
      votes++;
   }
}
