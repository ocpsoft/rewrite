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
package com.ocpsoft.rewrite.servlet;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.logging.Log;
import com.ocpsoft.rewrite.logging.LogFactory;
import com.ocpsoft.rewrite.pattern.WeightedComparator;
import com.ocpsoft.rewrite.services.ServiceLoader;
import com.ocpsoft.rewrite.servlet.event.BaseRewrite.Flow;
import com.ocpsoft.rewrite.servlet.event.InboundServletRewrite;
import com.ocpsoft.rewrite.servlet.impl.RewriteContextImpl;
import com.ocpsoft.rewrite.servlet.spi.InboundRewriteProducer;
import com.ocpsoft.rewrite.servlet.spi.OutboundRewriteProducer;
import com.ocpsoft.rewrite.servlet.spi.RequestCycleWrapper;
import com.ocpsoft.rewrite.servlet.spi.RewriteLifecycleListener;
import com.ocpsoft.rewrite.spi.RewriteProvider;
import com.ocpsoft.rewrite.util.Iterators;
import com.ocpsoft.rewrite.util.ServiceLogger;

/**
 * {@link Filter} responsible for handling all inbound {@link Rewrite} events.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class RewriteFilter implements Filter
{
   public static final String CONTEXT_KEY = "_com.ocpsoft.rewrite.RequestContext";

   Log log = LogFactory.getLog(RewriteFilter.class);

   private List<RewriteLifecycleListener<Rewrite>> listeners;
   private List<RequestCycleWrapper<ServletRequest, ServletResponse>> wrappers;
   private List<RewriteProvider<Rewrite>> providers;
   private List<InboundRewriteProducer<ServletRequest, ServletResponse>> inbound;
   private List<OutboundRewriteProducer<ServletRequest, ServletResponse, Object>> outbound;

   @Override
   @SuppressWarnings("unchecked")
   public void init(final FilterConfig filterConfig) throws ServletException
   {
      // TODO SPI pre filter init?
      log.info("RewriteFilter starting up...");

      listeners = Iterators.asUniqueList(ServiceLoader.load(RewriteLifecycleListener.class));
      wrappers = Iterators.asUniqueList(ServiceLoader.load(RequestCycleWrapper.class));
      providers = Iterators.asUniqueList(ServiceLoader.load(RewriteProvider.class));
      inbound = Iterators.asUniqueList(ServiceLoader.load(InboundRewriteProducer.class));
      outbound = Iterators.asUniqueList(ServiceLoader.load(OutboundRewriteProducer.class));

      Collections.sort(listeners, new WeightedComparator());
      Collections.sort(wrappers, new WeightedComparator());
      Collections.sort(providers, new WeightedComparator());
      Collections.sort(inbound, new WeightedComparator());
      Collections.sort(outbound, new WeightedComparator());

      ServiceLogger.logLoadedServices(log, RewriteLifecycleListener.class, listeners);
      ServiceLogger.logLoadedServices(log, RequestCycleWrapper.class, wrappers);
      ServiceLogger.logLoadedServices(log, RewriteProvider.class, providers);
      ServiceLogger.logLoadedServices(log, InboundRewriteProducer.class, inbound);
      ServiceLogger.logLoadedServices(log, OutboundRewriteProducer.class, outbound);

      log.info("RewriteFilter initialized.");
      // TODO SPI post filter init?
   }

   @Override
   public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException
   {
      InboundServletRewrite<ServletRequest, ServletResponse> event = createRewriteEvent(request,
               response);

      if (event == null)
      {
         log.warn("No Rewrite event was produced - RewriteFilter disabled on this request.");
         chain.doFilter(request, response);
      }
      else
      {
         if (request.getAttribute(CONTEXT_KEY) == null)
         {
            RewriteLifecycleContext context = new RewriteContextImpl(inbound, outbound, listeners, wrappers, providers);
            request.setAttribute(CONTEXT_KEY, context);
         }

         for (RewriteLifecycleListener<Rewrite> listener : listeners)
         {
            if (listener.handles(event))
               listener.beforeInboundLifecycle(event);
         }

         for (RequestCycleWrapper<ServletRequest, ServletResponse> wrapper : wrappers)
         {
            if (wrapper.handles(event))
            {
               event.setRequest(wrapper.wrapRequest(event.getRequest(), event.getResponse()));
               event.setResponse(wrapper.wrapResponse(event.getRequest(), event.getResponse()));
            }
         }

         for (RewriteLifecycleListener<Rewrite> listener : listeners)
         {
            if (listener.handles(event))
               listener.beforeInboundRewrite(event);
         }

         rewrite(event);

         if (!event.getFlow().is(Flow.ABORT_REQUEST))
         {
            chain.doFilter(event.getRequest(), event.getResponse());
         }

         for (RewriteLifecycleListener<Rewrite> listener : listeners)
         {
            if (listener.handles(event))
               listener.afterInboundLifecycle(event);
         }
      }
   }

   public InboundServletRewrite<ServletRequest, ServletResponse> createRewriteEvent(final ServletRequest request,
            final ServletResponse response)
   {
      for (InboundRewriteProducer<ServletRequest, ServletResponse> producer : inbound)
      {
         InboundServletRewrite<ServletRequest, ServletResponse> event = producer
                  .createInboundRewrite(request, response);
         if (event != null)
            return event;
      }
      return null;
   }

   private void rewrite(final InboundServletRewrite<ServletRequest, ServletResponse> event)
            throws ServletException,
            IOException
   {
      for (RewriteProvider<Rewrite> provider : providers)
      {
         if (provider.handles(event))
         {
            provider.rewrite(event);

            if (event.getFlow().is(Flow.HANDLED))
            {
               break;
            }
         }
      }

      for (RewriteLifecycleListener<Rewrite> listener : listeners)
      {
         if (listener.handles(event))
            listener.afterInboundRewrite(event);
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
      // TODO SPI filter destroy?
   }

}
