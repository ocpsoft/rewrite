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
package com.ocpsoft.rewrite.config;

import com.ocpsoft.rewrite.bind.Binding;
import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.logging.Logger;
import com.ocpsoft.rewrite.services.ServiceLoader;
import com.ocpsoft.rewrite.spi.InvocationResultHandler;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class Invoke extends OperationBuilder
{
   private static final Logger log = Logger.getLogger(Invoke.class);
   private final Binding binding;
   private final Binding valueBinding;

   private Invoke(final Binding property, final Binding valueBinding)
   {
      this.binding = property;
      this.valueBinding = valueBinding;
   }

   @Override
   @SuppressWarnings("unchecked")
   public void perform(final Rewrite event, final EvaluationContext context)
   {
      Object result;
      if (valueBinding == null)
      {
         result = binding.retrieve(event, context);
      }
      else
      {
         Object converted = binding.convert(event, context, valueBinding.retrieve(event, context));
         result = binding.submit(event, context, converted);
      }

      log.info("Invoked binding [" + binding + "] returned value [" + result + "]");

      ServiceLoader<InvocationResultHandler> providers = ServiceLoader.load(InvocationResultHandler.class);

      for (InvocationResultHandler handler : providers) {
         handler.handle(event, context, result);
      }
   }

   /**
    * Invoke the given {@link Binding} and process {@link InvocationResultHandler} instances on the result value (if
    * any.)
    */
   public static OperationBuilder retrieveFrom(final Binding property)
   {
      return new Invoke(property, null);
   }

   /**
    * 
    * Invoke {@link Binding#submit(Rewrite, EvaluationContext, Object)}, use the result of the given
    * {@link Binding#retrieve(Rewrite, EvaluationContext)} as the value for this submission. Process
    * {@link InvocationResultHandler} instances on the result value (if any.)
    */
   public static OperationBuilder submitTo(final Binding to, final Binding from)
   {
      return new Invoke(to, from);
   }

}
