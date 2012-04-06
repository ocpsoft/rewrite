package org.ocpsoft.rewrite.util;

import java.lang.reflect.Array;
import java.util.Collection;

import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.bind.Converter;
import org.ocpsoft.rewrite.bind.Validator;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.exception.RewriteException;

public final class ValueHolderUtil
{
   private static final Logger log = Logger.getLogger(ValueHolderUtil.class);

   public static Validator<?> resolveValidator(final Class<? extends Validator<?>> type)
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

   public static final Converter<?> resolveConverter(final Class<? extends Converter<?>> type)
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
