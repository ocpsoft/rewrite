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
package com.ocpsoft.rewrite.spi;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.ocpsoft.rewrite.Specialized;
import com.ocpsoft.rewrite.RewriteEvent;
import com.ocpsoft.rewrite.pattern.Weighted;

/**
 * Listens to rewrite life-cycle events.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface RewriteListener<IN extends ServletRequest, OUT extends ServletResponse> extends Specialized, Weighted
{
   /**
    * Invoked before {@link RequestCycleWrapper} services are processed.
    */
   void requestReceived(IN request, OUT response);

   /**
    * Invoked after {@link RequestCycleWrapper} services are processed, but before {@link RewriteProvider} services are
    * processed.
    */
   void rewriteStarted(RewriteEvent<IN, OUT> event);

   /**
    * Invoked after {@link RewriteProvider} services are processed, but before control of the request cycle is passed to
    * the application via {@link FilterChain#doFilter(ServletRequest, ServletResponse)}
    */
   void rewriteCompleted(RewriteEvent<IN, OUT> event);

   /**
    * Invoked after application has returned control of the request to the rewrite engine, but before the rewrite engine
    * passes control of the application to other filters in the application chain.
    */
   void requestProcessed(RewriteEvent<IN, OUT> event);
}
