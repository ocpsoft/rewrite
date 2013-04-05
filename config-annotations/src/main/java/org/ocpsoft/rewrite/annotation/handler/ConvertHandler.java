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
package org.ocpsoft.rewrite.annotation.handler;

import java.lang.reflect.Field;
import java.util.Iterator;

import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.common.util.Assert;
import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.annotation.Convert;
import org.ocpsoft.rewrite.annotation.api.FieldContext;
import org.ocpsoft.rewrite.annotation.api.HandlerChain;
import org.ocpsoft.rewrite.annotation.spi.FieldAnnotationHandler;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.exception.RewriteException;
import org.ocpsoft.rewrite.param.Converter;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.param.ParameterConfiguration;
import org.ocpsoft.rewrite.spi.ConverterProvider;

/**
 * Handler for {@link Convert}.
 * 
 * @author Christian Kaltepoth
 */
public class ConvertHandler extends FieldAnnotationHandler<Convert>
{
   private final Logger log = Logger.getLogger(ConvertHandler.class);

   @Override
   public Class<Convert> handles()
   {
      return Convert.class;
   }

   @Override
   public int priority()
   {
      return HandlerWeights.WEIGHT_TYPE_ENRICHING;
   }

   @Override
   public void process(FieldContext context, Convert annotation, HandlerChain chain)
   {
      Field field = context.getJavaField();

      Parameter<?> parameter = (Parameter<?>) context.get(Parameter.class);
      if (parameter != null) {

         Converter<?> converter = null;

         // identify converter by the type of the converter
         if (annotation.with() != Object.class) {
            converter = LazyConverterAdapter.forConverterType(annotation.with());
         }

         // identify converter by some kind of unique id
         else if (annotation.id().length() > 0) {
            converter = LazyConverterAdapter.forConverterId(annotation.id());
         }

         // default: identify converter by the target type
         else {
            converter = LazyConverterAdapter.forTargetType(field.getType());
         }

         if (parameter instanceof ParameterConfiguration)
            ((ParameterConfiguration<?>) parameter).convertedBy(converter);
         else
            throw new RewriteException("Cannot add @" + Convert.class.getSimpleName() + " to [" + field
                     + "] of class [" + field.getDeclaringClass() + "] because the parameter ["
                     + parameter.getName() + "] is not writable.");

         if (log.isTraceEnabled()) {
            log.trace("Attached converter to field [{}] of class [{}]: ", new Object[] {
                     field.getName(), field.getDeclaringClass().getName(), converter
            });
         }

      }

      // continue with the chain
      chain.proceed();

   }

   /**
    * This class uses the {@link ConverterProvider} SPI to lazily obtain the {@link Converter} for a given {@link Class}
    * instance.
    */
   private static class LazyConverterAdapter implements Converter<Object>
   {

      private final Class<?> targetType;
      private final String converterId;
      private final Class<?> converterType;

      private LazyConverterAdapter(Class<?> targetType, String converterId, Class<?> converterType)
      {
         this.targetType = targetType;
         this.converterId = converterId;
         this.converterType = converterType;
      }

      public static LazyConverterAdapter forConverterType(Class<?> converterType)
      {
         return new LazyConverterAdapter(null, null, converterType);
      }

      public static LazyConverterAdapter forConverterId(String id)
      {
         return new LazyConverterAdapter(null, id, null);
      }

      public static LazyConverterAdapter forTargetType(Class<?> targetType)
      {
         return new LazyConverterAdapter(targetType, null, null);
      }

      @Override
      @SuppressWarnings("unchecked")
      public Object convert(Rewrite event, EvaluationContext context, Object value)
      {

         Converter<?> converter = null;

         // let one of the SPI implementations build the converter
         Iterator<ConverterProvider> providers = ServiceLoader.load(ConverterProvider.class).iterator();
         while (providers.hasNext()) {
            ConverterProvider provider = providers.next();

            if (targetType != null) {
               converter = provider.getByTargetType(targetType);
            }
            else if (converterType != null) {
               converter = provider.getByConverterType(converterType);
            }
            else {
               converter = provider.getByConverterId(converterId);
            }

            if (converter != null) {
               break;
            }

         }
         Assert.notNull(converter, "Got no converter from any ConverterProvider for: " + this.toString());

         return converter.convert(event, context, value);

      }

      @Override
      public String toString()
      {
         StringBuilder b = new StringBuilder();
         b.append(this.getClass().getSimpleName());
         b.append(" for ");
         if (targetType != null) {
            b.append(" target type ");
            b.append(targetType.getName());
         }
         else if (converterType != null) {
            b.append(" converter type ");
            b.append(converterType.getName());
         }
         else {
            b.append(" id ");
            b.append(converterId);
         }
         return b.toString();
      }

   }
}
