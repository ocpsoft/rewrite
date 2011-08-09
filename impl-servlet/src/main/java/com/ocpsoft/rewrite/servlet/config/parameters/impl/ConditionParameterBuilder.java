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
package com.ocpsoft.rewrite.servlet.config.parameters.impl;

import com.ocpsoft.rewrite.EvaluationContext;
import com.ocpsoft.rewrite.config.Condition;
import com.ocpsoft.rewrite.config.ConditionBuilder;
import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.servlet.config.parameters.Parameter;
import com.ocpsoft.rewrite.servlet.config.parameters.ParameterBinding;
import com.ocpsoft.rewrite.servlet.config.parameters.ParameterizedCondition;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ConditionParameterBuilder implements Condition, ParameterizedCondition<ConditionParameterBuilder>
{
   private final ParameterizedCondition<ConditionParameterBuilder> parent;
   private final Parameter parameter;

   public ConditionParameterBuilder(final ParameterizedCondition<ConditionParameterBuilder> parent,
            final Parameter parameter)
   {
      this.parent = parent;
      this.parameter = parameter;
   }

   /*
    * ParameterBuilder 
    */
   public ConditionParameterBuilder matches(final String pattern)
   {
      parameter.matches(pattern);
      return this;
   }

   public ConditionParameterBuilder bindsTo(final ParameterBinding binding)
   {
      parameter.bindsTo(binding);
      return this;
   }

   /*
    * Parameterized<?>
    */
   public ConditionParameterBuilder where(final String param)
   {
      return parent.where(param);
   }

   public ConditionParameterBuilder where(final String param, final String pattern)
   {
      return parent.where(param, pattern);
   }

   public ConditionParameterBuilder where(final String param, final String pattern, final ParameterBinding binding)
   {
      return parent.where(param, pattern, binding);
   }

   public ConditionParameterBuilder where(final String param, final ParameterBinding binding)
   {
      return parent.where(param, binding);
   }

   public ConditionBuilder and(final Condition condition)
   {
      return parent.and(condition);
   }

   public ConditionBuilder andNot(final Condition condition)
   {
      return parent.andNot(condition);
   }

   public ConditionBuilder or(final Condition condition)
   {
      return parent.or(condition);
   }

   public ConditionBuilder orNot(final Condition condition)
   {
      return parent.orNot(condition);
   }

   @Override
   public boolean evaluate(final Rewrite event, final EvaluationContext context)
   {
      return parent.evaluate(event, context);
   }

}
