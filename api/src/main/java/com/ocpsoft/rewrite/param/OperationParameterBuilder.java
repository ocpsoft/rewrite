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
package com.ocpsoft.rewrite.param;

import com.ocpsoft.rewrite.bind.Binding;
import com.ocpsoft.rewrite.config.Operation;
import com.ocpsoft.rewrite.config.OperationBuilder;
import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.event.Rewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class OperationParameterBuilder<P extends OperationParameterBuilder<P, T>, T> implements
         ParameterizedOperation<OperationParameterBuilder<P, T>, T>
{
   private final ParameterizedOperation<OperationParameterBuilder<P, T>, T> parent;
   private final Parameter<T> parameter;

   public OperationParameterBuilder(final ParameterizedOperation<OperationParameterBuilder<P, T>, T> parent,
            final Parameter<T> parameter)
   {
      this.parent = parent;
      this.parameter = parameter;
   }

   /**
    * The {@link Parameter} must meet the given {@link Constraint}.
    */
   @SuppressWarnings("unchecked")
   public P constrainedBy(final Constraint<T> constraint)
   {
      parameter.constrainedBy(constraint);
      return (P) this;
   }

   /**
    * Apply the given {@link Transform} to this {@link Parameter}; it will be applied in the order in which it was
    * added. All transforms are applied before {@link Binding} occurs.
    */
   @SuppressWarnings("unchecked")
   public P transformedBy(final Transform<T> constraint)
   {
      parameter.transformedBy(constraint);
      return (P) this;
   }

   /**
    * The {@link Parameter} binds to the given {@link Binding}.
    */
   @SuppressWarnings("unchecked")
   public P bindsTo(final Binding binding)
   {
      parameter.bindsTo(binding);
      return (P) this;
   }

   @Override
   @SuppressWarnings("unchecked")
   public P where(final String param)
   {
      return (P) parent.where(param);
   }

   @Override
   @SuppressWarnings("unchecked")
   public P where(final String param, final T pattern)
   {
      return (P) parent.where(param, pattern);
   }

   @Override
   @SuppressWarnings("unchecked")
   public P where(final String param, final T pattern, final Binding binding)
   {
      return (P) parent.where(param, pattern, binding);
   }

   @Override
   @SuppressWarnings("unchecked")
   public P where(final String param, final Binding binding)
   {
      return (P) parent.where(param, binding);
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
