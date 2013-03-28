/*
 * Copyright 2012 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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
package org.ocpsoft.rewrite.servlet;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.config.response.ResponseContentInterceptor;
import org.ocpsoft.rewrite.servlet.config.response.ResponseStreamWrapper;

/**
 * A {@link HttpServletResponseWrapper} for the {@link Rewrite} framework.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class RewriteWrappedResponse extends HttpServletResponseWrapper
{
   protected final static String INSTANCE_KEY = RewriteWrappedResponse.class.getName() + "_instance";

   /**
    * Get the current {@link RewriteWrappedResponse} isntance for the current {@link ServletRequest}
    */
   public static RewriteWrappedResponse getCurrentInstance(ServletRequest request)
   {
      return (RewriteWrappedResponse) request.getAttribute(INSTANCE_KEY);
   }

   private HttpServletRequest request;

   /**
    * Set the current {@link RewriteWrappedResponse} instance.
    */
   protected void setCurrentInstance(RewriteWrappedResponse instance)
   {
      request.setAttribute(RewriteWrappedResponse.INSTANCE_KEY, instance);
   }

   /**
    * Create a new {@link RewriteWrappedResponse} instance.
    */
   public RewriteWrappedResponse(HttpServletRequest request, HttpServletResponse response)
   {
      super(response);
      this.request = request;
   }

   /**
    * Get the {@link HttpServletRequest} to which this {@link RewriteWrappedResponse} is associated.
    */
   public HttpServletRequest getRequest()
   {
      return request;
   }

   /**
    * Return <code>true</code> if any {@link ResponseContentInterceptor} instances have been registered on the current
    * {@link HttpServletResponse}.
    */
   abstract public boolean isResponseContentIntercepted();

   /**
    * Return <code>true</code> if any {@link ResponseStreamWrapper} instances have been registered on the current
    * {@link HttpServletResponse}.
    */
   abstract public boolean isResponseStreamWrapped();

   /**
    * Register a new {@link ResponseContentInterceptor} for the current {@link HttpServletResponse}. This method must be
    * called before the {@link HttpServletRequest} has been passed to the underlying application..
    */
   abstract public void addContentInterceptor(ResponseContentInterceptor stage);

   /**
    * Register a new {@link ResponseStreamWrapper} for the current {@link HttpServletResponse}. This method must be
    * called before the {@link HttpServletRequest} has been passed to the underlying application..
    */
   abstract public void addStreamWrapper(ResponseStreamWrapper wrapper);

   /**
    * Flush any content that may be buffered in registered {@link ResponseContentInterceptor} instances. This operation
    * has no effect if no {@link ResponseContentInterceptor} instances are registered.
    */
   abstract public void flushBufferedContent();

   /**
    * Call {@link ResponseStreamWrapper#finish()} any registered instances of {@link ResponseStreamWrapper}. This
    * operation has no effect if no instances are registered.
    */
   abstract public void finishStreamWrappers();
}
