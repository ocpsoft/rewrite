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

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.ocpsoft.rewrite.servlet.util.CompositeMap;

/**
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public class HttpRewriteWrappedRequest extends HttpServletRequestWrapper
{
   private final Map<String, String[]> modifiableParameters;
   private CompositeMap<String, String[]> allParameters;

   /**
    * Create a new request wrapper that will merge additional parameters into the request object without prematurely
    * reading parameters from the original request.
    */
   public HttpRewriteWrappedRequest(final HttpServletRequest request, final Map<String, String[]> additionalParams)
   {
      super((HttpServletRequest) (request instanceof ServletRequestWrapper ? ((ServletRequestWrapper) request)
               .getRequest() : request));

      modifiableParameters = new TreeMap<String, String[]>();
      modifiableParameters.putAll(additionalParams);

      setInRequest(this, request);
   }

   public Map<String, String[]> getNativeParameters()
   {
      return super.getParameterMap();
   }

   @Override
   public Map<String, String[]> getParameterMap()
   {
      allParameters = new CompositeMap<String, String[]>()
               .addDelegate(modifiableParameters)
               .addDelegate(super.getParameterMap());
      return Collections.unmodifiableMap(allParameters);
   }

   public Map<String, String[]> getModifiableParameters()
   {
      return modifiableParameters;
   }

   public static HttpRewriteWrappedRequest getFromRequest(ServletRequest request)
   {
      HttpRewriteWrappedRequest wrapper = (HttpRewriteWrappedRequest) request
               .getAttribute(HttpRewriteWrappedRequest.class.getName());
      return wrapper;
   }

   private static void setInRequest(final HttpRewriteWrappedRequest wrapped, final ServletRequest request)
   {
      request.setAttribute(HttpRewriteWrappedRequest.class.getName(), wrapped);
   }

   /*
    * HttpServletRequest overrides
    */

   @Override
   public String getParameter(final String name)
   {
      String[] strings = getParameterMap().get(name);
      if (strings != null)
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
