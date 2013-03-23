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
import org.ocpsoft.rewrite.spi.ConfigurationCacheProvider;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;
import org.ocpsoft.rewrite.util.Timer;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ConfigurationCacheProviderTest extends RewriteTest
{
   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      WebArchive deployment = RewriteTest
               .getDeployment()
               .addPackages(true, Root.class.getPackage())
               .addAsServiceProvider(ConfigurationProvider.class, ConfigurationCacheProviderConfig1.class,
                        ConfigurationCacheProviderConfig2.class)
               .addAsServiceProvider(ConfigurationCacheProvider.class, ConfigurationCacheProviderMock.class);
      return deployment;
   }

   volatile int votes = 0;
   volatile int configBuildCount = 0;

   @Test
   public void testCachingConfiguration() throws Exception
   {
      HttpAction<HttpGet> action = get("/cache1");
      Assert.assertEquals(201, action.getResponse().getStatusLine().getStatusCode());
   }

   @Test
   public void testCachingConfigurationPerformance()
   {
      Timer timer = Timer.getTimer().start();

      int MAX = 5;
      for (int i = 0; i < MAX; i++) {
         new Thread(request).start();
      }

      while (votes < MAX)
      {
         try {
            Thread.sleep(10);
            if (configBuildCount > 1)
            {
               Assert.fail();
            }
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
         HttpAction<HttpGet> action = null;
         try {
            action = get("/cache1");
         }
         catch (Exception e) {
            throw new RuntimeException(e);
         }
         configBuildCount = action.getResponse().getStatusLine().getStatusCode() - 200;
         vote();
      }
   };

   protected synchronized void vote()
   {
      votes++;
   }
}
