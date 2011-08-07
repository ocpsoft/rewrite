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

import java.util.Map;

import com.ocpsoft.rewrite.EvaluationContext;
import com.ocpsoft.rewrite.config.Operation;
import com.ocpsoft.rewrite.servlet.config.HttpOperation;
import com.ocpsoft.rewrite.servlet.config.parameters.Converter;
import com.ocpsoft.rewrite.servlet.config.parameters.ParameterBindingBuilder;
import com.ocpsoft.rewrite.servlet.config.parameters.Validator;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import com.ocpsoft.rewrite.servlet.http.impl.HttpRewriteWrappedRequest;
import com.ocpsoft.rewrite.servlet.util.Maps;

/**
 * // TODO arquillian test
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class Request extends ParameterBindingBuilder
{
   private final String parameter;

   private Request(final String property)
   {
      this.parameter = property;
   }

   public static Request parameter(final String property)
   {
      return new Request(property);
   }

   public static Request parameter(final String property, final Class<? extends Converter<?>> type)
   {
      Request request = parameter(property);
      request.convertedBy(type);
      return request;
   }

   public static Request parameter(final String property, final Class<Converter<?>> converterType,
            final Class<? extends Validator<?>> validatorType)
   {
      Request request = parameter(property, converterType);
      request.validatedBy(validatorType);
      return request;
   }

   @Override
   public Operation getOperation(final HttpServletRewrite event, final EvaluationContext context, final Object value)
   {
      return new RequestParameterBindingOperation(parameter, value);
   }

   private class RequestParameterBindingOperation extends HttpOperation
   {
      private final String parameter;
      private final Object value;

      public RequestParameterBindingOperation(final String parameter, final Object value)
      {
         this.parameter = parameter;
         this.value = value;
      }

      @Override
      public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
      {
         HttpRewriteWrappedRequest wrapper = (HttpRewriteWrappedRequest) event.getRequest().getAttribute(
                  HttpRewriteWrappedRequest.class.getName());
         Map<String, String[]> modifiableParameters = wrapper.getModifiableParameters();
         Maps.addArrayValue(modifiableParameters, parameter, value.toString());
      }
   }

   @Override
   public Object extractBoundValue(final HttpServletRewrite event, final EvaluationContext context)
   {
      return event.getRequest().getParameter(parameter);
   }
}
