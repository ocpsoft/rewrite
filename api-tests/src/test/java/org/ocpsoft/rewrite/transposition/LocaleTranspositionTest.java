/*
 * Copyright 2014 Université de Montréal
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
package org.ocpsoft.rewrite.transposition;

import java.io.File;

import org.apache.http.client.methods.HttpGet;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.Root;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;

/**
 * This test class requires /src/test/resources/bundle_en.properties and bundle_fr.properties files.
 * 
 * @author Christian Gendreau
 * 
 */
@RunWith(Arquillian.class)
public class LocaleTranspositionTest extends RewriteTest
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      WebArchive deployment = RewriteTest
               .getDeployment()
               .addPackages(true, Root.class.getPackage())
               .setWebXML(new File("src/test/webapp/WEB-INF/web.xml"))
               .addAsServiceProvider(ConfigurationProvider.class, LocaleTranspositionConfigurationProvider.class)
               .addAsWebResource(new StringAsset("search page"), "search")
               .addAsWebResource(new StringAsset("library page"), "library")
               .addAsWebResource(new File("src/test/webapp/search.xhtml"))
               .addAsResource(new File("src/test/resources/bundle_fr.properties"))
               .addAsResource(new File("src/test/resources/bundle_en.properties"));
      return deployment;
   }

   /**
    * Currently failing
    * 
    * @throws Exception
    */
   @Test
   public void testI18nSupport() throws Exception
   {
      // 'rechercher' should be translated to 'search' and 'lang' should be joined
      HttpAction<HttpGet> action = get("/fr/rechercher");
      Assert.assertEquals(200, action.getResponse().getStatusLine().getStatusCode());
      Assert.assertEquals("search page", action.getResponseContent());
   }

   @Test
   public void testI18nSupportWithAccent() throws Exception
   {
      // 'bibliothèque' should be translated to 'library' and 'lang' should be joined
      HttpAction<HttpGet> action = get("/fr/bibliothèque");
      Assert.assertEquals(200, action.getResponse().getStatusLine().getStatusCode());
      Assert.assertEquals("library page", action.getResponseContent());
   }

   @Test
   @Ignore
   public void testI18nSupportOutbound() throws Exception
   {
      HttpAction<HttpGet> action = get("/en/search.xhtml");
      Assert.assertEquals(200, action.getResponse().getStatusLine().getStatusCode());
      Assert.assertThat(action.getResponseContent(), org.hamcrest.CoreMatchers.containsString("/en/search"));
   }

   /**
    * Expected behavior(default) when used as a Transposition and Constraint is to abort the rule so 404 is expected.
    * 
    * @throws Exception
    */
   @Test
   public void testI18nSupportFailureNoHandling() throws Exception
   {
      HttpAction<HttpGet> action = get("/zz/library");
      Assert.assertEquals(404, action.getResponse().getStatusLine().getStatusCode());
   }

   /**
    * Expected behavior(default) when used as a Transposition only is to keep the original value so the address should
    * become /library
    * 
    * @Test
    * @throws Exception
    */
   public void testI18nSupportTranspositionFailureNoHandling() throws Exception
   {
      HttpAction<HttpGet> action = get("/zz/library/transposition_only");
      Assert.assertEquals(200, action.getResponse().getStatusLine().getStatusCode());
      Assert.assertEquals("library page", action.getResponseContent());
   }

   /**
    * Test onTranspositionFailed with missing Locale but good key. Expected behavior is to receive status code 201, as
    * defined by LocaleTranspositionConfigurationProvider.
    * 
    * @throws Exception
    */
   @Test
   public void testLocaleTranspositionFailureWithHandling() throws Exception
   {
      HttpAction<HttpGet> action = get("/zz/library/transposition_failed_1");
      Assert.assertEquals(201, action.getResponse().getStatusLine().getStatusCode());
   }

   /**
    * Test onTranspositionFailed with good Locale but missing key. Expected behavior is to receive status code 202, as
    * defined by LocaleTranspositionConfigurationProvider.
    * 
    * @throws Exception
    */
   @Test
   public void testLocaleTranspositionMissingKeyWithHandling() throws Exception
   {
      HttpAction<HttpGet> action = get("/en/missinglibrary/transposition_failed_2");
      Assert.assertEquals(202, action.getResponse().getStatusLine().getStatusCode());
   }
}