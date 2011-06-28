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
package com.ocpsoft.rewrite.servlet.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ocpsoft.rewrite.servlet.event.RewriteEventBase;
import com.ocpsoft.rewrite.servlet.http.HttpOutboundRewriteEvent;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class HttpOutboundRewriteEventImpl extends RewriteEventBase<HttpServletRequest, HttpServletResponse> implements
         HttpOutboundRewriteEvent
{

   private String url;

   public HttpOutboundRewriteEventImpl(final HttpServletRequest request, final HttpServletResponse response,
            final String url)
   {
      super(request, response);
      this.url = url;
   }

   @Override
   public String getOutboundURL()
   {
      return url;
   }

   @Override
   public void setOutboundURL(final String url)
   {
      this.url = url;
   }

   @Override
   public String getContextPath()
   {
      return getRequest().getContextPath();
   }

   @Override
   public String getRequestURL()
   {
      return getRequest().getRequestURI().substring(getContextPath().length());
   }

   @Override
   public String getRequestQueryString()
   {
      return getRequest().getQueryString() == null ? "" : getRequest().getQueryString();
   }
}
