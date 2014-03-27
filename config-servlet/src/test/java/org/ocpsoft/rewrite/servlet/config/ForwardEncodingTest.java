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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.apache.http.client.methods.HttpGet;
import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;

@RunWith(Arquillian.class)
public class ForwardEncodingTest extends RewriteTest
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteTest.getDeployment()
               .addPackages(true, ConfigRoot.class.getPackage())
               .addAsServiceProvider(ConfigurationProvider.class, ForwardEncodingProvider.class)
               .addAsWebResource(new StringAsset("foobar"), "direct/static/foobar.txt")
               .addAsWebResource(new StringAsset("spaces"), "direct/static/with spaces.txt")
               .addAsWebResource(new StringAsset("hash"), "direct/static/with#hash.txt");
   }

   @Test
   public void simpleFileDirect() throws Exception
   {
      HttpAction<HttpGet> action = get("/direct/static/foobar.txt");
      assertEquals(200, action.getResponse().getStatusLine().getStatusCode());
      assertThat(action.getResponseContent(), Matchers.containsString("foobar"));
   }

   @Test
   public void simpleFileForward() throws Exception
   {
      HttpAction<HttpGet> action = get("/forward/static/foobar.txt");
      assertEquals(200, action.getResponse().getStatusLine().getStatusCode());
      assertThat(action.getResponseContent(), Matchers.containsString("foobar"));
   }

   @Test
   public void fileWithSpacesDirect() throws Exception
   {
      HttpAction<HttpGet> action = get("/direct/static/with%20spaces.txt");
      assertEquals(200, action.getResponse().getStatusLine().getStatusCode());
      assertThat(action.getResponseContent(), Matchers.containsString("spaces"));
   }

   @Test
   public void fileWithSpacesForward() throws Exception
   {
      HttpAction<HttpGet> action = get("/forward/static/with%20spaces.txt");
      assertEquals(200, action.getResponse().getStatusLine().getStatusCode());
      assertThat(action.getResponseContent(), Matchers.containsString("spaces"));
   }

   @Test
   public void fileWithHashDirect() throws Exception
   {
      HttpAction<HttpGet> action = get("/direct/static/with%23hash.txt");
      assertEquals(200, action.getResponse().getStatusLine().getStatusCode());
      assertThat(action.getResponseContent(), Matchers.containsString("hash"));
   }

   @Test
   public void fileWithHashForward() throws Exception
   {
      HttpAction<HttpGet> action = get("/forward/static/with%23hash.txt");
      assertEquals(200, action.getResponse().getStatusLine().getStatusCode());
      assertThat(action.getResponseContent(), Matchers.containsString("hash"));
   }

   @Test
   public void requestUrlsDirect() throws Exception
   {

      HttpAction<HttpGet> action = get("/direct/debug/foo%20bar.dyn");
      assertEquals(200, action.getResponse().getStatusLine().getStatusCode());

      // we should get the encoded space character in both URLs
      assertThat(action.getResponseContent(),
               Matchers.containsString("getRequestURI: [/rewrite-test/direct/debug/foo%20bar.dyn]"));
      assertThat(action.getResponseContent(),
               Matchers.containsString("inboundAddressPath: [/rewrite-test/direct/debug/foo%20bar.dyn]"));

   }

   @Test
   public void requestUrlsForward() throws Exception
   {

      HttpAction<HttpGet> action = get("/forward/debug/foo%20bar.dyn");
      assertEquals(200, action.getResponse().getStatusLine().getStatusCode());

      // Not really sure if this is the expected result
      assertThat(action.getResponseContent(),
               Matchers.containsString("getRequestURI: [/rewrite-test/direct/debug/foo bar.dyn]"));

      // IMHO this should be the result as it is consistent with the non-forwarded case
      assertThat(action.getResponseContent(),
               Matchers.containsString("inboundAddressPath: [/rewrite-test/direct/debug/foo%20bar.dyn]"));

   }

}