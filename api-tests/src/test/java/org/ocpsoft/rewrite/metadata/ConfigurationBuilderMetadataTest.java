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

import static org.assertj.core.api.Assertions.assertThat;

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

      assertThat(config.getRules().size()).isEqualTo(1);
      Rule rule = config.getRules().get(0);
      assertThat(rule instanceof Context).isTrue();

      String location = (String) ((Context) rule).get(RuleMetadata.PROVIDER_LOCATION);
      assertThat(location).isEqualTo("org.ocpsoft.rewrite.metadata.ConfigurationBuilderMetadataTest"
              + ".testBuildConfigurationMetadata"
              + "(ConfigurationBuilderMetadataTest.java:39)");
   }

   @Test
   public void testBuildConfigurationMetadataWrappedRule()
   {
      Configuration config = ConfigurationBuilder.begin().addRule(RuleBuilder.define());

      assertThat(config.getRules().size()).isEqualTo(1);
      Rule rule = config.getRules().get(0);
      assertThat(rule instanceof Context).isTrue();

      String location = (String) ((Context) rule).get(RuleMetadata.PROVIDER_LOCATION);
      assertThat(location).isEqualTo("org.ocpsoft.rewrite.metadata.ConfigurationBuilderMetadataTest"
              + ".testBuildConfigurationMetadataWrappedRule"
              + "(ConfigurationBuilderMetadataTest.java:61)");
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

      assertThat(config.getRules().size()).isEqualTo(2);

      for (Rule rule : config.getRules()) {

         assertThat(rule instanceof Context).isTrue();

         String location = (String) ((Context) rule).get(RuleMetadata.PROVIDER_LOCATION);
         assertThat(location).isEqualTo("org.ocpsoft.rewrite.metadata.ConfigurationBuilderMetadataTest"
                 + ".testBuildConfigurationMetadataOtherwise"
                 + "(ConfigurationBuilderMetadataTest.java:78)");
      }
   }
}
