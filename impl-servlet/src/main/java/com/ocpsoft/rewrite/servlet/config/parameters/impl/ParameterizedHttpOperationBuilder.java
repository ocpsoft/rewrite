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
import com.ocpsoft.rewrite.config.Operation;
import com.ocpsoft.rewrite.config.OperationBuilder;
import com.ocpsoft.rewrite.servlet.config.HttpOperation;
import com.ocpsoft.rewrite.servlet.config.ParameterizedHttpOperation;
import com.ocpsoft.rewrite.servlet.config.parameters.Parameter;
import com.ocpsoft.rewrite.servlet.config.parameters.ParameterBinding;
import com.ocpsoft.rewrite.servlet.config.parameters.Parameterized;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ParameterizedHttpOperationBuilder extends HttpOperation implements
         Parameterized<ParameterizedHttpOperationBuilder>
{
   private final ParameterizedHttpOperation<ParameterizedHttpOperationBuilder> parent;
   private final Parameter parameter;

   private ParameterizedHttpOperationBuilder(
            final ParameterizedHttpOperation<ParameterizedHttpOperationBuilder> parent, final Parameter parameter)
   {
      this.parent = parent;
      this.parameter = parameter;
   }

   public static ParameterizedHttpOperationBuilder create(
            final ParameterizedHttpOperation<ParameterizedHttpOperationBuilder> parent,
            final Parameter parameter)
   {
      return new ParameterizedHttpOperationBuilder(parent, parameter);
   }

   @Override
   public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      parent.performHttp(event, context);
   }

   /*
    * ParameterBuilder 
    */
   public ParameterizedHttpOperationBuilder matches(final String pattern)
   {
      parameter.matches(pattern);
      return this;
   }

   public ParameterizedHttpOperationBuilder bindsTo(final ParameterBinding binding)
   {
      parameter.bindsTo(binding);
      return this;
   }

   public ParameterizedHttpOperationBuilder attemptBindTo(final ParameterBinding binding)
   {
      parameter.attemptBindTo(binding);
      return this;
   }

   /*
    * Parameterized<?>
    */
   public ParameterizedHttpOperationBuilder where(final String param)
   {
      return parent.where(param);
   }

   public ParameterizedHttpOperationBuilder where(final String param, final String pattern)
   {
      return parent.where(param, pattern);
   }

   public ParameterizedHttpOperationBuilder where(final String param, final String pattern,
            final ParameterBinding binding)
   {
      return parent.where(param, pattern, binding);
   }

   public ParameterizedHttpOperationBuilder where(final String param, final ParameterBinding binding)
   {
      return parent.where(param, binding);
   }

   /*
    * HttpOperation
    */
   @Override
   public OperationBuilder and(final Operation other)
   {
      return parent.and(other);
   }
}
