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
package org.ocpsoft.rewrite.servlet.wrapper;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.category.IgnoreForWildfly;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.servlet.ServletRoot;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class WrappedResponseStreamTest extends RewriteTest
{
   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      WebArchive deployment = RewriteTest
               .getDeployment()
               .addPackages(true, ServletRoot.class.getPackage())
               .addAsWebResource(new StringAsset("lowercase"), "index.html")
               .addAsWebResource(new StringAsset("zip me to gzip please and make it zippy"), "gzip.html")
               .addAsServiceProvider(ConfigurationProvider.class, WrappedResponseStreamTestProvider.class);
      return deployment;
   }

   @Test
   public void testWrappedResponseStreamToUppercase() throws Exception
   {
      HttpAction<HttpGet> action = get("/index.html");
      Assert.assertEquals(200, action.getStatusCode());
      Assert.assertEquals("LOWERCASE", action.getResponseContent());
   }

   /**
    * Ignored on Wildlfy because there seems to be some issue with the content length.
    * 
    * @see https://github.com/ocpsoft/rewrite/issues/145
    */
   @Test
   @Category(IgnoreForWildfly.class)
   public void testWrappedResponseStreamToGZip() throws Exception
   {

      DefaultHttpClient client = new DefaultHttpClient();
      client.addRequestInterceptor(new HttpRequestInterceptor()
      {

         @Override
         public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException
         {
            if (!request.containsHeader("Accept-Encoding"))
            {
               request.addHeader("Accept-Encoding", "gzip");
            }
         }

      });

      client.addResponseInterceptor(new HttpResponseInterceptor()
      {

         @Override
         public void process(final HttpResponse response, final HttpContext context) throws HttpException, IOException
         {
            HttpEntity entity = response.getEntity();
            if (entity != null)
            {
               Header header = entity.getContentEncoding();
               if (header != null)
               {
                  HeaderElement[] codecs = header.getElements();
                  for (int i = 0; i < codecs.length; i++)
                  {
                     if (codecs[i].getName().equalsIgnoreCase("gzip"))
                     {
                        response.setEntity(new GzipDecompressingEntity(response.getEntity()));
                        return;
                     }
                  }
               }
            }
         }

      });

      HttpAction<HttpGet> action = get(client, "/gzip.html");
      Assert.assertEquals(200, action.getStatusCode());
      Assert.assertEquals("gzip", action.getResponseHeaderValues("Content-Encoding").get(0));
      Assert.assertEquals("zip me to gzip please and make it zippy", action.getResponseContent());
   }
}
