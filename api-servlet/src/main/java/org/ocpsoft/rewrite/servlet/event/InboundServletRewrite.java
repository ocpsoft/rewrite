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
package org.ocpsoft.rewrite.servlet.event;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;

import org.ocpsoft.rewrite.event.InboundRewrite;
import org.ocpsoft.rewrite.spi.RewriteProvider;

/**
 * Immutable event propagated to registered {@link org.ocpsoft.rewrite.servlet.spi.RewriteLifecycleListener} and {@link RewriteProvider} instances when
 * an inbound as the rewrite lifecycle is executed.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface InboundServletRewrite<IN extends ServletRequest, OUT extends ServletResponse> extends
        InboundRewrite, ServletRewrite<IN, OUT>
{

   /**
    * Marks the {@link InboundServletRewriteEvent} as handled, terminates further handling, and instructs the container
    * to include the specified resource address in the current {@link ServletRequest} and {@link ServletResponse} cycle.
    * This include must be implemented using {@link RequestDispatcher#include(ServletRequest, ServletResponse)}
    */
   public void include(String resource);

   /**
    * Marks the {@link InboundServletRewriteEvent} as handled, terminates further handling, and instructs the container
    * to forward the current {@link ServletRequest} and {@link ServletResponse} to the specified resource address. This
    * forward must be implemented using {@link RequestDispatcher#forward(ServletRequest, ServletResponse)}
    */
   public void forward(String resource);

   /**
    * Set a new {@link ServletRequest} to be used for the remaining duration of the current {@link ServletRequest}. This
    * method is usually used to add {@link ServletRequestWrapper} implementations.
    */
   public void setRequest(IN request);

   /**
    * Set a new {@link ServletResponse} to be used for the remaining duration of the current {@link ServletRequest}.
    * This method is usually used to add {@link ServletResponseWrapper} implementations.
    */
   public void setResponse(OUT response);

   /**
    * Returns the resource address of the requested {@link InboundServletRewriteEvent#include(String)} or
    * {@link InboundServletRewriteEvent#forward(String)}
    */
   public String getDispatchResource();
}
