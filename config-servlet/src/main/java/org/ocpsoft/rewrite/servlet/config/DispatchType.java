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
package org.ocpsoft.rewrite.servlet.config;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.common.pattern.WeightedComparator;
import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.common.util.Iterators;
import org.ocpsoft.common.util.Strings;
import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.DispatcherType;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.servlet.spi.DispatcherTypeProvider;

/**
 * A {@link Condition} that inspects the value of {@link HttpServletRequest#getDispatcherType()}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class DispatchType extends HttpCondition
{
   private final static String PROVIDER_KEY = DispatchType.class.getName() + "_PROVIDERS";

   private final DispatcherType type;

   private DispatchType(final DispatcherType type)
   {
      this.type = type;
   }

   @Override
   public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      if (this.type.equals(getDispatcherType(event)))
      {
         return true;
      }
      return false;
   }

   /**
    * Determines the {@link DispatcherType} of the current request using the {@link DispatcherTypeProvider} SPI.
    */
   private DispatcherType getDispatcherType(final HttpServletRewrite event)
   {
      for (DispatcherTypeProvider provider : getDispatcherTypeProviders(event)) {
         DispatcherType dispatcherType = provider.getDispatcherType(event.getRequest(), event.getServletContext());
         if (dispatcherType != null) {
            return dispatcherType;
         }
      }
      throw new IllegalStateException("Unable to determine dispatcher type of current request");
   }

   /**
    * Simple caching mechanism for the providers on a per request basis
    */
   @SuppressWarnings("unchecked")
   private List<DispatcherTypeProvider> getDispatcherTypeProviders(HttpServletRewrite event)
   {
      List<DispatcherTypeProvider> providers = (List<DispatcherTypeProvider>)
               event.getRequest().getAttribute(PROVIDER_KEY);
      if (providers == null) {
         providers = Iterators.asList(ServiceLoader.loadTypesafe(DispatcherTypeProvider.class).iterator());
         Collections.sort(providers, new WeightedComparator());
         event.getRequest().setAttribute(PROVIDER_KEY, providers);
      }
      return providers;
   }

   /**
    * Create a {@link DispatchType} condition that ensures the current {@link HttpServletRewrite} is a
    * {@link DispatcherType#FORWARD}
    */
   public static DispatchType isForward()
   {
      return new DispatchType(DispatcherType.FORWARD);
   }

   /**
    * Create a {@link DispatchType} condition that ensures the current {@link HttpServletRewrite} is a
    * {@link DispatcherType#REQUEST}
    */
   public static DispatchType isRequest()
   {
      return new DispatchType(DispatcherType.REQUEST);
   }

   /**
    * Create a {@link DispatchType} condition that ensures the current {@link HttpServletRewrite} is a
    * {@link DispatcherType#ERROR}
    */
   public static DispatchType isError()
   {
      return new DispatchType(DispatcherType.ERROR);
   }

   /**
    * Create a {@link DispatchType} condition that ensures the current {@link HttpServletRewrite} is a
    * {@link DispatcherType#ASYNC}
    */
   public static DispatchType isAsync()
   {
      return new DispatchType(DispatcherType.ASYNC);
   }

   /**
    * Create a {@link DispatchType} condition that ensures the current {@link HttpServletRewrite} is a
    * {@link DispatcherType#INCLUDE}
    */
   public static DispatchType isInclude()
   {
      return new DispatchType(DispatcherType.INCLUDE);
   }

   @Override
   public String toString()
   {
      return "DispatchType.is" + Strings.capitalize(type.toString().toLowerCase() + "()");
   }

}