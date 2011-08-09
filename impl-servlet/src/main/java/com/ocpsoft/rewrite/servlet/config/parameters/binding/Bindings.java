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
package com.ocpsoft.rewrite.servlet.config.parameters.binding;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ocpsoft.rewrite.config.Operation;
import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.servlet.config.parameters.DefaultBindable;
import com.ocpsoft.rewrite.servlet.config.parameters.ParameterBinding;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class Bindings
{
   public static void evaluateCondition(final HttpServletRewrite event, final EvaluationContext context,
            final DefaultBindable bindable, final Object value)
   {
      Map<DefaultBindable, Object> map = new LinkedHashMap<DefaultBindable, Object>();
      map.put(bindable, value);
      evaluateCondition(event, context, map);
   }

   public static void evaluateCondition(final HttpServletRewrite event, final EvaluationContext context,
            final Map<? extends DefaultBindable, ? extends Object> parameters)
   {
      List<Operation> operations = new ArrayList<Operation>();
      for (Entry<? extends DefaultBindable, ? extends Object> entry : parameters.entrySet()) {

         DefaultBindable parameter = entry.getKey();
         Object value = entry.getValue();

         List<ParameterBinding> bindings = parameter.getBindings();
         for (ParameterBinding binding : bindings) {
            try {
               value = binding.convert(event, context, value);
               if (binding.validates(event, context, value))
               {
                  operations.add(binding.getOperation(event, context, value));
               }
               else
               {
                  return;
               }
            }
            catch (Exception e) {
               return;
            }
         }
      }

      for (Operation operation : operations) {
         context.addPreOperation(operation);
      }
   }
}
