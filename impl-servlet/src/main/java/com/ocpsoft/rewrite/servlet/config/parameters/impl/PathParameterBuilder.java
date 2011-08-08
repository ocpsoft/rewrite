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
import com.ocpsoft.rewrite.servlet.config.Path;
import com.ocpsoft.rewrite.servlet.config.parameters.Parameter;
import com.ocpsoft.rewrite.servlet.config.parameters.ParameterBinding;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class PathParameterBuilder extends HttpCondition
{
   private final Path parent;
   private final Parameter parameter;

   private PathParameterBuilder(final Path parent, final Parameter parameter)
   {
      this.parent = parent;
      this.parameter = parameter;
   }

   public static PathParameterBuilder create(final Path parent,
            final Parameter parameter)
   {
      return new PathParameterBuilder(parent, parameter);
   }

   /*
    * ParameterBuilder 
    */
   public PathParameterBuilder matches(final String pattern)
   {
      parameter.matches(pattern);
      return this;
   }

   public PathParameterBuilder bindsTo(final ParameterBinding binding)
   {
      parameter.bindsTo(binding);
      return this;
   }

   public PathParameterBuilder attemptBindTo(final ParameterBinding binding)
   {
      parameter.attemptBindTo(binding);
      return this;
   }

   /*
    * Parameterized<?>
    */
   public PathParameterBuilder where(final String param)
   {
      return parent.where(param);
   }

   public PathParameterBuilder where(final String param, final String pattern)
   {
      return parent.where(param, pattern);
   }

   public PathParameterBuilder where(final String param, final String pattern, final ParameterBinding binding)
   {
      return parent.where(param, pattern, binding);
   }

   public PathParameterBuilder where(final String param, final ParameterBinding binding)
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
