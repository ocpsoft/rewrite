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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.ByteString;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;

/**
 * @author Christian Kaltepoth
 */
@RunWith(Arquillian.class)
public class JoinEncodingConfigurationTest extends RewriteTest
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteTest.getDeployment()
               .addPackages(true, ConfigRoot.class.getPackage())
               .addAsServiceProvider(ConfigurationProvider.class, JoinEncodingConfigurationProvider.class);
   }

   /**
    * A simple path parameter gets transformed into a query parameter
    */
   @Test
   public void testJoinEncodingSimpleString() throws Exception
   {
      HttpAction action = get("/encoding/foo");
      assertEquals(200, action.getStatusCode());

      String responseContent = action.getResponseContent();
      assertThat(responseContent, containsString("getRequestPath() = " + getContextPath() + "/encoding.html"));
      assertThat(responseContent, containsString("getParameter('param') = foo"));
   }

   /**
    * The space character is correctly decoded
    */
   @Test
   public void testJoinEncodingSpaceCharacter() throws Exception
   {
      HttpAction action = get("/encoding/foo%20bar");
      assertEquals(200, action.getStatusCode());

      String responseContent = action.getResponseContent();
      assertThat(responseContent, containsString("getRequestPath() = " + getContextPath() + "/encoding.html"));
      assertThat(responseContent, containsString("getParameter('param') = foo bar"));
   }

   @Test
   public void testJoinSupportsSingleCurlyBrace() throws Exception
   {
      HttpAction action = get("/encoding/foo%7Bbar");
      assertEquals(200, action.getStatusCode());

      String responseContent = action.getResponseContent();
      assertThat(responseContent, containsString("getRequestPath() = " + getContextPath() + "/encoding.html"));
      assertThat(responseContent, containsString("getParameter('param') = foo{bar"));
   }

   @Test
   public void testJoinSupportsCurlyBracketGroup() throws Exception
   {
      HttpAction action = get("/encoding/foo%5B%5D");
      assertEquals(200, action.getStatusCode());

      String responseContent = action.getResponseContent();
      assertThat(responseContent, containsString("getRequestPath() = " + getContextPath() + "/encoding.html"));
      assertThat(responseContent, containsString("getParameter('param') = foo[]"));
   }

   @Test
   public void testJoinSupportsCurlyBraceGroup() throws Exception
   {
      HttpAction action = get("/encoding/foo%7Bbar%7D");
      assertEquals(200, action.getStatusCode());

      String responseContent = action.getResponseContent();
      assertThat(responseContent, containsString("getRequestPath() = " + getContextPath() + "/encoding.html"));
      assertThat(responseContent, containsString("getParameter('param') = foo{bar}"));
   }

   /**
    * Ampersands don't have to be encoded in the path segment (rfc2396, section 3.3). Make sure this works when
    * transforming it into a query parameter.
    */
   @Test
   public void testJoinEncodingAmpersandCharacter() throws Exception
   {
      HttpAction action = get("/encoding/foo&bar");
      assertEquals(200, action.getStatusCode());

      String responseContent = action.getResponseContent();
      assertThat(responseContent, containsString("getRequestPath() = " + getContextPath() + "/encoding.html"));
      assertThat(responseContent, containsString("getParameter('param') = foo&bar"));
   }

   /**
    * Basic tests for inbound correction with a simple string parameter
    */
   @Test
   public void testInboundCorrectionSimpleString() throws Exception
   {
      HttpAction action = get("/encoding.html?param=foo");

      assertEquals(200, action.getStatusCode());
      assertEquals("/encoding/foo", action.getCurrentContextRelativeURL());
   }

   /**
    * Inbound correction should transform the space character of a query parameter (encoded as '+') into a space
    * character in the path segment (encoded as %20).
    */
   @Test
   public void testInboundCorrectionSpaceCharacter() throws Exception
   {
      HttpAction action = get("/encoding.html?param=foo+bar");

      assertEquals(200, action.getStatusCode());
      assertEquals("/encoding/foo%20bar", action.getCurrentContextRelativeURL());
   }

   /**
    * Ampersands have to be encoded in the query string but not in the path component. Make sure that this works.
    */
   @Test
   public void testInboundCorrectionAmpersandCharacter() throws Exception
   {
      HttpAction action = get("/encoding.html?param=foo%26bar");

      assertEquals(200, action.getStatusCode());
      assertEquals("/encoding/foo&bar", action.getCurrentContextRelativeURL());
   }

   /**
    * Basic test for outbound rewriting
    */
   @Test
   public void testOutboundRewritingSimpleString() throws Exception
   {
      String url = "/encoding.html?param=foo";
      String rewritten = post("/outbound", RequestBody.create(ByteString.encodeUtf8(url), MediaType.get("text/plain")));

      assertThat(rewritten, is("/encoding/foo"));
   }

   /**
    * Make sure that an URL containing a space in the query string (encoded as '+') is rewritten to a path containing
    * the space encoded as %20. This effectively tests if proper encoding/decoding is occurring throughout the entire
    * life-cycle of the parameter.
    */
   @Test
   public void testOutboundRewritingSpaceCharacter() throws Exception
   {
      String url = "/encoding.html?param=foo+bar";
      String rewritten = post("/outbound", RequestBody.create(ByteString.encodeUtf8(url), MediaType.get("text/plain")));

      assertThat(rewritten, is("/encoding/foo%20bar"));
   }

   /**
    * Ampersands have to be encoded in a query string but not in a path component.
    */
   @Test
   public void testOutboundRewritingAmpersandCharacter() throws Exception
   {
      String url = "/encoding.html?param=foo%26bar";
      String rewritten = post("/outbound", RequestBody.create(ByteString.encodeUtf8(url), MediaType.get("text/plain")));

      assertThat(rewritten, is("/encoding/foo&bar"));
   }

   /*
    * Helper methods
    */

   private String post(String path, RequestBody entity) throws IOException
   {
      Request post = new Request.Builder().post(entity)
              .url(getBaseURL() + getContextPath() + path)
              .build();

      try (Response response = client.newCall(post).execute()) {
         return response.body().string();
      }
   }

}