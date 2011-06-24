/*
 * Copyright 2011 Lincoln Baxter, III
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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.ocpsoft.rewrite.BaseRewriteEvent.Flow;
import com.ocpsoft.rewrite.services.ServiceLoader;
import com.ocpsoft.rewrite.servlet.RewriteWrappedResponse;
import com.ocpsoft.rewrite.spi.RequestCycleWrapper;
import com.ocpsoft.rewrite.spi.RewriteListener;
import com.ocpsoft.rewrite.spi.RewriteProvider;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class CoreFilter implements Filter
{

   // @URLAction(CONFERENCE, methods=)
   @Override
   public void init(final FilterConfig filterConfig) throws ServletException
   {
      // TODO SPI filter init
   }

   @Override
   @SuppressWarnings({ "rawtypes", "unchecked" })
   public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException
   {
      ServiceLoader<RewriteListener> listenerLoader = ServiceLoader.load(RewriteListener.class);
      for (RewriteListener listener : listenerLoader)
      {
         listener.onPreWrapRequestCycle(request, response);
      }

      BaseRewriteEvent<ServletRequest, ServletResponse> event = new InboundRewriteEventImpl<ServletRequest, ServletResponse>(
               request, new RewriteWrappedResponse(request, response));

      ServiceLoader<RequestCycleWrapper> wrapperLoader = ServiceLoader.load(RequestCycleWrapper.class);

      for (RequestCycleWrapper wrapper : wrapperLoader)
      {
         event.setRequest(wrapper.wrapRequest(request, response));
         event.setResponse(wrapper.wrapResponse(request, response));
      }

      for (RewriteListener listener : listenerLoader)
      {
         listener.onPreRewrite(event);
      }

      ServiceLoader<RewriteProvider> providerLoader = ServiceLoader.load(RewriteProvider.class);
      for (RewriteProvider provider : providerLoader)
      {
         if (provider.handles(event.getRequest(), event.getResponse()))
         {
            provider.rewriteInbound(event);

            if (event.getFlow().is(Flow.HALT))
            {
               break;
            }
         }
      }

      if (event.getFlow().is(Flow.ABORT))
      {
         if (event.getFlow().is(Flow.INCLUDE))
         {
            event.getRequest().getRequestDispatcher(event.getDispatchResource())
                     .include(event.getRequest(), event.getResponse());
         }
         else if (event.getFlow().is(Flow.FORWARD))
         {
            event.getRequest().getRequestDispatcher(event.getDispatchResource())
                     .forward(event.getRequest(), event.getResponse());
         }
      }

      for (RewriteListener listener : listenerLoader)
      {
         listener.onPostRewrite(event);
      }

      if (!event.getFlow().is(Flow.ABORT))
      {
         chain.doFilter(event.getRequest(), event.getResponse());

         for (RewriteListener listener : listenerLoader)
         {
            listener.onPostChain(event);
         }
      }

   }

   @Override
   public void destroy()
   {
      // TODO SPI filter destroy
   }
}
