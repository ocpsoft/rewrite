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

import junit.framework.Assert;

import org.apache.http.client.methods.HttpGet;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;

/**
 * @see https://github.com/ocpsoft/rewrite/issues/82
 * 
 * @author Christian Kaltepoth
 */
@RunWith(Arquillian.class)
public class JSessionIdHandlingTest extends RewriteTest
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteTest.getDeployment()
               .addClass(JSessionIdHandlingProvider.class)
               .addAsWebResource(new StringAsset("some content"), "file.txt")
               .addAsServiceProvider(ConfigurationProvider.class, JSessionIdHandlingProvider.class);
   }

   @Test
   public void testPathRuleMatchesWithoutSessionId() throws Exception
   {
      HttpAction<HttpGet> action = get("/path");
      Assert.assertEquals(210, action.getResponse().getStatusLine().getStatusCode());
   }

   @Test
   public void testPathRuleMatchesWithSessionId() throws Exception
   {
      HttpAction<HttpGet> action = get("/path;jsessionid=8970B4E77CAFE4390B0A2ED374C1815B");
      Assert.assertEquals(210, action.getResponse().getStatusLine().getStatusCode());
   }

   public void testPathRuleMatchesWithGoogleAppEngineSessionId() throws Exception
   {
      // see: https://github.com/ocpsoft/prettyfaces/issues/15
      HttpAction<HttpGet> action = get("/path;jsessionid=1E-y6jzfx53ou9wymGmcfw");
      Assert.assertEquals(210, action.getResponse().getStatusLine().getStatusCode());
   }

   @Test
   public void testPathRuleMatchesWithTomcatClusterSessionId() throws Exception
   {
      // see: http://ocpsoft.com/support/topic/problem-with-jsessionid-in-url-and-cluster
      HttpAction<HttpGet> action = get("/path;jsessionid=2437ae534134eeae.server1");
      Assert.assertEquals(210, action.getResponse().getStatusLine().getStatusCode());
   }

   @Test
   public void testJoinRuleMatchesWithoutSessionId() throws Exception
   {
      HttpAction<HttpGet> action = get("/join");
      Assert.assertEquals(200, action.getResponse().getStatusLine().getStatusCode());
   }

   @Test
   public void testJoinRuleMatchesWithSessionId() throws Exception
   {
      HttpAction<HttpGet> action = get("/join;jsessionid=8970B4E77CAFE4390B0A2ED374C1815B");
      Assert.assertEquals(200, action.getResponse().getStatusLine().getStatusCode());
   }

   public void testJoinRuleMatchesWithGoogleAppEngineSessionId() throws Exception
   {
      // see: https://github.com/ocpsoft/prettyfaces/issues/15
      HttpAction<HttpGet> action = get("/join;jsessionid=1E-y6jzfx53ou9wymGmcfw");
      Assert.assertEquals(200, action.getResponse().getStatusLine().getStatusCode());
   }

   @Test
   public void testJoinRuleMatchesWithTomcatClusterSessionId() throws Exception
   {
      // see: http://ocpsoft.com/support/topic/problem-with-jsessionid-in-url-and-cluster
      HttpAction<HttpGet> action = get("/join;jsessionid=2437ae534134eeae.server1");
      Assert.assertEquals(200, action.getResponse().getStatusLine().getStatusCode());
   }

}