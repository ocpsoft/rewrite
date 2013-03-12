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

import java.util.Arrays;
import java.util.List;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * Abstract builder for fluently defining new composite {@link Operation} instances.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class DefaultOperationBuilder implements OperationBuilder
{

   @Override
   public OperationBuilder and(final Operation other)
   {
      if (other == null)
         return this;
      return new DefaultCompositeOperation(this, other);
   }

   static class DefaultCompositeOperation extends DefaultOperationBuilder implements CompositeOperation
   {
      private final Operation left;
      private final Operation right;

      public DefaultCompositeOperation(final Operation left, final Operation right)
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

      @Override
      public List<Operation> getOperations()
      {
         return Arrays.asList(left, right);
      }

      @Override
      public String toString()
      {
         return DefaultCompositeOperation.class.getSimpleName() + " [left=" + left + ", right=" + right + "]";
      }
   }
}
