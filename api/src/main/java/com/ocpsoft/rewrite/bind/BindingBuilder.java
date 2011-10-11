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

import java.util.Collection;

import com.ocpsoft.common.services.ServiceLoader;
import com.ocpsoft.logging.Logger;
import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.exception.RewriteException;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class BindingBuilder implements Binding, RetrievalBuilder, SubmissionBuilder
{
   private Converter<?> converter = new DefaultConverter();
   private Validator<?> validator = new DefaultValidator();
   private final Logger log = Logger.getLogger(BindingBuilder.class);

   @Override
   public BindingBuilder convertedBy(final Class<? extends Converter<?>> type)
   {
      this.converter = resolveConverter(type);
      return this;
   }

   @Override
   public BindingBuilder convertedBy(final Converter<?> converter)
   {
      this.converter = converter;
      return this;
   }

   @Override
   public BindingBuilder validatedBy(final Class<? extends Validator<?>> type)
   {
      this.validator = resolveValidator(type);
      return this;
   }

   @Override
   public BindingBuilder validatedBy(final Validator<?> validator)
   {
      this.validator = validator;
      return this;
   }

   @Override
   @SuppressWarnings({ "unchecked", "rawtypes" })
   public boolean validates(final Rewrite event, final EvaluationContext context, final Object value)
   {
      // We must assume the value was properly converted.
      return ((Validator) validator).validate(event, context, value);
   }

   @Override
   public Object convert(final Rewrite event, final EvaluationContext context, final Object value)
   {
      return converter.convert(event, context, value);
   }

   private Validator<?> resolveValidator(final Class<? extends Validator<?>> type)
   {
      try {
         Collection<? extends Validator<?>> enriched = ServiceLoader.loadEnriched(type);
         if (enriched != null)
         {
            if ((enriched.size() > 1) && log.isWarnEnabled())
            {
               log.warn("Multiple Validator instances available for type [" + type.getName() + "], using first of "
                        + enriched + "");
            }
            for (Validator<?> validator : enriched) {
               if (validator != null)
               {
                  return validator;
               }
            }
         }
         return null;
      }
      catch (Exception e) {
         throw new RewriteException("Could not instantiate Validator of type [" + type.getName() + "]", e);
      }
   }

   private Converter<?> resolveConverter(final Class<? extends Converter<?>> type)
   {
      try {
         Collection<? extends Converter<?>> enriched = ServiceLoader.loadEnriched(type);
         if (enriched != null)
         {
            if ((enriched.size() > 1) && log.isWarnEnabled())
            {
               log.warn("Multiple Converter instances available for type [" + type.getName() + "], using first of "
                        + enriched + "");
            }
            for (Converter<?> converter : enriched) {
               if (converter != null)
               {
                  return converter;
               }
            }
         }
         return null;
      }
      catch (Exception e) {
         throw new RewriteException("Could not instantiate Converter of type [" + type.getName() + "]", e);
      }
   }
}
