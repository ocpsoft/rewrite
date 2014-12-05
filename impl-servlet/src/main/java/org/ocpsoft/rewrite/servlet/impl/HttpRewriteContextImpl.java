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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.http.HttpRewriteLifecycleContext;
import org.ocpsoft.rewrite.servlet.spi.InboundRewriteProducer;
import org.ocpsoft.rewrite.servlet.spi.OutboundRewriteProducer;
import org.ocpsoft.rewrite.servlet.spi.RequestCycleWrapper;
import org.ocpsoft.rewrite.servlet.spi.RewriteLifecycleListener;
import org.ocpsoft.rewrite.servlet.spi.RewriteResultHandler;
import org.ocpsoft.rewrite.spi.RewriteProvider;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class HttpRewriteContextImpl implements HttpRewriteLifecycleContext
{
   private final Map<Object, Object> map = new LinkedHashMap<Object, Object>();

   private final List<RewriteProvider<ServletContext, Rewrite>> providers;
   private final List<RewriteResultHandler> resultHandlers;
   private final List<RewriteLifecycleListener<Rewrite>> listeners;
   private final List<RequestCycleWrapper<ServletRequest, ServletResponse>> wrappers;
   private final List<InboundRewriteProducer<ServletRequest, ServletResponse>> inboundProducers;
   private final List<OutboundRewriteProducer<ServletRequest, ServletResponse, Object>> outboundProducers;

   public HttpRewriteContextImpl(final List<InboundRewriteProducer<ServletRequest, ServletResponse>> inboundProducers,
            final List<OutboundRewriteProducer<ServletRequest, ServletResponse, Object>> outboundProducers,
            final List<RewriteLifecycleListener<Rewrite>> listeners,
            List<RewriteResultHandler> resultHandlers,
            final List<RequestCycleWrapper<ServletRequest, ServletResponse>> wrappers,
            final List<RewriteProvider<ServletContext, Rewrite>> providers)
   {
      this.inboundProducers = inboundProducers;
      this.outboundProducers = outboundProducers;
      this.listeners = listeners;
      this.wrappers = wrappers;
      this.providers = providers;
      this.resultHandlers = resultHandlers;
   }

   @Override
   public List<RewriteLifecycleListener<Rewrite>> getRewriteLifecycleListeners()
   {
      return listeners;
   }

   @Override
   public List<RequestCycleWrapper<ServletRequest, ServletResponse>> getRequestCycleWrappers()
   {
      return wrappers;
   }

   @Override
   public List<RewriteProvider<ServletContext, Rewrite>> getRewriteProviders()
   {
      return providers;
   }

   @Override
   public List<RewriteResultHandler> getResultHandlers()
   {
      return resultHandlers;
   }

   @Override
   public List<InboundRewriteProducer<ServletRequest, ServletResponse>> getInboundRewriteEventProducers()
   {
      return inboundProducers;
   }

   @Override
   public List<OutboundRewriteProducer<ServletRequest, ServletResponse, Object>> getOutboundProducers()
   {
      return outboundProducers;
   }

   @Override
   public Object get(final Object key)
   {
      return map.get(key);
   }

   @Override
   public void put(final Object key, final Object value)
   {
      map.put(key, value);
   }

   @Override
   public boolean containsKey(final Object key)
   {
      return map.containsKey(key);
   }

   @Override
   public void clear()
   {
      map.clear();
   }

}
