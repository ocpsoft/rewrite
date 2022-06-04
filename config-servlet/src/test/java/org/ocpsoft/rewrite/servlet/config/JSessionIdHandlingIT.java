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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.category.IgnoreForWildfly;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteIT;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @see https://github.com/ocpsoft/rewrite/issues/82
 *
 * @author Christian Kaltepoth
 */
@RunWith(Arquillian.class)
public class JSessionIdHandlingIT extends RewriteIT
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteIT.getDeployment()
               .addClass(JSessionIdHandlingProvider.class)
               .addAsWebResource(new StringAsset("some content"), "file.txt")
               .addAsServiceProvider(ConfigurationProvider.class, JSessionIdHandlingProvider.class);
   }

   /**
    * This tests is ignored for Wildfly because of the following issues in 8.0.0.Beta1:
    * 
    * @see https://issues.jboss.org/browse/WFLY-2259
    * @see https://issues.jboss.org/browse/WFLY-2251
    */
   @Test
   @Category(IgnoreForWildfly.class)
   public void testPathRuleMatchesWithRedirectedSessionId() throws Exception
   {
      HttpAction action = get("/getsession");
      assertThat(action.getStatusCode()).isEqualTo(210);
   };

   @Test
   public void testPathRuleMatchesWithoutSessionId() throws Exception
   {
      HttpAction action = get("/path");
      assertThat(action.getStatusCode()).isEqualTo(210);
   }

   /*
    * This test works on AS7 and Glassfish, but fails on Tomcat. As it doesn't test
    * a real world use case (empty session IDs are not valid), we will ignore it for
    * now.
    */
   @Test
   @Ignore
   public void testPathRuleMatchesWithMissingSessionId() throws Exception
   {
      HttpAction action = get("/path;jsessionid=");
      assertThat(action.getStatusCode()).isEqualTo(210);
   }

   @Test
   public void testPathRuleMatchesWithSessionId() throws Exception
   {
      HttpAction action = get("/path;jsessionid=8970B4E77CAFE4390B0A2ED374C1815B");
      assertThat(action.getStatusCode()).isEqualTo(210);
   }

   @Test
   public void testPathRuleMatchesWithGoogleAppEngineSessionId() throws Exception
   {
      // see: https://github.com/ocpsoft/prettyfaces/issues/15
      HttpAction action = get("/path;jsessionid=1E-y6jzfx53ou9wymGmcfw");
      assertThat(action.getStatusCode()).isEqualTo(210);
   }

   @Test
   public void testPathRuleMatchesWithJBoss713() throws Exception
   {
      // see: https://github.com/ocpsoft/rewrite/issues/173
      HttpAction action = get("/path;jsessionid=1E+y6jzfx53ou9wymGmcfw");
      assertThat(action.getStatusCode()).isEqualTo(210);
   }

   @Test
   public void testPathRuleMatchesWithTomcatClusterSessionId() throws Exception
   {
      // see: http://ocpsoft.com/support/topic/problem-with-jsessionid-in-url-and-cluster
      HttpAction action = get("/path;jsessionid=2437ae534134eeae.server1");
      assertThat(action.getStatusCode()).isEqualTo(210);
   }

   @Test
   public void testJoinRuleMatchesWithoutSessionId() throws Exception
   {
      HttpAction action = get("/join");
      assertThat(action.getStatusCode()).isEqualTo(200);
   }

   @Test
   public void testJoinRuleMatchesWithSessionId() throws Exception
   {
      HttpAction action = get("/join;jsessionid=8970B4E77CAFE4390B0A2ED374C1815B");
      assertThat(action.getStatusCode()).isEqualTo(200);
   }

   public void testJoinRuleMatchesWithGoogleAppEngineSessionId() throws Exception
   {
      // see: https://github.com/ocpsoft/prettyfaces/issues/15
      HttpAction action = get("/join;jsessionid=1E-y6jzfx53ou9wymGmcfw");
      assertThat(action.getStatusCode()).isEqualTo(200);
   }

   @Test
   public void testJoinRuleMatchesWithTomcatClusterSessionId() throws Exception
   {
      // see: http://ocpsoft.com/support/topic/problem-with-jsessionid-in-url-and-cluster
      HttpAction action = get("/join;jsessionid=2437ae534134eeae.server1");
      assertThat(action.getStatusCode()).isEqualTo(200);
   }

}