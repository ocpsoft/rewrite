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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteIT;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @see https://github.com/ocpsoft/rewrite/issues/81
 * @author Christian Kaltepoth
 */
@RunWith(Arquillian.class)
public class ResourceReadParamsInConditionIT extends RewriteIT
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteIT.getDeployment()
               .addClass(ResourceReadParamsInConditionProvider.class)
               .addAsWebResource(new StringAsset("some content"), "exists.txt")
               .addAsServiceProvider(ConfigurationProvider.class, ResourceReadParamsInConditionProvider.class);
   }

   @Test
   public void testParamReadsForMatchingCondition() throws Exception
   {
      HttpAction action = get("/exists.txt");
      assertThat(action.getStatusCode()).isEqualTo(210);
   }

   @Test
   public void testParamReadsForNotMatchingCondition() throws Exception
   {
      HttpAction action = get("/missing.txt");
      assertThat(action.getStatusCode()).isEqualTo(404);
   }

}