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

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * Abstract builder for fluently defining new composite {@link Operation} instances.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class OperationBuilder implements Operation
{

   /**
    * Return a new {@link OperationBuilder} that takes no action when {@link #perform(Rewrite, EvaluationContext)} is
    * invoked.
    */
   public static OperationBuilder create()
   {
      return new OperationBuilder() {
         @Override
         public void perform(Rewrite event, EvaluationContext context)
         {}
      };
   }

   /**
    * Wrap a given {@link Operation} as a new {@link OperationBuilder} that performs the action of the original
    * {@link Operation} when {@link #perform(Rewrite, EvaluationContext)} is invoked.
    */
   public static OperationBuilder wrap(Operation operation)
   {
      return new CompositeOperation(OperationBuilder.create(), operation);
   }

   /**
    * Join this instance with another {@link Operation} to be performed. All joined operations are guaranteed to be
    * evaluated. Operations are evaluated in the order in which they are added.
    */
   public OperationBuilder and(final Operation other)
   {
      return new CompositeOperation(this, other);
   }

   private static class CompositeOperation extends OperationBuilder
   {
      private final Operation left;
      private final Operation right;

      public CompositeOperation(final Operation left, final Operation right)
      {
         this.left = left;
         this.right = right;
      }

      @Override
      public void perform(final Rewrite event, final EvaluationContext context)
      {
         left.perform(event, context);
         right.perform(event, context);
      }

   }
}
