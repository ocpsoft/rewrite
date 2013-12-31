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
package org.ocpsoft.rewrite.faces.concurrency;

import static org.junit.Assert.assertEquals;

import java.net.URL;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.faces.annotation.RewriteFacesAnnotationsTest;
import org.ocpsoft.rewrite.test.RewriteTest;
import org.ocpsoft.rewrite.test.RewriteTestBase;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 * Test to reproduce #155
 * 
 * @author Christian Kaltepoth
 * 
 * @see https://github.com/ocpsoft/rewrite/issues/155
 * 
 */
@RunWith(Arquillian.class)
public class ConcurrencyTest extends RewriteTestBase
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteTest.getDeployment()
               .addAsLibrary(RewriteFacesAnnotationsTest.getRewriteAnnotationArchive())
               .addAsLibrary(RewriteFacesAnnotationsTest.getRewriteFacesArchive())
               .addClass(ConcurrencyBean.class)
               .addAsWebResource(
                        new StringAsset("<html>#{concurrencyBean.message}</html>"),
                        "page.xhtml");
   }

   @ArquillianResource
   private URL baseUrl;

   @Test
   public void testManyConcurrentDeferredRequests() throws Exception
   {

      // increase me to break the test
      final int NUMBER_OF_THREADS = 10;
      final int REQUESTS_PER_THREAD = 10;

      final AtomicInteger successCounter = new AtomicInteger(0);

      ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

      for (int thread = 0; thread < NUMBER_OF_THREADS; thread++) {

         executor.submit(new Runnable() {

            @Override
            public void run()
            {

               for (int i = 0; i < REQUESTS_PER_THREAD; i++) {

                  String uuid = UUID.randomUUID().toString();

                  WebDriver driver = new HtmlUnitDriver();
                  driver.get(baseUrl + "test/" + uuid + "/");

                  if (driver.getPageSource().contains("The parameter is [" + uuid + "]")) {
                     successCounter.addAndGet(1);
                  }

               }

            }
         });

      }

      executor.shutdown();
      executor.awaitTermination(20, TimeUnit.SECONDS);
      executor.shutdownNow();

      assertEquals(NUMBER_OF_THREADS * REQUESTS_PER_THREAD, successCounter.get());

   }
}
