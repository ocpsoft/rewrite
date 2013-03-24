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
package org.ocpsoft.rewrite.faces.navigate;

import java.util.List;
import java.util.Map.Entry;

import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.faces.RewriteNavigationHandler;
import org.ocpsoft.rewrite.servlet.spi.ResourcePathResolver;
import org.ocpsoft.urlbuilder.Address;
import org.ocpsoft.urlbuilder.AddressBuilder;
import org.ocpsoft.urlbuilder.AddressBuilderPath;

/**
 * Helper class to build JSF action outcomes.
 * 
 * @author Christian Kaltepoth
 */
public class Navigate
{
   private final String viewId;
   private final ParameterMap parameters = new ParameterMap();
   private boolean redirect = true;

   private Navigate(String viewId)
   {
      this.viewId = viewId;
   }

   /**
    * Create a new {@link Navigate} instance that will invoke navigation to the specified View ID.
    * <p>
    * Query parameters to be provided to the destination View ID may be supplied via {@link #with(CharSequence, Object)}.
    * 
    * @param viewId the destination View ID
    */
   public static Navigate to(String viewId)
   {
      Assert.notNull(viewId, "viewId must not be null");
      return new Navigate(viewId);
   }

   /**
    * Create a new {@link Navigate} instance that will invoke navigation to the specified {@link Class}. For instance,
    * if the class specifies a {@literal @}Join rule, this would generate a navigation case targeting the
    * {@link Address} for the defined resource.
    * <p>
    * Query parameters to be provided to the destination View ID may be supplied via {@link #with(CharSequence, Object)}.
    * 
    * @see ResourcePathResolver
    * 
    * @param clazz the target {@link Class}
    */
   public static Navigate to(Class<?> clazz)
   {

      Assert.notNull(clazz, "clazz must not be null");

      @SuppressWarnings("unchecked")
      Iterable<ResourcePathResolver> resolvers = ServiceLoader.load(ResourcePathResolver.class);

      for (ResourcePathResolver resolver : resolvers) {
         String viewId = resolver.resolveFrom(clazz);
         if (viewId != null) {
            return new Navigate(viewId);
         }
      }

      throw new IllegalArgumentException("Unable to find the resource path for: " + clazz.getName());
   }

   /**
    * Set a query parameter to be passed to the specified View Id.
    * 
    * @param The query parameter name.
    * @param The query parameter value.
    */
   public Navigate with(CharSequence name, Object value)
   {
      Assert.notNull(name, "name must not be null");
      if (value != null) {
         parameters.put(name.toString(), value.toString());
      }
      return this;
   }

   /**
    * Specify that navigation should be performed using JavaServer Faces non-redirecting navigation.
    */
   public Navigate withoutRedirect()
   {
      redirect = false;
      return this;
   }

   /**
    * Build and return the fully constructed navigation {@link String}.
    */
   public String build()
   {
      if (redirect) {
         return buildRedirectOutcome();
      }
      else {
         return buildStandardOutcome();
      }
   }

   /**
    * Builds a special outcome processed by {@link RewriteNavigationHandler}
    */
   private String buildRedirectOutcome()
   {
      AddressBuilderPath builderPath = AddressBuilder.begin().path(viewId);
      for (Entry<String, List<String>> param : parameters.entrySet()) {
         String[] values = param.getValue().toArray(new String[param.getValue().size()]);
         builderPath.query(param.getKey(), (Object[]) values);
      }
      String url = builderPath.toString();
      return RewriteNavigationHandler.REDIRECT_PREFIX + url;
   }

   /**
    * Builds a standard JSF 2.0 implicit navigation outcome
    */
   private String buildStandardOutcome()
   {
      StringBuilder outcome = new StringBuilder();
      outcome.append(viewId);

      boolean first = true;
      for (Entry<String, List<String>> param : parameters.entrySet()) {

         for (String value : param.getValue()) {

            outcome.append(first ? '?' : '&');

            outcome.append(param.getKey());
            outcome.append('=');
            outcome.append(value);

            first = false;

         }

      }

      return outcome.toString();
   }

   @Override
   public String toString()
   {
      return build();
   }

}
