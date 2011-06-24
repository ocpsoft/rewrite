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
import java.util.ServiceLoader;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.ocpsoft.rewrite.BaseRewriteEvent.Flow;
import com.ocpsoft.rewrite.servlet.RewriteWrappedResponse;
import com.ocpsoft.rewrite.spi.RewriteProvider;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class CoreFilter implements Filter
{
   @Override
   public void init(final FilterConfig filterConfig) throws ServletException
   {
      // TODO SPI filter init
   }

   @Override
   public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException
   {
      BaseRewriteEvent event = new InboundRewriteEventImpl(request, new RewriteWrappedResponse(request, response));

      // TODO SPI wrap filter?
      ServiceLoader<RewriteProvider> loader = ServiceLoader.load(RewriteProvider.class);
      for (RewriteProvider provider : loader)
      {
         provider.rewriteInbound(event);
         if (event.getFlow().is(Flow.ABORTED))
         {
            break;
         }
      }
      // TODO SPI publish rewrite outcome

      chain.doFilter(event.getRequest(), event.getResponse());
   }

   @Override
   public void destroy()
   {
      // TODO EXTENSION filter destroy
   }
}
