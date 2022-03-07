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

import okhttp3.OkHttpClient;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.category.IgnoreForWildfly;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.servlet.ServletRoot;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;

import static org.assertj.core.api.Assertions.assertThat;

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
      HttpAction action = get("/index.html");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).isEqualTo("LOWERCASE");
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

      OkHttpClient client = this.client.newBuilder()
              .addNetworkInterceptor(chain -> chain.proceed(
                      chain.request().newBuilder()
                              .header("Accept-Encoding", "gzip")
                              .build()
              ))
              .build();

      HttpAction action = get(client, "/gzip.html");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseHeaderValues("Content-Encoding").get(0)).isEqualTo("gzip");
      assertThat(action.getResponseContent()).isEqualTo("zip me to gzip please and make it zippy");
   }
}
