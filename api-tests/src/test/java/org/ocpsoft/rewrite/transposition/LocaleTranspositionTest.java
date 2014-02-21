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
               .addAsServiceProvider(ConfigurationProvider.class, LocaleTranspositionConfigurationProvider.class)
               .addAsWebResource(new StringAsset("search page"), "search")
               .addAsWebResource(new StringAsset("library page"), "library")
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
   public void testLocaleTransposition() throws Exception
   {
      // 'rechercher' should be translated to 'search' and 'lang' should be joined
      HttpAction<HttpGet> action = get("/fr/rechercher");
      Assert.assertEquals(200, action.getResponse().getStatusLine().getStatusCode());
      Assert.assertEquals("search page", action.getResponseContent());
   }

   @Test
   public void testLocaleTranspositionWithAccent() throws Exception
   {
      // 'bibliothèque' should be translated to 'library' and 'lang' should be joined
      HttpAction<HttpGet> action = get("/fr/bibliothèque");
      Assert.assertEquals(200, action.getResponse().getStatusLine().getStatusCode());
      Assert.assertEquals("library page", action.getResponseContent());
   }
   
   @Test
   public void testMissingLocaleTransposition() throws Exception
   {
	   //FIXME a MissingResourceException is thrown
      HttpAction<HttpGet> action = get("/zz/library");
      Assert.assertEquals(500, action.getResponse().getStatusLine().getStatusCode());
   }
}