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

import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.ocpsoft.rewrite.context.Context;
import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.servlet.spi.InboundRewriteProducer;
import com.ocpsoft.rewrite.servlet.spi.OutboundRewriteProducer;
import com.ocpsoft.rewrite.servlet.spi.RequestCycleWrapper;
import com.ocpsoft.rewrite.servlet.spi.RewriteLifecycleListener;
import com.ocpsoft.rewrite.spi.RewriteProvider;

/**
 * Stores implementations of all Rewrite lifecycle services.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface RewriteLifecycleContext extends Context
{
   public static final String CONTEXT_KEY = "_com.ocpsoft.rewrite.RequestContext";

   /**
    * Get all available {@link RewriteLifecycleListener} instances.
    */
   List<RewriteLifecycleListener<Rewrite>> getRewriteLifecycleListeners();

   /**
    * Get all available {@link RequestCycleWrapper} instances.
    */
   List<RequestCycleWrapper<ServletRequest, ServletResponse>> getRequestCycleWrappers();

   /**
    * Get all available {@link RewriteProvider} instances.
    */
   List<RewriteProvider<Rewrite>> getRewriteProviders();

   /**
    * Get all available {@link InboundRewriteProducer} instances.
    */
   List<InboundRewriteProducer<ServletRequest, ServletResponse>> getInboundRewriteEventProducers();

   /**
    * Get all available {@link OutboundRewriteProducer} instances.
    */
   List<OutboundRewriteProducer<ServletRequest, ServletResponse, Object>> getOutboundProducers();
}
