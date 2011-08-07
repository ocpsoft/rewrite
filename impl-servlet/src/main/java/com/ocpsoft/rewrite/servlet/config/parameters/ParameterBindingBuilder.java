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
import com.ocpsoft.rewrite.servlet.config.parameters.impl.DefaultConverter;
import com.ocpsoft.rewrite.servlet.config.parameters.impl.DefaultValidator;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public abstract class ParameterBindingBuilder implements ParameterBinding
{
   private Converter<?> converter = new DefaultConverter();
   private Validator<?> validator = new DefaultValidator();

   public ParameterBindingBuilder convertedBy(final Class<? extends Converter<?>> type)
   {
      this.converter = resolveConverter(type);
      return this;
   }

   public ParameterBindingBuilder validatedBy(final Class<? extends Validator<?>> type)
   {
      this.validator = resolveValidator(type);
      return this;
   }

   @Override
   @SuppressWarnings({ "unchecked", "rawtypes" })
   public boolean validates(final HttpServletRewrite event, final EvaluationContext context, final Object value)
   {
      return ((Validator) getValidator()).validate(event, context, value);
   }

   @Override
   public Object convert(final HttpServletRewrite event, final EvaluationContext context, final String value)
   {
      return getConverter().convert(event, context, value);
   }

   private Validator<?> resolveValidator(final Class<? extends Validator<?>> type)
   {
      // TODO resolve validator and pass to service enrichers
      return null;
   }

   private Converter<?> resolveConverter(final Class<? extends Converter<?>> type)
   {
      // TODO resolve converter and pass to service enrichers
      return null;
   }

   public Converter<?> getConverter()
   {
      return converter;
   }

   public Validator<?> getValidator()
   {
      return validator;
   }
}
