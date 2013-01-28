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
package org.ocpsoft.rewrite.config.tuckey;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.exception.RewriteException;
import org.ocpsoft.rewrite.servlet.config.HttpOperation;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.tuckey.web.filters.urlrewrite.UrlRewriter;

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
   public boolean evaluate(final Rewrite event, final EvaluationContext context)
   {
      return Direction.isInbound().evaluate(event, context);
   }

   @Override
   public void perform(final Rewrite event, final EvaluationContext context)
   {
      new HttpOperation() {

         boolean forwarded = false;

         @Override
         public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
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
      }.perform(event, context);
   }

   @Override
   public String getId()
   {
      return "tuckey-" + hashCode();
   }

}
