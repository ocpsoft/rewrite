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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ocpsoft.common.pattern.WeightedComparator;
import org.ocpsoft.common.services.NonEnriching;
import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.common.util.Iterators;
import org.ocpsoft.rewrite.servlet.DispatcherType;
import org.ocpsoft.rewrite.servlet.http.HttpRequestCycleWrapper;
import org.ocpsoft.rewrite.servlet.spi.DispatcherTypeProvider;
import org.ocpsoft.rewrite.servlet.spi.RequestParameterProvider;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class HttpRewriteRequestCycleWrapper extends HttpRequestCycleWrapper implements NonEnriching
{
   private volatile List<RequestParameterProvider> providers;
   private final List<DispatcherTypeProvider> dispatcherProviders;

   @SuppressWarnings("unchecked")
   public HttpRewriteRequestCycleWrapper()
   {
      if (providers == null)
         synchronized (this) {
            if (providers == null)
               providers = Iterators.asList(ServiceLoader.load(RequestParameterProvider.class));
         }

      dispatcherProviders = Iterators.asList(
               ServiceLoader.loadTypesafe(DispatcherTypeProvider.class).iterator());
      Collections.sort(dispatcherProviders, new WeightedComparator());

   }

   @Override
   public HttpServletRequest wrapRequest(final HttpServletRequest request, final HttpServletResponse response,
            final ServletContext servletContext)
   {
      HttpServletRequest result = request;
      if (HttpRewriteWrappedRequest.getCurrentInstance(request) == null) {
         Map<String, String[]> additionalParams = new LinkedHashMap<String, String[]>();

         for (RequestParameterProvider provider : providers) {
            Map<String, String[]> m = provider.getAdditionalParameters(request, response);
            if (m != null)
            {
               additionalParams.putAll(m);
            }
         }

         result = new HttpRewriteWrappedRequest(request, additionalParams);
      }
      return result;
   }

   @Override
   public HttpServletResponse wrapResponse(final HttpServletRequest request, final HttpServletResponse response,
            final ServletContext servletContext)
   {
      HttpServletResponse result = response;
      /*
       * The Servlet spec requires that the unwrapped response is used in case of dispatch type ERROR. 
       * Therefore we must ALWAYS wrap the response for the ERROR dispatch type.
       */
      if (HttpRewriteWrappedResponse.getCurrentInstance(request) == null ||
               getDispatcherType(request, servletContext) == DispatcherType.ERROR)
         result = new HttpRewriteWrappedResponse(request, response, servletContext);
      return result;
   }

   /**
    * Determines the {@link DispatcherType} of the current request using the {@link DispatcherTypeProvider} SPI.
    */
   private DispatcherType getDispatcherType(HttpServletRequest request, ServletContext context)
   {
      for (DispatcherTypeProvider provider : dispatcherProviders) {
         DispatcherType dispatcherType = provider.getDispatcherType(request, context);
         if (dispatcherType != null) {
            return dispatcherType;
         }
      }
      throw new IllegalStateException("Unable to determine dispatcher type of current request");
   }

   @Override
   public int priority()
   {
      return 0;
   }
}
