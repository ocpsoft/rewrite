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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map.Entry;

import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.faces.spi.JoinResourcePathResolver;

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

   public static Navigate to(String viewId)
   {
      Assert.notNull(viewId, "viewId must not be null");
      return new Navigate(viewId);
   }

   public static Navigate to(Class<?> clazz)
   {

      Assert.notNull(clazz, "clazz must not be null");

      @SuppressWarnings("unchecked")
      Iterable<JoinResourcePathResolver> resolvers = ServiceLoader.load(JoinResourcePathResolver.class);

      for (JoinResourcePathResolver resolver : resolvers) {
         String viewId = resolver.byClass(clazz);
         if (viewId != null) {
            return new Navigate(viewId);
         }
      }

      throw new IllegalArgumentException("Unable to find the resource path for: " + clazz.getName());

   }

   public Navigate with(CharSequence name, Object value)
   {
      Assert.notNull(name, "name must not be null");
      if (value != null) {
         parameters.put(name.toString(), value.toString());
      }
      return this;
   }

   public Navigate withoutRedirect()
   {
      redirect = false;
      return this;
   }

   public String build()
   {

      ParameterMap query = parameters.copy();

      if (redirect) {
         query.put("faces-redirect", "true");
      }

      StringBuilder outcome = new StringBuilder();
      outcome.append(viewId);

      boolean first = true;
      for (Entry<String, List<String>> param : query.entrySet()) {

         for (String value : param.getValue()) {

            outcome.append(first ? '?' : '&');

            outcome.append(encodeQuery(param.getKey()));
            outcome.append('=');
            outcome.append(encodeQuery(value));

            first = false;

         }

      }

      return outcome.toString();

   }

   private static String encodeQuery(String str)
   {
      try {
         return URLEncoder.encode(str, "UTF-8");
      }
      catch (UnsupportedEncodingException e) {
         throw new IllegalArgumentException(e);
      }
   }

   @Override
   public String toString()
   {
      return build();
   }

}
