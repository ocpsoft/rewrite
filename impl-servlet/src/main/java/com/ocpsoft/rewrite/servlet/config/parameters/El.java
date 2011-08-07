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
package com.ocpsoft.rewrite.servlet.config.parameters;

import com.ocpsoft.rewrite.EvaluationContext;
import com.ocpsoft.rewrite.config.Operation;
import com.ocpsoft.rewrite.servlet.config.HttpOperation;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * TODO arquillian test
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class El extends ParameterBindingBuilder
{
   private final String property;

   public El(final String property)
   {
      this.property = property;
   }

   public static El property(final String property)
   {
      return new El(property);
   }

   public static El property(final String property, final Class<? extends Converter<?>> type)
   {
      El el = new El(property);
      el.convertedBy(type);
      return el;
   }

   public static El property(final String property, final Class<? extends Converter<?>> converterType,
            final Class<? extends Validator<?>> validatorType)
   {
      El el = new El(property);
      el.convertedBy(converterType);
      el.validatedBy(validatorType);
      return el;
   }

   private class ElBindingOperation extends HttpOperation
   {
      private final String property;
      private final Object value;

      public ElBindingOperation(final String property, final Object value)
      {
         this.property = property;
         this.value = value;
      }

      @Override
      public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
      {
         // TODO perform EL injection via ServiceLoader lookup for EL Injection Providers
      }
   }

   @Override
   public Operation getOperation(final HttpServletRewrite event, final EvaluationContext context, final Object value)
   {
      return new ElBindingOperation(property, value);
   }

   @Override
   public Object extractBoundValue(final HttpServletRewrite event, final EvaluationContext context)
   {
      // TODO Extract EL value via ServiceLoader lookup for EL Injection Providers
      return null;
   }
}
