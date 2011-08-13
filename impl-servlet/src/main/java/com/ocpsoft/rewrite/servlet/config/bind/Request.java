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
package com.ocpsoft.rewrite.servlet.config.bind;

import java.util.Map;

import com.ocpsoft.rewrite.bind.BindingBuilder;
import com.ocpsoft.rewrite.bind.Converter;
import com.ocpsoft.rewrite.bind.Validator;
import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import com.ocpsoft.rewrite.servlet.impl.HttpRewriteWrappedRequest;
import com.ocpsoft.rewrite.servlet.util.Maps;

/**
 * // TODO arquillian test
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class Request extends BindingBuilder
{
   private final String parameter;

   private Request(final String property)
   {
      this.parameter = property;
   }

   public static BindingBuilder parameter(final String property)
   {
      return new Request(property);
   }

   public static BindingBuilder parameter(final String property, final Class<? extends Converter<?>> type)
   {
      return parameter(property).convertedBy(type);
   }

   public static BindingBuilder parameter(final String property, final Class<Converter<?>> converterType,
            final Class<? extends Validator<?>> validatorType)
   {
      return parameter(property, converterType).validatedBy(validatorType);
   }

   @Override
   public Object submit(final Rewrite event, final EvaluationContext context, final Object value)
   {
      HttpRewriteWrappedRequest wrapper = (HttpRewriteWrappedRequest) ((HttpServletRewrite) event).getRequest()
               .getAttribute(HttpRewriteWrappedRequest.class.getName());

      Map<String, String[]> modifiableParameters = wrapper.getModifiableParameters();
      if (value.getClass().isArray())
      {
         Object[] values = (Object[]) value;
         for (Object object : values) {
            Maps.addArrayValue(modifiableParameters, parameter, object.toString());
         }
      }
      else
      {
         Maps.addArrayValue(modifiableParameters, parameter, value.toString());
      }

      return null;
   }

   @Override
   public Object retrieve(final Rewrite event, final EvaluationContext context)
   {
      if (event instanceof HttpServletRewrite)
         return ((HttpServletRewrite) event).getRequest().getParameter(parameter);

      else
         return null;
   }

   @Override
   public boolean supportsRetrieval()
   {
      return true;
   }

   @Override
   public boolean supportsSubmission()
   {
      return true;
   }
}
