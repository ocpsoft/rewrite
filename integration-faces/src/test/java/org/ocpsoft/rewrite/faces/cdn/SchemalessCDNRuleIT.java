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
package org.ocpsoft.rewrite.faces.cdn;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.faces.test.FacesBase;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteITBase;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class SchemalessCDNRuleIT extends RewriteITBase
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return FacesBase.getDeployment()
               .addClass(SchemalessCDNRuleConfig.class)
               .addAsServiceProviderAndClasses(ConfigurationProvider.class, SchemalessCDNRuleConfig.class)
               .addAsWebResource(EmptyAsset.INSTANCE, "resources/jquery.js")
               .addAsWebResource("schemaless-cdn-page.xhtml", "page.xhtml");
   }

   @Test
   public void shouldRewriteToSchemalessURL() throws Exception
   {

      HttpAction action = get("/page.xhtml");

      assertThat(action.getResponseContent()).contains("src=\"//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js");

   }
}