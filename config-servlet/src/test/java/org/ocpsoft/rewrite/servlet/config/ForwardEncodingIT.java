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

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteIT;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class ForwardEncodingIT extends RewriteIT
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteIT.getDeployment()
               .addPackages(true, ConfigRoot.class.getPackage())
               .addAsServiceProvider(ConfigurationProvider.class, ForwardEncodingProvider.class)
               .addAsWebResource(new StringAsset("foobar"), "direct/static/foobar.txt")
               .addAsWebResource(new StringAsset("spaces"), "direct/static/with spaces.txt")
               .addAsWebResource(new StringAsset("hash"), "direct/static/with#hash.txt");
   }

   @Test
   public void simpleFileDirect() throws Exception
   {
      HttpAction action = get("/direct/static/foobar.txt");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).contains("foobar");
   }

   @Test
   public void simpleFileForward() throws Exception
   {
      HttpAction action = get("/forward/static/foobar.txt");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).contains("foobar");
   }

   @Test
   public void fileWithSpacesDirect() throws Exception
   {
      HttpAction action = get("/direct/static/with%20spaces.txt");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).contains("spaces");
   }

   @Test
   public void fileWithSpacesForward() throws Exception
   {
      HttpAction action = get("/forward/static/with%20spaces.txt");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).contains("spaces");
   }

   @Test
   public void fileWithHashDirect() throws Exception
   {
      HttpAction action = get("/direct/static/with%23hash.txt");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).contains("hash");
   }

   @Test
   public void fileWithHashForward() throws Exception
   {
      HttpAction action = get("/forward/static/with%23hash.txt");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).contains("hash");
   }

   @Test
   public void requestUrlsDirect() throws Exception
   {

      HttpAction action = get("/direct/debug/foo%20bar.dyn");
      assertThat(action.getStatusCode()).isEqualTo(200);

      // we should get the encoded space character in both URLs
      assertThat(action.getResponseContent()).contains("getRequestURI: [/rewrite-test/direct/debug/foo%20bar.dyn]");
      assertThat(action.getResponseContent()).contains("inboundAddressPath: [/rewrite-test/direct/debug/foo%20bar.dyn]");
   }

   @Test
   public void requestUrlsForward() throws Exception
   {

      HttpAction action = get("/forward/debug/foo%20bar.dyn");
      assertThat(action.getStatusCode()).isEqualTo(200);

      // Not really sure if this is the expected result
      // Lincoln: This is the behavior of the underlying HttpServletRequest, so we've really not changed/modified any
      // behavior here. Recommend leaving this alone.
      assertThat(action.getResponseContent()).contains("getRequestURI: [/rewrite-test/direct/debug/foo bar.dyn]");

      // IMHO this should be the result as it is consistent with the non-forwarded case
      assertThat(action.getResponseContent()).contains("inboundAddressPath: [/rewrite-test/direct/debug/foo%20bar.dyn]");
   }

}