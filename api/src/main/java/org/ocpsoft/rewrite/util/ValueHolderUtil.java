package org.ocpsoft.rewrite.util;

import java.lang.reflect.Array;

import org.ocpsoft.rewrite.bind.Converter;
import org.ocpsoft.rewrite.bind.Validator;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

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
               if (!((Validator) validator).validate(event, context, values[i]))
               {
                  return false;
               }
            }
         }
         else
            return ((Validator) validator).validate(event, context, value);
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
