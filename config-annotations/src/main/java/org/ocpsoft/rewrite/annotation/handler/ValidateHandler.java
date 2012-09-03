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
import org.ocpsoft.rewrite.annotation.Validate;
import org.ocpsoft.rewrite.annotation.api.FieldContext;
import org.ocpsoft.rewrite.annotation.api.HandlerChain;
import org.ocpsoft.rewrite.annotation.spi.FieldAnnotationHandler;
import org.ocpsoft.rewrite.annotation.spi.ValidatorProvider;
import org.ocpsoft.rewrite.bind.BindingBuilder;
import org.ocpsoft.rewrite.bind.Validator;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * Handler for {@link Validate}.
 * 
 * @author Christian Kaltepoth
 */
public class ValidateHandler extends FieldAnnotationHandler<Validate>
{
   private final Logger log = Logger.getLogger(ValidateHandler.class);

   @Override
   public Class<Validate> handles()
   {
      return Validate.class;
   }

   @Override
   public int priority()
   {
      return HandlerWeights.WEIGHT_TYPE_ENRICHING;
   }

   @Override
   @SuppressWarnings({ "rawtypes", "unchecked" })
   public void process(FieldContext context, Validate annotation, HandlerChain chain)
   {
      Field field = context.getJavaField();

      // locate the binding previously created by @ParameterBinding
      BindingBuilder bindingBuilder = (BindingBuilder) context.get(BindingBuilder.class);
      Assert.notNull(bindingBuilder, "No binding found for field: " + field);

      // add the validator
      Class<?> validatorType = annotation.with();
      LazyValidatorAdapter validator = new LazyValidatorAdapter(validatorType);
      bindingBuilder.validatedBy(validator);

      // some logging
      if (log.isTraceEnabled()) {
         log.trace("Attached validator adapeter for [{}] to field [{}] of class [{}]", new Object[] {
                  validatorType.getSimpleName(), field.getName(), field.getDeclaringClass().getName()
         });
      }

      // continue with the chain
      chain.proceed();

   }

   /**
    * This class uses the {@link ValidatorProvider} SPI to lazily obtain the {@link Validator} for a given {@link Class}
    * instance.
    */
   private static class LazyValidatorAdapter implements Validator<Object>
   {

      private final Class<?> validatorClass;

      public LazyValidatorAdapter(Class<?> clazz)
      {
         this.validatorClass = clazz;
      }

      @Override
      @SuppressWarnings({ "rawtypes", "unchecked" })
      public boolean validate(Rewrite event, EvaluationContext context, Object value)
      {

         Validator validator = null;

         // let one of the SPI implementations build the validator
         Iterator<ValidatorProvider> providers = ServiceLoader.load(ValidatorProvider.class).iterator();
         while (providers.hasNext()) {
            validator = providers.next().getByType(validatorClass);
            if (validator != null) {
               break;
            }
         }
         Assert.notNull(validator, "Could not build validator for type: " + validatorClass.getName());

         return validator.validate(event, context, value);

      }
   }
}
