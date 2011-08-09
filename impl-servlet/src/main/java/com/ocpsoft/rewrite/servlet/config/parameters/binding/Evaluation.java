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

import java.util.Arrays;
import java.util.List;

import com.ocpsoft.rewrite.config.Operation;
import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.servlet.config.parameters.Converter;
import com.ocpsoft.rewrite.servlet.config.parameters.ParameterBindingBuilder;
import com.ocpsoft.rewrite.servlet.config.parameters.Validator;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * // TODO arquillian test
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class Evaluation extends ParameterBindingBuilder
{
   private final String property;

   private Evaluation(final String property)
   {
      this.property = property;
   }

   public static Evaluation property(final String property)
   {
      return new Evaluation(property);
   }

   public static Evaluation property(final String property, final Class<? extends Converter<?>> type)
   {
      Evaluation evaluation = property(property);
      evaluation.convertedBy(type);
      return evaluation;
   }

   public static Evaluation property(final String property, final Class<Converter<?>> converterType,
            final Class<? extends Validator<?>> validatorType)
   {
      Evaluation evaluation = property(property, converterType);
      evaluation.validatedBy(validatorType);
      return evaluation;
   }

   @Override
   public Operation getOperation(final HttpServletRewrite event, final EvaluationContext context, final Object value)
   {
      return new EvaluationContextBindingOperation(property, value);
   }

   private class EvaluationContextBindingOperation implements Operation
   {
      private final String parameter;
      private final Object value;

      public EvaluationContextBindingOperation(final String parameter, final Object value)
      {
         this.parameter = parameter;
         this.value = value;
      }

      @Override
      public void perform(final Rewrite event, final EvaluationContext context)
      {
         if (!context.containsKey(parameter))
         {
            if (value.getClass().isArray())
               context.put(getParameterName(parameter), value);
            else
               context.put(getParameterName(parameter), new Object[] { value });
         }
         else
         {
            Object[] values = (Object[]) context.get(getParameterName(parameter));
            List<Object> list = Arrays.asList(values);

            if (value.getClass().isArray())
               list.addAll(Arrays.asList((Object[]) value));
            else
               list.add(value);

            context.put(getParameterName(parameter), list.toArray());
         }
      }
   }

   public String getParameterName(final String parameter)
   {
      return Evaluation.class.getName() + parameter;
   }

   @Override
   public Object extractBoundValue(final HttpServletRewrite event, final EvaluationContext context)
   {
      return context.get(getParameterName(property));
   }

}
