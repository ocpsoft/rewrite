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
package org.ocpsoft.rewrite.config;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.ocpsoft.rewrite.Root;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteIT;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public class RelocatingConfigurationLoaderIT extends RewriteIT
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      WebArchive deployment = RewriteIT
               .getDeployment()
               .addPackages(true, Root.class.getPackage())
               .addAsServiceProvider(ConfigurationProvider.class,
                        RelocatingConfigurationProvider1.class, RelocatingConfigurationProvider2.class,
                        RelocatingConfigurationProvider3.class);
      return deployment;
   }

   @Test
   public void testRelocatedRuleExecutesInNewOrderUp() throws Exception
   {
      HttpAction action = get("/priority");
      assertThat(action.getStatusCode()).isEqualTo(201);
   }

   @Test
   public void testRelocatedRuleExecutesInNewOrderDown() throws Exception
   {
      HttpAction action = get("/priority2");
      assertThat(action.getStatusCode()).isEqualTo(202);
   }

   @Test
   public void testRelocatedRuleExecutesInNewOrderUpUp() throws Exception
   {
      HttpAction action = get("/priority3");
      assertThat(action.getStatusCode()).isEqualTo(203);
   }

   @Test
   public void testRelocatedRulePriorityOverlapFollowsProviderPriorityOrder() throws Exception
   {
      HttpAction action = get("/priority4");
      assertThat(action.getStatusCode()).isEqualTo(202);
   }

}
