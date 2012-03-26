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

import java.util.Arrays;
import java.util.List;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * // TODO arquillian test
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class Evaluation extends BindingBuilder
{
   private final CharSequence property;

   private Evaluation(final CharSequence property)
   {
      this.property = property;
   }

   public static BindingBuilder property(final CharSequence property)
   {
      return new Evaluation(property);
   }

   public static BindingBuilder property(final CharSequence property, final Class<? extends Converter<?>> type)
   {
      return property(property).convertedBy(type);
   }

   public static BindingBuilder property(final CharSequence property, final Class<Converter<?>> converterType,
            final Class<? extends Validator<?>> validatorType)
   {
      return property(property, converterType).validatedBy(validatorType);
   }

   @Override
   public Object submit(final Rewrite event, final EvaluationContext context, final Object value)
   {
      if (!context.containsKey(property))
      {
         if (value.getClass().isArray())
            context.put(getParameterName(property), value);
         else
            context.put(getParameterName(property), new Object[] { value });
      }
      else
      {
         Object[] values = (Object[]) context.get(getParameterName(property));
         List<Object> list = Arrays.asList(values);

         if (value.getClass().isArray())
            list.addAll(Arrays.asList((Object[]) value));
         else
            list.add(value);

         context.put(getParameterName(property), list.toArray());
      }

      return null;
   }

   private String getParameterName(final CharSequence parameter)
   {
      return Evaluation.class.getName() + parameter;
   }

   @Override
   public Object retrieve(final Rewrite event, final EvaluationContext context)
   {
      Object object = context.get(getParameterName(property));
      if (object.getClass().isArray())
      {
         Object[] array = (Object[]) object;
         if (array.length == 1)
         {
            return array[0];
         }
      }
      return object;
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

   @Override
   public String toString()
   {
      return "Evaluation [property=" + property + "]";
   }

}
