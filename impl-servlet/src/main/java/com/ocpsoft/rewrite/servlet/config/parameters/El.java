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

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class El implements ParameterBinding
{
   private final String property;
   private Converter converter;
   private Validator validator;

   public El(final String property)
   {
      this.property = property;
   }

   public static El property(final String property)
   {
      return new El(property);
   }

   @Override
   public ParameterBinding using(final Class<? extends Converter> type)
   {
      this.converter = resolveConverter(type);
      return this;
   }

   @Override
   public ParameterBinding validatedBy(final Class<? extends Validator> type)
   {
      this.validator = resolveValidator(type);
      return null;
   }

   private Validator resolveValidator(final Class<? extends Validator> type)
   {
      return null;
   }

   private Converter resolveConverter(final Class<? extends Converter> type)
   {
      return null;
   }

   public static El property(final String string, final Class<Converter> type)
   {
      return null;
   }

   public static El property(final String string, final Class<Converter> converterType,
            final Class<Validator> validatorType)
   {
      return null;
   }
}
