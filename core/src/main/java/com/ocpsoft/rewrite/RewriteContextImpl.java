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

import java.util.List;

import com.ocpsoft.rewrite.spi.RequestCycleWrapper;
import com.ocpsoft.rewrite.spi.RewriteEventProducer;
import com.ocpsoft.rewrite.spi.RewriteLifecycleListener;
import com.ocpsoft.rewrite.spi.RewriteProvider;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class RewriteContextImpl implements RewriteContext
{

   private final List<RewriteProvider<?, ?, ?, ?>> providers;
   private final List<RewriteLifecycleListener<?>> listeners;
   private final List<RequestCycleWrapper<?, ?>> wrappers;
   private final List<RewriteEventProducer> producers;

   public RewriteContextImpl(final List<RewriteEventProducer> producers,
            final List<RewriteLifecycleListener<?>> listeners,
            final List<RequestCycleWrapper<?, ?>> wrappers,
            final List<RewriteProvider<?, ?, ?, ?>> providers)
   {
      this.producers = producers;
      this.listeners = listeners;
      this.wrappers = wrappers;
      this.providers = providers;
   }

   @Override
   public List<RewriteLifecycleListener<?>> getRewriteLifecycleListeners()
   {
      return listeners;
   }

   @Override
   public List<RequestCycleWrapper<?, ?>> getRequestCycleWrappers()
   {
      return wrappers;
   }

   @Override
   public List<RewriteProvider<?, ?, ?, ?>> getRewriteProviders()
   {
      return providers;
   }

   @Override
   public List<RewriteEventProducer> getRewriteEventProducers()
   {
      return producers;
   }

}
