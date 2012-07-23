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
package org.ocpsoft.rewrite.servlet.spi;

import javax.servlet.FilterChain;

import org.ocpsoft.common.pattern.Specialized;
import org.ocpsoft.common.pattern.Weighted;

import org.ocpsoft.rewrite.event.Rewrite;

/**
 * Listens to {@link org.ocpsoft.rewrite.event.Rewrite} life-cycle events. Additional listeners may be specified by
 * providing a service activator file containing the name of your implementations:
 * 
 * /META-INF/services/org.ocpsoft.rewrite.servlet.spi.RewriteLifecycleListener --------------
 * com.example.LifecycleListenerImpl
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface RewriteLifecycleListener<T extends Rewrite> extends Specialized<Rewrite>, Weighted
{
   /**
    * Invoked before {@link RequestCycleWrapper} services are processed.
    */
   void beforeInboundLifecycle(T event);

   /**
    * Invoked after {@link RequestCycleWrapper} services are processed, but before
    * {@link org.ocpsoft.rewrite.spi.RewriteProvider} services are processed for inbound {@link Rewrite} requests.
    */
   void beforeInboundRewrite(T event);

   /**
    * Invoked after {@link org.ocpsoft.rewrite.spi.RewriteProvider} services are processed on inbound {@link Rewrite}
    * requests, but before control of the request cycle is passed to the application via
    * {@link FilterChain#doFilter(IN, OUT)}
    */
   void afterInboundRewrite(T event);

   /**
    * Invoked before {@link org.ocpsoft.rewrite.spi.RewriteProvider} services are processed on outbound {@link Rewrite}
    * events.
    */
   void beforeOutboundRewrite(T event);

   /**
    * Invoked after {@link org.ocpsoft.rewrite.spi.RewriteProvider} services are processed on outbound {@link Rewrite}
    * events.
    */
   void afterOutboundRewrite(T event);

   /**
    * Invoked after application has returned control of the request to the rewrite engine, and after the rewrite engine
    * calls {@link FilterChain#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse)} or ends the
    * request.
    */
   void afterInboundLifecycle(T event);
}
