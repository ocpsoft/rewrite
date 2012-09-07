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
package org.ocpsoft.rewrite.faces.annotation.config;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.bind.Converter;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.faces.annotation.util.NullComponent;

/**
 * Adapter class that allows to use JSF converters with Rewrite.
 * 
 * @author Christian Kaltepoth
 */
public class FacesConverterAdapter<T> implements Converter<T>
{

   private final Class<?> targetType;
   private final String converterId;

   /**
    * Create an adapter for the specified target type. The adapter will use {@link Application#createConverter(Class)}
    * to obtain the underlying JSF converter.
    */
   public FacesConverterAdapter(Class<?> targetType)
   {
      this.targetType = targetType;
      this.converterId = null;
   }

   /**
    * Create an adapter for the specified converter ID. The adapter will use {@link Application#createConverter(String)}
    * to obtain the underlying JSF converter.
    */
   public FacesConverterAdapter(String converterId)
   {
      this.converterId = converterId;
      this.targetType = null;
   }

   @Override
   @SuppressWarnings("unchecked")
   public T convert(Rewrite event, EvaluationContext context, Object value)
   {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      Assert.notNull(facesContext, "FacesContext.getCurrentInstance() returned null. " +
               "You should use @Deferred so the binding gets executed within the JSF lifecycle.");

      javax.faces.convert.Converter converter = null;
      if (converterId != null) {
         converter = facesContext.getApplication().createConverter(converterId);
         Assert.notNull(converter, "Could not create JSF converter for ID: " + converterId);
      }
      else {
         converter = facesContext.getApplication().createConverter(targetType);
         Assert.notNull(converter, "Could not create JSF converter for type: " + targetType.getName());
      }

      String valueAsString = value != null ? value.toString() : null;
      try {
         return (T) converter.getAsObject(facesContext, new NullComponent(), valueAsString);
      }
      catch (ConverterException e) {
         return null; // TODO: is this correct
      }
   }

   @Override
   public String toString()
   {
      StringBuilder builder = new StringBuilder();
      builder.append("FacesConverterAdapter[");
      if (converterId != null) {
         builder.append("converterId=");
         builder.append(converterId);
      }
      else {
         builder.append("targetType=");
         builder.append(targetType.getName().toString());
      }
      builder.append("]");
      return builder.toString();
   }

}