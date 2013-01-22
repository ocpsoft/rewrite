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
package org.ocpsoft.rewrite.bind;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.util.ValueHolderUtil;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class BindingBuilder<IMPLTYPE extends BindingBuilder<IMPLTYPE, VALUETYPE>, VALUETYPE> implements Binding, Retrieval,
Submission, Convertable<IMPLTYPE>, Validatable<IMPLTYPE>, HasConverter, HasValidator
{
   private Converter<?> converter = new DefaultConverter();
   private Validator<?> validator = new DefaultValidator();

   @Override
   @SuppressWarnings("unchecked")
   public <X extends Converter<?>> IMPLTYPE convertedBy(Class<X> type)
   {
      this.converter = ValueHolderUtil.resolveConverter(type);
      return (IMPLTYPE) this;
   }

   @Override
   @SuppressWarnings("unchecked")
   public IMPLTYPE convertedBy(Converter<?> converter)
   {
      this.converter = converter;
      return (IMPLTYPE) this;
   }

   @Override
   public Converter<?> getConverter()
   {
      return converter;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <X extends Validator<?>> IMPLTYPE validatedBy(Class<X> type)
   {
      this.validator = ValueHolderUtil.resolveValidator(type);
      return (IMPLTYPE) this;
   }

   @Override
   @SuppressWarnings("unchecked")
   public IMPLTYPE validatedBy(Validator<?> validator)
   {
      this.validator = validator;
      return (IMPLTYPE) this;
   }

   @Override
   public Validator<?> getValidator()
   {
      return validator;
   }

   @Override
   public Object convert(Rewrite event, EvaluationContext context, Object value)
   {
      return ValueHolderUtil.convert(event, context, converter, value);
   }

   @Override
   public boolean validate(Rewrite event, EvaluationContext context, Object value)
   {
      return ValueHolderUtil.validates(event, context, validator, value);
   }

}
