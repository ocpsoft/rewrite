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
package com.ocpsoft.rewrite.config.tuckey;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.tuckey.web.filters.urlrewrite.UrlRewriter;

import com.ocpsoft.rewrite.config.Condition;
import com.ocpsoft.rewrite.config.Direction;
import com.ocpsoft.rewrite.config.Operation;
import com.ocpsoft.rewrite.config.Rule;
import com.ocpsoft.rewrite.exception.RewriteException;
import com.ocpsoft.rewrite.servlet.config.HttpInboundOperation;
import com.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class TuckeyRewriteRuleAdaptor implements Rule
{
   private final UrlRewriter rewriter;

   public TuckeyRewriteRuleAdaptor(final UrlRewriter urlRewriter)
   {
      this.rewriter = urlRewriter;
   }

   @Override
   public Condition getCondition()
   {
      return Direction.isInbound();
   }

   @Override
   public Operation getOperation()
   {
      return new HttpInboundOperation() {

         boolean forwarded = false;

         @Override
         public void performInbound(final HttpInboundServletRewrite event)
         {
            try {
               FilterChain filterChainAdaptor = new FilterChain() {
                  @Override
                  public void doFilter(final ServletRequest request, final ServletResponse response)
                           throws IOException, ServletException
                  {
                     forwarded = true;
                  }
               };

               if (rewriter.processRequest(event.getRequest(), event.getResponse(), filterChainAdaptor))
               {
                  if (forwarded)
                     event.handled();
                  else
                     event.abort();
               }
            }
            catch (Exception e) {
               throw new RewriteException("Error processing [" + this.getClass().getName() + "]", e);
            }
         }
      };
   }

}
