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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.common.util.Streams;
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
      HttpAction<HttpGet> action = get("/encoding/foo");
      assertEquals(200, action.getResponse().getStatusLine().getStatusCode());

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
      HttpAction<HttpGet> action = get("/encoding/foo%20bar");
      assertEquals(200, action.getResponse().getStatusLine().getStatusCode());

      String responseContent = action.getResponseContent();
      assertThat(responseContent, containsString("getRequestPath() = " + getContextPath() + "/encoding.html"));
      assertThat(responseContent, containsString("getParameter('param') = foo bar"));
   }

   @Test
   public void testJoinSupportsSingleCurlyBrace() throws Exception
   {
      HttpAction<HttpGet> action = get("/encoding/foo%7Bbar");
      assertEquals(200, action.getResponse().getStatusLine().getStatusCode());

      String responseContent = action.getResponseContent();
      assertThat(responseContent, containsString("getRequestPath() = " + getContextPath() + "/encoding.html"));
      assertThat(responseContent, containsString("getParameter('param') = foo{bar"));
   }

   @Test
   public void testJoinSupportsCurlyBracketGroup() throws Exception
   {
      HttpAction<HttpGet> action = get("/encoding/foo%5B%5D");
      assertEquals(200, action.getResponse().getStatusLine().getStatusCode());

      String responseContent = action.getResponseContent();
      assertThat(responseContent, containsString("getRequestPath() = " + getContextPath() + "/encoding.html"));
      assertThat(responseContent, containsString("getParameter('param') = foo[]"));
   }

   @Test
   public void testJoinSupportsCurlyBraceGroup() throws Exception
   {
      HttpAction<HttpGet> action = get("/encoding/foo%7Bbar%7D");
      assertEquals(200, action.getResponse().getStatusLine().getStatusCode());

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
      HttpAction<HttpGet> action = get("/encoding/foo&bar");
      assertEquals(200, action.getResponse().getStatusLine().getStatusCode());

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
      HttpAction<HttpGet> action = get("/encoding.html?param=foo");

      assertEquals(200, action.getResponse().getStatusLine().getStatusCode());
      assertEquals("/encoding/foo", action.getCurrentContextRelativeURL());
   }

   /**
    * Inbound correction should transform the space character of a query parameter (encoded as '+') into a space
    * character in the path segment (encoded as %20).
    */
   @Test
   public void testInboundCorrectionSpaceCharacter() throws Exception
   {
      HttpAction<HttpGet> action = get("/encoding.html?param=foo+bar");

      assertEquals(200, action.getResponse().getStatusLine().getStatusCode());
      assertEquals("/encoding/foo%20bar", action.getCurrentContextRelativeURL());
   }

   /**
    * Ampersands have to be encoded in the query string but not in the path component. Make sure that this works.
    */
   @Test
   public void testInboundCorrectionAmpersandCharacter() throws Exception
   {
      HttpAction<HttpGet> action = get("/encoding.html?param=foo%26bar");

      assertEquals(200, action.getResponse().getStatusLine().getStatusCode());
      assertEquals("/encoding/foo&bar", action.getCurrentContextRelativeURL());
   }

   /**
    * Basic test for outbound rewriting
    */
   @Test
   public void testOutboundRewritingSimpleString() throws Exception
   {
      String url = "/encoding.html?param=foo";
      String rewritten = post("/outbound", new StringEntity(url));

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
      String rewritten = post("/outbound", new StringEntity(url));

      assertThat(rewritten, is("/encoding/foo%20bar"));
   }

   /**
    * Ampersands have to be encoded in a query string but not in a path component.
    */
   @Test
   public void testOutboundRewritingAmpersandCharacter() throws Exception
   {
      String url = "/encoding.html?param=foo%26bar";
      String rewritten = post("/outbound", new StringEntity(url));

      assertThat(rewritten, is("/encoding/foo&bar"));
   }

   /*
    * Helper methods
    */

   private String post(String path, HttpEntity entity) throws IOException
   {
      HttpPost post = new HttpPost(getBaseURL() + getContextPath() + path);
      post.setEntity(entity);
      HttpContext context = new BasicHttpContext();
      HttpResponse response = new DefaultHttpClient().execute(post, context);
      return Streams.toString(response.getEntity().getContent()).trim();
   }

}