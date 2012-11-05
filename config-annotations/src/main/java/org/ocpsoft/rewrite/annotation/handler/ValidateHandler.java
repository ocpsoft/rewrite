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
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.BindingBuilder;
import org.ocpsoft.rewrite.bind.Validator;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.spi.ValidatorProvider;

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
      Binding binding = (Binding) context.get(Binding.class);
      if (binding != null) {

         Assert.assertTrue(binding instanceof BindingBuilder,
                  "Found Binding which is not a BindingBuilder but: " + binding.getClass().getSimpleName());
         BindingBuilder bindingBuilder = (BindingBuilder) binding;

         Validator<?> validator = null;

         // identify validator by the type of the validator
         if (annotation.with() != Object.class) {
            validator = LazyValidatorAdapter.forValidatorType(annotation.with());
         }

         // identify validator by some kind of unique id
         else if (annotation.id().length() > 0) {
            validator = LazyValidatorAdapter.forValidatorId(annotation.id());
         }

         // default: identify validator by the target type
         else {
            validator = LazyValidatorAdapter.forTargetType(field.getType());
         }

         bindingBuilder.validatedBy(validator);

         if (log.isTraceEnabled()) {
            log.trace("Attached validator to field [{}] of class [{}]: ", new Object[] {
                     field.getName(), field.getDeclaringClass().getName(), validator
            });
         }

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

      private final Class<?> targetType;
      private final String validatorId;
      private final Class<?> validatorType;

      private LazyValidatorAdapter(Class<?> targetType, String validatorId, Class<?> validatorType)
      {
         this.targetType = targetType;
         this.validatorId = validatorId;
         this.validatorType = validatorType;
      }

      public static LazyValidatorAdapter forValidatorType(Class<?> validatorType)
      {
         return new LazyValidatorAdapter(null, null, validatorType);
      }

      public static LazyValidatorAdapter forValidatorId(String id)
      {
         return new LazyValidatorAdapter(null, id, null);
      }

      public static LazyValidatorAdapter forTargetType(Class<?> targetType)
      {
         return new LazyValidatorAdapter(targetType, null, null);
      }

      @Override
      @SuppressWarnings({ "rawtypes", "unchecked" })
      public boolean validate(Rewrite event, EvaluationContext context, Object value)
      {

         Validator validator = null;

         // let one of the SPI implementations build the validator
         Iterator<ValidatorProvider> providers = ServiceLoader.load(ValidatorProvider.class).iterator();
         while (providers.hasNext()) {
            ValidatorProvider provider = providers.next();

            if (targetType != null) {
               validator = provider.getByTargetType(targetType);
            }
            else if (validatorType != null) {
               validator = provider.getByValidatorType(validatorType);
            }
            else {
               validator = provider.getByValidatorId(validatorId);
            }

            if (validator != null) {
               break;
            }

         }
         Assert.notNull(validator, "Got no validator from any ValidatorProvider for: " + this.toString());

         return validator.validate(event, context, value);

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
         else if (validatorType != null) {
            b.append(" validator type ");
            b.append(validatorType.getName());
         }
         else {
            b.append(" id ");
            b.append(validatorId);
         }
         return b.toString();
      }
   }
}
