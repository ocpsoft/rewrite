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

import static org.junit.Assert.assertThat;

import org.apache.http.client.methods.HttpGet;
import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;
import org.ocpsoft.rewrite.servlet.config.ConfigRoot;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class CDNConfigurationTest extends RewriteTest
{
   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      WebArchive deployment = RewriteTest
               .getDeployment()
               .addPackages(true, ConfigRoot.class.getPackage())
               .addAsServiceProvider(ConfigurationProvider.class, CDNConfigurationProvider.class);
      return deployment;
   }

   @Test
   public void testCDNRelocation() throws Exception
   {
      HttpAction<HttpGet> action = get("/relocate");
      Assert.assertEquals(200, action.getResponse().getStatusLine().getStatusCode());
      Assert.assertTrue(action.getResponseContent().contains(
               "http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"));
      Assert.assertTrue(action.getResponseContent().contains(
               "http://mycdn.com/foo-1.2.3.js"));

   }

   @Test
   public void testCDNRelocationWithSchemalessURL() throws Exception
   {

      HttpAction<HttpGet> action = get("/relocate");
      Assert.assertEquals(200, action.getResponse().getStatusLine().getStatusCode());

      assertThat(action.getResponseContent(), Matchers.containsString(
               "[//ajax.googleapis.com/ajax/libs/angularjs/1.0.6/angular.min.js]"));

   }

}