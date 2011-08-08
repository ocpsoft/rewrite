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
package com.ocpsoft.rewrite.servlet.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ocpsoft.rewrite.EvaluationContext;
import com.ocpsoft.rewrite.config.Operation;
import com.ocpsoft.rewrite.servlet.config.parameters.Parameter;
import com.ocpsoft.rewrite.servlet.config.parameters.ParameterBinding;
import com.ocpsoft.rewrite.servlet.config.parameters.ParameterizedCondition;
import com.ocpsoft.rewrite.servlet.config.parameters.impl.ConditionParameterBuilder;
import com.ocpsoft.rewrite.servlet.config.parameters.impl.ParameterizedExpression;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import com.ocpsoft.rewrite.util.Assert;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Path extends HttpCondition implements ParameterizedCondition
{
   private final ParameterizedExpression path;

   private Path(final String pattern)
   {
      Assert.notNull(pattern, "Path must not be null.");
      this.path = new ParameterizedExpression(pattern);
   }

   public static Path matches(final String pattern)
   {
      return new Path(pattern);
   }

   public ConditionParameterBuilder where(final String param)
   {
      return new ConditionParameterBuilder(this, path.getParameter(param));
   }

   public ConditionParameterBuilder where(final String param, final String pattern)
   {
      return where(param).matches(pattern);
   }

   public ConditionParameterBuilder where(final String param, final String pattern,
            final ParameterBinding binding)
   {
      return where(param, pattern).bindsTo(binding);
   }

   public ConditionParameterBuilder where(final String param, final ParameterBinding binding)
   {
      return where(param).bindsTo(binding);
   }

   @Override
   public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      if (path.matches(event.getRequestURL()))
      {
         List<Operation> operations = new ArrayList<Operation>();
         Map<Parameter, String> parameters = path.parseEncoded(event.getRequestURL());
         for (Entry<Parameter, String> entry : parameters.entrySet()) {
            Parameter parameter = entry.getKey();
            List<ParameterBinding> bindings = parameter.getBindings();
            for (ParameterBinding binding : bindings) {
               try {
                  Object value = binding.convert(event, context, entry.getValue());
                  if (binding.validates(event, context, value))
                  {
                     operations.add(binding.getOperation(event, context, value));
                  }
                  else
                  {
                     return false;
                  }
               }
               catch (Exception e) {
                  return false;
               }
            }
         }

         for (Operation operation : operations) {
            context.addPreOperation(operation);
         }
         return true;
      }
      return false;
   }
}