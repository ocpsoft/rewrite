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
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.ocpsoft.rewrite.servlet.config.response.ResponseContentInterceptor;
import org.ocpsoft.rewrite.servlet.config.response.ResponseStreamWrapper;

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

   protected void setCurrentInstance(RewriteWrappedResponse instance)
   {
      request.setAttribute(RewriteWrappedResponse.INSTANCE_KEY, instance);
   }

   public RewriteWrappedResponse(HttpServletRequest request, HttpServletResponse response)
   {
      super(response);
      this.request = request;
   }

   public HttpServletRequest getRequest()
   {
      return request;
   }

   @Override
   public ServletResponse getResponse()
   {
      return super.getResponse();
   }

   @Override
   public void setResponse(ServletResponse response)
   {
      super.setResponse(response);
   }

   abstract public boolean isResponseContentIntercepted();

   abstract public boolean isResponseStreamWrapped();

   abstract public void addContentInterceptor(ResponseContentInterceptor stage);

   abstract public void addStreamWrapper(ResponseStreamWrapper wrapper);

   abstract public void flushBufferedContent();
}
