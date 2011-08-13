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

import com.ocpsoft.rewrite.bind.Binding;
import com.ocpsoft.rewrite.config.Operation;
import com.ocpsoft.rewrite.config.OperationBuilder;
import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.servlet.config.parameters.ParameterizedOperation;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class OperationParameterBuilder implements ParameterizedOperation<OperationParameterBuilder>
{
   private final ParameterizedOperation<OperationParameterBuilder> parent;
   private final Parameter parameter;

   public OperationParameterBuilder(final ParameterizedOperation<OperationParameterBuilder> parent,
            final Parameter parameter)
   {
      this.parent = parent;
      this.parameter = parameter;
   }

   /**
    * The {@link Parameter} must match the given pattern.
    */
   public OperationParameterBuilder matches(final String pattern)
   {
      parameter.matches(pattern);
      return this;
   }

   /**
    * The {@link Parameter} binds to the given {@link Binding}
    */
   public OperationParameterBuilder bindsTo(final Binding binding)
   {
      parameter.bindsTo(binding);
      return this;
   }

   @Override
   public OperationParameterBuilder where(final String param)
   {
      return parent.where(param);
   }

   @Override
   public OperationParameterBuilder where(final String param, final String pattern)
   {
      return parent.where(param, pattern);
   }

   @Override
   public OperationParameterBuilder where(final String param, final String pattern, final Binding binding)
   {
      return parent.where(param, pattern, binding);
   }

   @Override
   public OperationParameterBuilder where(final String param, final Binding binding)
   {
      return parent.where(param, binding);
   }

   @Override
   public OperationBuilder and(final Operation other)
   {
      return parent.and(other);
   }

   @Override
   public void perform(final Rewrite event, final EvaluationContext context)
   {
      parent.perform(event, context);
   }
}
