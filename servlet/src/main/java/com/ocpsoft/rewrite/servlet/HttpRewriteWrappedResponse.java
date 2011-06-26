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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.ocpsoft.rewrite.RewriteContext;
import com.ocpsoft.rewrite.RewriteFilter;
import com.ocpsoft.rewrite.event.MutableOutboundRewriteEvent;
import com.ocpsoft.rewrite.spi.RewriteProvider;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class HttpRewriteWrappedResponse extends HttpServletResponseWrapper
{
   private final HttpServletRequest request;

   public HttpRewriteWrappedResponse(final HttpServletRequest request, final HttpServletResponse response)
   {
      super(response);
      this.request = request;
   }

   public HttpServletRequest getRequest()
   {
      return request;
   }

   @Override
   public String encodeRedirectUrl(final String url)
   {
      return encodeRedirectURL(url);
   }

   @Override
   public String encodeUrl(final String url)
   {
      return encodeURL(url);
   }

   @Override
   @SuppressWarnings({ "rawtypes", "unchecked" })
   public String encodeRedirectURL(final String url)
   {
      RewriteContext context = (RewriteContext) request.getAttribute(RewriteFilter.CONTEXT_KEY);

      MutableOutboundRewriteEvent event = new HttpOutboundRewriteEvent(request, this, url);

      for (RewriteProvider p : context.getRewriteProviders()) {
         if (p.handles(event))
         {
            p.rewriteOutbound(event);
         }
      }

      return super.encodeRedirectURL(event.getURL());
   }

   @Override
   @SuppressWarnings({ "rawtypes", "unchecked" })
   public String encodeURL(final String url)
   {
      RewriteContext context = (RewriteContext) request.getAttribute(RewriteFilter.CONTEXT_KEY);

      MutableOutboundRewriteEvent event = new HttpOutboundRewriteEvent(request, this, url);

      for (RewriteProvider p : context.getRewriteProviders()) {
         if (p.handles(event))
         {
            p.rewriteOutbound(event);
         }
      }

      return super.encodeURL(event.getURL());
   }
}
