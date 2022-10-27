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

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteIT;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 
 * Integration test for {@link CssMinify}.
 * 
 * @author Christian Kaltepoth
 * 
 */
@RunWith(Arquillian.class)
public class CssMinifyIT extends RewriteIT
{
   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteIT.getDeployment()
               .addAsWebResource(new StringAsset(".class {\n  width : 100px;\n}"), "test.css")
               .addAsLibraries(getTransformArchive())
               .addAsLibraries(resolveDependency("com.yahoo.platform.yui:yuicompressor"))
               .addAsLibraries(resolveDependencies("de.larsgrefer.sass:sass-embedded-host"))
               .addAsLibraries(resolveDependencies("de.larsgrefer.sass:sass-embedded-protocol"))
               .addClasses(CssMinifyTestProvider.class)
               .addAsServiceProvider(ConfigurationProvider.class, CssMinifyTestProvider.class);
   }

   protected static JavaArchive getTransformArchive()
   {
      JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "rewrite-transform.jar")

               .addAsResource(new File("../transform/target/classes/org"));

      return archive;
   }
   
   @Test
   public void testCssFileCompression() throws Exception
   {
      HttpAction action = get("/test.css");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).isEqualTo(".class{width:100px}");
   }

   @Test
   public void testNotExistingSourceFile() throws Exception
   {
      HttpAction action = get("/not-existing.css");
      assertThat(action.getStatusCode()).isEqualTo(404);
   }

}
