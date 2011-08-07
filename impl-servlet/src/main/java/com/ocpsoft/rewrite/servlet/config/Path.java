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
import com.ocpsoft.rewrite.servlet.config.parameters.CompiledPath;
import com.ocpsoft.rewrite.servlet.config.parameters.ParameterBinding;
import com.ocpsoft.rewrite.servlet.config.parameters.PathParameter;
import com.ocpsoft.rewrite.servlet.config.parameters.PathParameterBuilder;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import com.ocpsoft.rewrite.util.Assert;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Path extends HttpCondition
{
   private final CompiledPath pattern;

   protected Path(final String pattern, final PathParameter... params)
   {
      Assert.notNull(pattern, "URL pattern must not be null.");
      this.pattern = new CompiledPath(this, pattern);

      if (params != null)
         for (PathParameter param : params) {
            if (this.pattern.getParameters().containsKey(param.getName()))
            {
               this.pattern.getParameters().put(param.getName(), param);
            }
         }
   }

   public static Path matches(final String pattern)
   {
      return new Path(pattern);
   }

   public static Path matches(final String pattern, final PathParameter... params)
   {
      return new Path(pattern, params);
   }

   public PathParameterBuilder and(final String param)
   {
      return PathParameterBuilder.create(this, pattern.getParameter(param));
   }

   public PathParameterBuilder and(final String param, final String pattern)
   {
      return and(param).matches(pattern);
   }

   public PathParameterBuilder and(final String param, final String pattern, final ParameterBinding binding)
   {
      return and(param, pattern).bindsTo(binding);
   }

   public PathParameterBuilder and(final String param, final ParameterBinding binding)
   {
      return and(param).bindsTo(binding);
   }

   @Override
   public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      // TODO need to do conversion and validation for bound parameters here as well
      if (pattern.matches(event.getRequestURL()))
      {
         List<Operation> operations = new ArrayList<Operation>();
         Map<PathParameter, String> parameters = pattern.parseEncoded(event.getRequestURL());
         for (Entry<PathParameter, String> entry : parameters.entrySet()) {
            PathParameter parameter = entry.getKey();
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
            context.addPostOperation(operation);
         }
         return true;
      }
      return false;
   }

   public Path withRequestParamBinding()
   {
      // TODO go through all parameters and set up request param binding
      return this;
   }
}