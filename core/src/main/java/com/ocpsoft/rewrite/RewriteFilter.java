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
package com.ocpsoft.rewrite;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.jboss.logging.Logger;

import com.ocpsoft.rewrite.BaseRewriteEvent.Flow;
import com.ocpsoft.rewrite.pattern.WeightedComparator;
import com.ocpsoft.rewrite.services.ServiceLoader;
import com.ocpsoft.rewrite.spi.RequestCycleWrapper;
import com.ocpsoft.rewrite.spi.RewriteListener;
import com.ocpsoft.rewrite.spi.RewriteProvider;
import com.ocpsoft.rewrite.util.Iterators;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class RewriteFilter implements Filter
{
   Logger log = Logger.getLogger(RewriteFilter.class);

   private List<RewriteListener> listeners;
   private List<RequestCycleWrapper> wrappers;
   private List<RewriteProvider> providers;

   @Override
   public void init(final FilterConfig filterConfig) throws ServletException
   {
      // TODO SPI pre filter init
      log.info("RewriteFilter starting up...");

      listeners = Iterators.asList(ServiceLoader.load(RewriteListener.class));
      wrappers = Iterators.asList(ServiceLoader.load(RequestCycleWrapper.class));
      providers = Iterators.asList(ServiceLoader.load(RewriteProvider.class));

      Collections.sort(listeners, new WeightedComparator());
      Collections.sort(wrappers, new WeightedComparator());
      Collections.sort(providers, new WeightedComparator());

      logLoadedServices(RewriteListener.class, listeners);
      logLoadedServices(RequestCycleWrapper.class, wrappers);
      logLoadedServices(RewriteProvider.class, providers);

      log.info("RewriteFilter initialized.");
      // TODO SPI post filter init
   }

   @Override
   public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException
   {
      for (RewriteListener listener : listeners)
      {
         listener.requestReceived(request, response);
      }

      BaseRewriteEvent<ServletRequest, ServletResponse> event = new InboundRewriteEventImpl<ServletRequest, ServletResponse>(
               request, response);

      for (RequestCycleWrapper wrapper : wrappers)
      {
         event.setRequest(wrapper.wrapRequest(request, response));
         event.setResponse(wrapper.wrapResponse(request, response));
      }

      for (RewriteListener listener : listeners)
      {
         listener.rewriteStarted(event);
      }

      rewrite(event);

      for (RewriteListener listener : listeners)
      {
         listener.rewriteCompleted(event);
      }

      if (!event.getFlow().is(Flow.ABORT_REQUEST))
      {
         chain.doFilter(event.getRequest(), event.getResponse());
      }

      for (RewriteListener listener : listeners)
      {
         listener.requestProcessed(event);
      }
   }

   private void rewrite(final BaseRewriteEvent<ServletRequest, ServletResponse> event) throws ServletException,
            IOException
   {
      for (RewriteProvider provider : providers)
      {
         if (provider.handles(event.getRequest(), event.getResponse()))
         {
            provider.rewriteInbound(event);

            if (event.getFlow().is(Flow.HALT_HANDLING))
            {
               break;
            }
         }
      }

      if (event.getFlow().is(Flow.ABORT_REQUEST))
      {
         if (event.getFlow().is(Flow.FORWARD))
         {
            event.getRequest().getRequestDispatcher(event.getDispatchResource())
                     .forward(event.getRequest(), event.getResponse());
         }
      }
      else if (event.getFlow().is(Flow.INCLUDE))
      {
         event.getRequest().getRequestDispatcher(event.getDispatchResource())
                  .include(event.getRequest(), event.getResponse());
      }
   }

   @Override
   public void destroy()
   {
      // TODO SPI filter destroy
   }

   private <T> void logLoadedServices(final Class<T> type, final List<T> services)
   {
      log.info("Loaded [" + services.size() + "] " + type.getName() + " ["
               + joinTypeNames(services) + "]");
   }

   private String joinTypeNames(final List<?> list)
   {
      StringBuilder result = new StringBuilder();
      for (int i = 0; i < list.size(); i++)
      {
         result.append(list.get(i).getClass().getName());
         if ((i + 1) < list.size())
         {
            result.append(", ");
         }
      }
      return result.toString();
   }
}
