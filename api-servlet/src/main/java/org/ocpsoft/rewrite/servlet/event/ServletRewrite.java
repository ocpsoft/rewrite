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

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.ocpsoft.rewrite.event.Flow;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface ServletRewrite<IN extends ServletRequest, OUT extends ServletResponse>
         extends Rewrite
{
   /**
    * Get the current {@link ServletRequest}.
    */
   public IN getRequest();

   /**
    * Get the current {@link ServletResponse}.
    */
   public OUT getResponse();

   public ServletContext getServletContext();

   /**
    * Marks the current {@link ServletRewrite} as handled and terminates further handling. Control of the request is
    * <b>not</b> passed to the application; the request is terminated immediately following execution of the current
    * handler.
    */
   public void abort();

   /**
    * Marks the {@link ServletRewrite} as handled and proceeds with the rest of the handlers. Unless another handler
    * calls {@link #abort()}, this typically results in passing control of the request to the application via
    * {@link FilterChain#doFilter(ServletRequest, ServletResponse)} after all handlers have processed.
    */
   public void proceed();

   /**
    * Marks the {@link ServletRewrite} as handled and terminates further handling. Typically this results in passing
    * control of the request to the application via {@link FilterChain#doFilter(ServletRequest, ServletResponse)}
    */
   public void handled();

   /**
    * Set the current {@link Flow} state.
    */
   public void setFlow(Flow flow);
}
