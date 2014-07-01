/*
 * Copyright 2013 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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

import java.util.HashSet;
import java.util.Set;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.InboundRewrite;
import org.ocpsoft.rewrite.event.OutboundRewrite;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;

/**
 * Utility for creating and wrapping {@link Operation} instances.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Operations
{
   /**
    * Return a new {@link DefaultOperationBuilder} that takes no action when
    * {@link #perform(Rewrite, EvaluationContext)} is invoked.
    */
   public static OperationBuilder create()
   {
      return new NoOp();
   }

   /**
    * Wrap the given {@link Operation} in a new {@link InboundOperation} which will invoke the wrapped operation only
    * for inbound rewrites.
    */
   public static Operation onInbound(final Operation operation)
   {
      return new DefaultOperationBuilderInternal(operation) {

         @Override
         public void perform(Rewrite event, EvaluationContext context)
         {
            if (event instanceof InboundRewrite)
               operation.perform(event, context);
         }

         @Override
         public String toString()
         {
            return "Operations.onInbound(" + operation + ")";
         }
      };
   }

   /**
    * Wrap the given {@link Operation} in a new {@link Operation} which will invoke the wrapped operation only for
    * outbound rewrites.
    */
   public static Operation onOutbound(final Operation operation)
   {
      return new DefaultOperationBuilderInternal(operation) {

         @Override
         public void perform(Rewrite event, EvaluationContext context)
         {
            if (event instanceof OutboundRewrite)
               operation.perform(event, context);
         }

         @Override
         public String toString()
         {
            return "Operations.onOutbound(" + operation + ")";
         }
      };
   }

   /**
    * Wrap a given {@link Operation} as a new {@link DefaultOperationBuilder} that performs the action of the original
    * {@link Operation} when {@link #perform(Rewrite, EvaluationContext)} is invoked.
    */
   public static OperationBuilder wrap(final Operation operation)
   {
      if (operation == null)
         return create();
      if (operation instanceof OperationBuilder)
         return (OperationBuilder) operation;

      return new DefaultOperationBuilderInternal(operation) {
         @Override
         public void perform(Rewrite event, EvaluationContext context)
         {
            operation.perform(event, context);
         }
      };
   }

   /**
    * An operation that is only performed if the current {@link org.ocpsoft.rewrite.event.Rewrite} event is an
    * {@link OutboundRewrite} event.
    * 
    * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
    */
   private abstract static class DefaultOperationBuilderInternal extends DefaultOperationBuilder implements
            Parameterized
   {
      private Operation operation;

      public DefaultOperationBuilderInternal(Operation operation)
      {
         this.operation = operation;
      }

      @Override
      public Set<String> getRequiredParameterNames()
      {
         Set<String> result = new HashSet<String>();
         if (operation instanceof Parameterized)
            result.addAll(((Parameterized) operation).getRequiredParameterNames());
         return result;
      }

      @Override
      public void setParameterStore(ParameterStore store)
      {
         if (operation instanceof Parameterized)
            ((Parameterized) operation).setParameterStore(store);
      }
   }

}
