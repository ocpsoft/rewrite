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

import org.junit.Test;
import org.ocpsoft.common.pattern.Weighted;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.context.RewriteState;
import org.ocpsoft.rewrite.event.Rewrite;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public class RuleBuilderTest
{
   private final EvaluationContext context = new EvaluationContext() {

      @Override
      public void clear()
      {}

      @Override
      public void put(Object key, Object value)
      {}

      @Override
      public Object get(Object key)
      {
         return null;
      }

      @Override
      public boolean containsKey(Object key)
      {
         return false;
      }

      @Override
      public void addPreOperation(Operation operation)
      {}

      @Override
      public void addPostOperation(Operation operation)
      {}

      @Override
      public RewriteState getState()
      {
         return null;
      }
   };

   @Test
   public void testRuleBuilderDefaultsToAlwaysPerform()
   {
      assertThat(RuleBuilder.define().evaluate(null, context)).isTrue();
   }

   @Test
   public void testRuleBuilderIsRelocatable()
   {
      RuleBuilder ruleBuilder = RuleBuilder.define();
      assertThat(ruleBuilder).isInstanceOf(Weighted.class);
      assertThat(ruleBuilder).isInstanceOf(Rule.class);
      assertThat(ruleBuilder).isInstanceOf(Relocatable.class);
      assertThat(ruleBuilder).isInstanceOf(RelocatableRule.class);
   }

   @Test
   public void testRuleBuilderCreatedFromRule()
   {
      Rule rule = new Rule() {
         @Override
         public String getId()
         {
            return null;
         }

         @Override
         public boolean evaluate(Rewrite event, EvaluationContext context)
         {
            return false;
         }

         @Override
         public void perform(Rewrite event, EvaluationContext context)
         {}
      };

      RuleBuilder ruleBuilder = RuleBuilder.wrap(rule);

      assertThat(ruleBuilder.getId()).isEqualTo(null);
      assertThat(ruleBuilder.priority()).isEqualTo(0);
   }

}
