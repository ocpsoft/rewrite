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

package org.ocpsoft.rewrite.servlet.impl;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.RewriteLifecycleContext;
import org.ocpsoft.rewrite.servlet.event.BaseRewrite.Flow;
import org.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;
import org.ocpsoft.rewrite.servlet.spi.RewriteLifecycleListener;
import org.ocpsoft.rewrite.spi.RewriteProvider;

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
   public String encodeRedirectURL(final String url)
   {
      HttpOutboundServletRewrite event = new HttpOutboundRewriteImpl(request, this, url);
      rewrite(event);

      if (event.getFlow().is(Flow.ABORT_REQUEST))
      {
         return event.getOutboundURL();
      }

      return super.encodeRedirectURL(event.getOutboundURL());
   }

   @Override
   public String encodeURL(final String url)
   {
      HttpOutboundServletRewrite event = new HttpOutboundRewriteImpl(request, this, url);
      rewrite(event);

      if (event.getFlow().is(Flow.ABORT_REQUEST))
      {
         return event.getOutboundURL();
      }

      return super.encodeURL(event.getOutboundURL());
   }

   private void rewrite(final HttpOutboundServletRewrite event)
   {
      @SuppressWarnings("unchecked")
      RewriteLifecycleContext<ServletContext> context = (RewriteLifecycleContext<ServletContext>) request
               .getAttribute(RewriteLifecycleContext.CONTEXT_KEY);
      for (RewriteLifecycleListener<Rewrite> listener : context.getRewriteLifecycleListeners())
      {
         listener.beforeOutboundRewrite(event);
      }

      for (RewriteProvider<ServletContext, Rewrite> p : context.getRewriteProviders())
      {
         if (p.handles(event))
         {
            p.rewrite(event);
            if (event.getFlow().is(Flow.HANDLED))
            {
               break;
            }
         }
      }

      for (RewriteLifecycleListener<Rewrite> listener : context.getRewriteLifecycleListeners())
      {
         listener.afterOutboundRewrite(event);
      }
   }
}
