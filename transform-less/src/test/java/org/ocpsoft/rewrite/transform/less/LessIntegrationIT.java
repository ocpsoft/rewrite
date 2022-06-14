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
package org.ocpsoft.rewrite.transform.less;

import java.io.File;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.category.IgnoreForWildfly;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteIT;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 
 * Integration test for {@link Less}.
 * 
 * @author Christian Kaltepoth
 * 
 */
@RunWith(Arquillian.class)
public class LessIntegrationIT extends RewriteIT
{
   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteIT.getDeployment()
               .addAsWebResource(new StringAsset(".class { width: 1 + 2 }"), "test.less")
               .addAsLibraries(getTransformArchive())
               .addAsLibraries(resolveDependency("org.mozilla:rhino"))
               .addClasses(LessIntegrationTestProvider.class)
               .addAsServiceProvider(ConfigurationProvider.class, LessIntegrationTestProvider.class);
   }

   protected static JavaArchive getTransformArchive()
   {
      JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "rewrite-transform.jar")

               .addAsResource(new File("../transform/target/classes/org"));
      
      return archive;
   }

   @Test
   public void testSimpleLessFileRendering() throws Exception
   {
      HttpAction action = get("/test.css");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).contains("width: 3");
   }

   @Test
   @Category(IgnoreForWildfly.class)
   public void testNotExistingLessFile() throws Exception
   {
      HttpAction action = get("/not-existing.css");
      assertThat(action.getStatusCode()).isEqualTo(404);
   }

}
