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
import com.ocpsoft.rewrite.servlet.config.HttpCondition;
import com.ocpsoft.rewrite.servlet.config.ParameterizedHttpCondition;
import com.ocpsoft.rewrite.servlet.config.parameters.Parameter;
import com.ocpsoft.rewrite.servlet.config.parameters.ParameterBinding;
import com.ocpsoft.rewrite.servlet.config.parameters.Parameterized;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ParameterizedHttpConditionBuilder extends HttpCondition implements Parameterized<ParameterizedHttpConditionBuilder>
{
   private final ParameterizedHttpCondition<ParameterizedHttpConditionBuilder> parent;
   private final Parameter parameter;

   private ParameterizedHttpConditionBuilder(final ParameterizedHttpCondition<ParameterizedHttpConditionBuilder> parent, final Parameter parameter)
   {
      this.parent = parent;
      this.parameter = parameter;
   }

   public static ParameterizedHttpConditionBuilder create(final ParameterizedHttpCondition<ParameterizedHttpConditionBuilder> parent,
            final Parameter parameter)
   {
      return new ParameterizedHttpConditionBuilder(parent, parameter);
   }

   /*
    * ParameterBuilder 
    */
   public ParameterizedHttpConditionBuilder matches(final String pattern)
   {
      parameter.matches(pattern);
      return this;
   }

   public ParameterizedHttpConditionBuilder bindsTo(final ParameterBinding binding)
   {
      parameter.bindsTo(binding);
      return this;
   }

   public ParameterizedHttpConditionBuilder attemptBindTo(final ParameterBinding binding)
   {
      parameter.attemptBindTo(binding);
      return this;
   }

   /*
    * Parameterized<?>
    */
   public ParameterizedHttpConditionBuilder where(final String param)
   {
      return parent.where(param);
   }

   public ParameterizedHttpConditionBuilder where(final String param, final String pattern)
   {
      return parent.where(param, pattern);
   }

   public ParameterizedHttpConditionBuilder where(final String param, final String pattern, final ParameterBinding binding)
   {
      return parent.where(param, pattern, binding);
   }

   public ParameterizedHttpConditionBuilder where(final String param, final ParameterBinding binding)
   {
      return parent.where(param, binding);
   }

   /*
    * HttpCondition
    */
   @Override
   public ConditionBuilder and(final Condition condition)
   {
      return parent.and(condition);
   }

   @Override
   public ConditionBuilder andNot(final Condition condition)
   {
      return parent.andNot(condition);
   }

   @Override
   public ConditionBuilder or(final Condition condition)
   {
      return parent.or(condition);
   }

   @Override
   public ConditionBuilder orNot(final Condition condition)
   {
      return parent.orNot(condition);
   }

   @Override
   public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      return parent.evaluateHttp(event, context);
   }

}
