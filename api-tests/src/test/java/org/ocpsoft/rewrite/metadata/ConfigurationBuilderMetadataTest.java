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
package org.ocpsoft.rewrite.metadata;

import org.junit.Assert;
import org.junit.Test;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.config.RuleBuilder;
import org.ocpsoft.rewrite.config.RuleMetadata;
import org.ocpsoft.rewrite.context.Context;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ConfigurationBuilderMetadataTest
{
   @Test
   public void testBuildConfigurationMetadata()
   {
      Configuration config = ConfigurationBuilder.begin().addRule()
               .perform(new Operation() {
                  @Override
                  public void perform(Rewrite event, EvaluationContext context)
                  {}
               });

      Assert.assertEquals(1, config.getRules().size());
      Rule rule = config.getRules().get(0);
      Assert.assertTrue(rule instanceof Context);

      String location = (String) ((Context) rule).get(RuleMetadata.PROVIDER_LOCATION);
      Assert.assertEquals(
               "org.ocpsoft.rewrite.metadata.ConfigurationBuilderMetadataTest"
                        + ".testBuildConfigurationMetadata"
                        + "(ConfigurationBuilderMetadataTest.java:39)",
               location);
   }

   @Test
   public void testBuildConfigurationMetadataWrappedRule()
   {
      Configuration config = ConfigurationBuilder.begin().addRule(RuleBuilder.define());

      Assert.assertEquals(1, config.getRules().size());
      Rule rule = config.getRules().get(0);
      Assert.assertTrue(rule instanceof Context);

      String location = (String) ((Context) rule).get(RuleMetadata.PROVIDER_LOCATION);
      Assert.assertEquals(
               "org.ocpsoft.rewrite.metadata.ConfigurationBuilderMetadataTest"
                        + ".testBuildConfigurationMetadataWrappedRule"
                        + "(ConfigurationBuilderMetadataTest.java:61)",
               location);
   }

   @Test
   public void testBuildConfigurationMetadataOtherwise()
   {
      Configuration config = ConfigurationBuilder.begin().addRule()
               .perform(new Operation() {
                  @Override
                  public void perform(Rewrite event, EvaluationContext context)
                  {}
               })
               .otherwise(new Operation() {

                  @Override
                  public void perform(Rewrite event, EvaluationContext context)
                  {}
               });

      Assert.assertEquals(2, config.getRules().size());

      for (Rule rule : config.getRules()) {

         Assert.assertTrue(rule instanceof Context);

         String location = (String) ((Context) rule).get(RuleMetadata.PROVIDER_LOCATION);
         Assert.assertEquals(
                  "org.ocpsoft.rewrite.metadata.ConfigurationBuilderMetadataTest"
                           + ".testBuildConfigurationMetadataOtherwise"
                           + "(ConfigurationBuilderMetadataTest.java:78)",
                  location);
      }
   }
}
