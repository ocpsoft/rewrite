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

package org.ocpsoft.rewrite.servlet.impl;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.rewrite.servlet.RewriteWrappedRequest;
import org.ocpsoft.rewrite.util.CompositeMap;

/**
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public class HttpRewriteWrappedRequest extends RewriteWrappedRequest
{
   private final Map<String, String[]> modifiableParameters;
   private CompositeMap<String, String[]> allParameters;

   /**
    * Create a new request wrapper that will merge additional parameters into the request object without prematurely
    * reading parameters from the original request.
    */
   public HttpRewriteWrappedRequest(final HttpServletRequest request, final Map<String, String[]> additionalParams)
   {
      super(request);

      modifiableParameters = new TreeMap<String, String[]>();
      modifiableParameters.putAll(additionalParams);

      setCurrentInstance(this);
   }

   @SuppressWarnings("unchecked")
   public Map<String, String[]> getNativeParameters()
   {
      return super.getParameterMap();
   }

   @Override
   @SuppressWarnings("unchecked")
   public Map<String, String[]> getParameterMap()
   {
      allParameters = new CompositeMap<String, String[]>()
               .addDelegate(modifiableParameters)
               .addDelegate(super.getParameterMap());
      return Collections.unmodifiableMap(allParameters);
   }

   @Override
   public Map<String, String[]> getModifiableParameters()
   {
      return modifiableParameters;
   }

   /*
    * HttpServletRequest overrides
    */

   @Override
   public String getParameter(final String name)
   {
      String[] strings = getParameterMap().get(name);
      if (strings != null && strings.length > 0)
      {
         return strings[0];
      }
      return super.getParameter(name);
   }

   @Override
   public Enumeration<String> getParameterNames()
   {
      return Collections.enumeration(getParameterMap().keySet());
   }

   @Override
   public String[] getParameterValues(final String name)
   {
      return getParameterMap().get(name);
   }

   @Override
   public String toString()
   {
      return super.getRequestURL().toString();
   }
}
