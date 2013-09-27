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

import org.apache.http.client.methods.HttpGet;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class RequestQueryStringTest extends RewriteTest
{
   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      WebArchive deployment = RewriteTest
               .getDeployment()
               .addPackages(true, ConfigRoot.class.getPackage())
               .addAsServiceProvider(ConfigurationProvider.class, RequestQueryStringConfigurationProvider.class);
      return deployment;
   }

   @Test
   public void testCanParseEmptyQueryString() throws Exception
   {
      HttpAction<HttpGet> action = get("/something?");
      Assert.assertEquals(209, action.getResponse().getStatusLine().getStatusCode());
   }

   @Test
   public void testCanParseAmpersandCharactersInParameterValue() throws Exception
   {
      /*
       * Contains a query parameter with an correctly encoded ampersand.
       */
      HttpAction<HttpGet> action = get("/ampersand?param=foo%26bar"); // foo&bar

      /*
       * Rule #1 should match. HttpInboundRewriteImpl.getURL() used to decode the query string to '?param=foo&bar' which
       * QueryStringBuilder could not parse. Therefore rule #2 matched, which was not correct!
       */
      Assert.assertEquals(209, action.getResponse().getStatusLine().getStatusCode());

   }

}