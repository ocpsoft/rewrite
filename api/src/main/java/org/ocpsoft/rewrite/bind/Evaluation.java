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
import org.ocpsoft.rewrite.param.Parameter;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Evaluation implements Binding
{
   private final String property;

   private Evaluation(final String property)
   {
      this.property = property;
   }

   public static Evaluation property(final String property)
   {
      return new Evaluation(property);
   }

   @Override
   public Object submit(final Rewrite event, final EvaluationContext context, final Object value)
   {
      String unconvertedName = getParameterUnconvertedName(property);
      if (!context.containsKey(property))
      {
         if (value.getClass().isArray())
            storeValue(event, context, unconvertedName, value);
         else
            storeValue(event, context, unconvertedName, new Object[] { value });
      }
      else
      {
         Object[] values = (Object[]) context.get(unconvertedName);
         List<Object> list = Arrays.asList(values);

         if (value.getClass().isArray())
            list.addAll(Arrays.asList((Object[]) value));
         else
            list.add(value);

         storeValue(event, context, unconvertedName, list.toArray());
      }

      return null;
   }

   public Object submitConverted(final Rewrite event, final EvaluationContext context, final Object value)
   {
      String convertedName = getParameterConvertedName(property);
      if (!context.containsKey(property))
      {
         if (value != null && value.getClass().isArray())
            storeValue(event, context, convertedName, value);
         else
            storeValue(event, context, convertedName, new Object[] { value });
      }
      else
      {
         Object[] values = (Object[]) context.get(convertedName);
         List<Object> list = Arrays.asList(values);

         if (value.getClass().isArray())
            list.addAll(Arrays.asList((Object[]) value));
         else
            list.add(value);

         storeValue(event, context, convertedName, list.toArray());
      }

      return null;
   }

   private void storeValue(final Rewrite event, final EvaluationContext context, final String key, final Object value)
   {
      context.put(key, value);
   }

   public boolean hasValue(final Rewrite event, final EvaluationContext context)
   {
      return context.get(getParameterUnconvertedName(property)) != null;
   }

   private String getParameterUnconvertedName(final String parameter)
   {
      return Evaluation.class.getName() + "_" + parameter;
   }

   private String getParameterConvertedName(final String parameter)
   {
      return getParameterUnconvertedName(parameter) + "_converted";
   }

   @Override
   public Object retrieve(final Rewrite event, final EvaluationContext context)
   {
      return retrieveFromProperty(context, getParameterUnconvertedName(property));
   }

   public Object retrieveConverted(Rewrite inbound, EvaluationContext context, Parameter<?> parameter)
   {
      return retrieveFromProperty(context, getParameterConvertedName(property));
   }

   private Object retrieveFromProperty(final EvaluationContext context, String propertyName)
   {
      Object object = context.get(propertyName);

      if (object == null)
      {
         throw new IllegalArgumentException("Attempted to access the non-existent " + converted(propertyName)
                  + " EvaluationContext property \"{"
                  + removePropertyNamespace(propertyName) + "}\"");
      }

      if (object.getClass().isArray())
      {
         Object[] values = (Object[]) object;
         if (values.length == 1)
         {
            return values[0];
         }
      }
      return object;
   }

   private String converted(String propertyName)
   {
      return propertyName.endsWith("_converted") ? "converted" : "";
   }

   private String removePropertyNamespace(String propertyName)
   {
      String result = propertyName;
      if (propertyName.startsWith(getClass().getName()))
      {
         result = result.substring(getClass().getName().length() + 1);
      }
      if (result.endsWith("_converted"))
      {
         result = result.substring(0, result.length() - "_converted".length() - 1);
      }
      return result;
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

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((property == null) ? 0 : property.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Evaluation other = (Evaluation) obj;
      if (property == null) {
         if (other.property != null)
            return false;
      }
      else if (!property.equals(other.property))
         return false;
      return true;
   }

}
