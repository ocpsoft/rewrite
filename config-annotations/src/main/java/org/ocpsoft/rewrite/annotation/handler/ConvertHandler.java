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
import org.ocpsoft.rewrite.annotation.spi.ConverterProvider;
import org.ocpsoft.rewrite.annotation.spi.FieldAnnotationHandler;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.BindingBuilder;
import org.ocpsoft.rewrite.bind.Converter;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

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
   @SuppressWarnings({ "rawtypes", "unchecked" })
   public void process(FieldContext context, Convert annotation, HandlerChain chain)
   {
      Field field = context.getJavaField();

      // locate the binding previously created by @ParameterBinding
      Binding binding = (Binding) context.get(Binding.class);
      if (binding != null) {

         Assert.assertTrue(binding instanceof BindingBuilder,
                  "Found Binding which is not a BindingBuilder but: " + binding.getClass().getSimpleName());
         BindingBuilder bindingBuilder = (BindingBuilder) binding;

         // add the converter
         Class<?> converterType = annotation.with();
         LazyConverterAdapter converter = new LazyConverterAdapter(converterType);
         bindingBuilder.convertedBy(converter);

         // some logging
         if (log.isTraceEnabled()) {
            log.trace("Attached converter adapter for [{}] to field [{}] of class [{}]", new Object[] {
                     converterType.getSimpleName(), field.getName(), field.getDeclaringClass().getName()
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

      private final Class<?> converterClass;

      public LazyConverterAdapter(Class<?> clazz)
      {
         this.converterClass = clazz;
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
            converter = provider.getByType(converterClass);
            if (converter != null) {
               break;
            }
         }
         Assert.notNull(converter, "Got no result from any ConverterProvider for type: " + converterClass.getName());

         return converter.convert(event, context, value);

      }
   }
}
