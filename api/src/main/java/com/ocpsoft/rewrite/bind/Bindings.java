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
package com.ocpsoft.rewrite.bind;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ocpsoft.rewrite.config.Operation;
import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.exception.RewriteException;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class Bindings
{
   public static void performSubmission(final Rewrite event, final EvaluationContext context,
            final Bindable bindable, final Object value)
   {
      Map<Bindable, Object> map = new LinkedHashMap<Bindable, Object>();
      map.put(bindable, value);
      performSubmissions(event, context, map);
   }

   public static void performSubmissions(final Rewrite event, final EvaluationContext context,
            final Map<? extends Bindable, ? extends Object> parameters)
   {
      List<Operation> operations = new ArrayList<Operation>();
      for (Entry<? extends Bindable, ? extends Object> entry : parameters.entrySet()) {

         Bindable parameter = entry.getKey();
         Object value = entry.getValue();

         List<Binding> bindings = parameter.getBindings();
         for (Binding binding : bindings) {
            try {
               value = binding.convert(event, context, value);
               if (binding.validates(event, context, value))
               {
                  operations.add(new BindingOperation(binding, value));
               }
               else
               {
                  return;
               }
            }
            catch (Exception e) {
               throw new RewriteException("Failed to bind value [" + value + "] to binding [" + binding + "]", e);
            }
         }
      }

      for (Operation operation : operations) {
         context.addPreOperation(operation);
      }
   }

   private static class BindingOperation implements Operation
   {

      private final Binding binding;
      private final Object value;

      public BindingOperation(final Binding binding, final Object value)
      {
         this.binding = binding;
         this.value = value;
      }

      @Override
      public void perform(final Rewrite event, final EvaluationContext context)
      {
         binding.submit(event, context, value);
      }

   }
}
