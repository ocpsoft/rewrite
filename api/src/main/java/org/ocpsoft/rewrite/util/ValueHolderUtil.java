/*
 * Copyright 2013 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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
package org.ocpsoft.rewrite.util;

import java.lang.reflect.Array;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.Converter;
import org.ocpsoft.rewrite.param.Validator;

/**
 * Utility for interacting with {@link Validator} and {@link Converter} instances.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public final class ValueHolderUtil
{
   @SuppressWarnings({ "unchecked", "rawtypes" })
   public static boolean validates(final Rewrite event, final EvaluationContext context, Validator<?> validator,
            final Object value)
   {
      if (validator != null)
      {
         if (value != null && value.getClass().isArray())
         {
            Object[] values = (Object[]) value;
            for (int i = 0; i < values.length; i++) {
               if (!((Validator) validator).isValid(event, context, values[i]))
               {
                  return false;
               }
            }
         }
         else
            return ((Validator) validator).isValid(event, context, value);
      }
      return true;
   }

   public static Object convert(final Rewrite event, final EvaluationContext context, Converter<?> converter,
            final Object value)
   {
      if (converter != null)
      {
         if (value != null && value.getClass().isArray())
         {
            Object[] values = (Object[]) value;
            Object[] convertedValues = new Object[values.length];
            for (int i = 0; i < convertedValues.length; i++) {
               convertedValues[i] = converter.convert(event, context, values[i]);
            }

            Class<?> type = Object.class;
            for (Object object : convertedValues) {
               if (object != null)
               {
                  type = object.getClass();
                  break;
               }
            }

            Object[] result = (Object[]) Array.newInstance(type, convertedValues.length);
            System.arraycopy(convertedValues, 0, result, 0, result.length);
            return result;
         }
         else
            return converter.convert(event, context, value);
      }
      return value;
   }
}
