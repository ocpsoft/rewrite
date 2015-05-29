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
package org.ocpsoft.rewrite.transform.minify;

import static org.junit.Assert.assertEquals;

import org.apache.http.client.methods.HttpGet;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.category.IgnoreForWildfly;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;

/**
 * 
 * Integration test for {@link JsMinify}.
 * 
 * @author Christian Kaltepoth
 * 
 */
@RunWith(Arquillian.class)
public class JsMinifyTest extends RewriteTest
{
   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteTest.getDeployment()
               .addAsWebResource(new StringAsset("var text = \"hello\";\n\nalert(text);"), "test.js")
               .addAsLibraries(CssMinifyTest.getTransformArchive())
               .addAsLibraries(resolveDependency("com.yahoo.platform.yui:yuicompressor"))
               .addAsLibraries(resolveDependency("rhino:js"))
               .addClasses(JsMinifyTestProvider.class)
               .addAsServiceProvider(ConfigurationProvider.class, JsMinifyTestProvider.class);
   }

   /**
    * Ignored on Wildlfy because there seems to be some issue with the content length.
    * 
    * @see https://github.com/ocpsoft/rewrite/issues/145
    */
   @Test
   @Category(IgnoreForWildfly.class)
   public void testJavaScriptCompression() throws Exception
   {
      HttpAction<HttpGet> action = get("/test.js");
      assertEquals(200, action.getResponse().getStatusLine().getStatusCode());
      assertEquals("var text=\"hello\";alert(text);", action.getResponseContent());
   }

   @Test
   @Category(IgnoreForWildfly.class)
   public void testNotExistingSourceFile() throws Exception
   {
      HttpAction<HttpGet> action = get("/not-existing.js");
      assertEquals(404, action.getResponse().getStatusLine().getStatusCode());
   }

}
