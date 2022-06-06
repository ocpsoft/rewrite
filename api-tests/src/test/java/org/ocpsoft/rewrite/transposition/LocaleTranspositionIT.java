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

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.Root;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteIT;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This test class requires /src/test/resources/bundle_en.properties and bundle_fr.properties files.
 * 
 * @author Christian Gendreau
 * 
 */
@RunWith(Arquillian.class)
public class LocaleTranspositionIT extends RewriteIT
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      WebArchive deployment = RewriteIT
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
      HttpAction action = get("/fr/rechercher");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).isEqualTo("search page");
   }

   @Test
   public void testI18nSupportWithAccent() throws Exception
   {
      // 'bibliothèque' should be translated to 'library' and 'lang' should be joined
      HttpAction action = get("/fr/bibliothèque");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).isEqualTo("library page");
   }

   @Test
   @Ignore
   public void testI18nSupportOutbound() throws Exception
   {
      HttpAction action = get("/en/search.xhtml");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).contains("/en/search");
   }

   /**
    * Expected behavior(default) when used as a Transposition and Constraint is to abort the rule so 404 is expected.
    * 
    * @throws Exception
    */
   @Test
   public void testI18nSupportFailureNoHandling() throws Exception
   {
      HttpAction action = get("/zz/library");
      assertThat(action.getStatusCode()).isEqualTo(404);
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
      HttpAction action = get("/zz/library/transposition_only");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).isEqualTo("library page");
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
      HttpAction action = get("/zz/library/transposition_failed_1");
      assertThat(action.getStatusCode()).isEqualTo(201);
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
      HttpAction action = get("/en/missinglibrary/transposition_failed_2");
      assertThat(action.getStatusCode()).isEqualTo(202);
   }
}