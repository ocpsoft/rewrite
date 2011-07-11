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
package com.ocpsoft.rewrite.servlet.event;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.ocpsoft.rewrite.RewriteContext;
import com.ocpsoft.rewrite.exception.RewriteException;
import com.ocpsoft.rewrite.servlet.RewriteFilter;

/**
 * Base implementation of {@link InboundServletRewriteEvent}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class BaseRewrite<IN extends ServletRequest, OUT extends ServletResponse> implements
         InboundServletRewrite<IN, OUT>, OutboundServletRewrite<IN, OUT>
{
   private IN request;
   private OUT response;
   protected Flow flow;
   protected String dispatchResource;

   public BaseRewrite(final IN request, final OUT response)
   {
      flow = Flow.UN_HANDLED;
      this.request = request;
      this.response = response;
   }

   /*
    * Mutators
    */

   @Override
   public void abort()
   {
      this.flow = Flow.ABORT_REQUEST;
   }

   @Override
   public void proceed()
   {
      this.flow = Flow.PROCEED;
   }

   @Override
   public void handled()
   {
      this.flow = Flow.HANDLED;
   }

   @Override
   public void include(final String resource)
   {
      this.dispatchResource = resource;
      this.flow = Flow.INCLUDE;
   }

   @Override
   public void forward(final String resource)
   {
      this.dispatchResource = resource;
      this.flow = Flow.FORWARD;
   }

   /*
    * Getters
    */
   @Override
   public Flow getFlow()
   {
      return flow;
   }

   @Override
   public String getDispatchResource()
   {
      return dispatchResource;
   }

   @Override
   public IN getRequest()
   {
      return request;
   }

   @Override
   public OUT getResponse()
   {
      return response;
   }

   @Override
   public void setRequest(final IN request)
   {
      this.request = request;
   }

   @Override
   public void setResponse(final OUT response)
   {
      this.response = response;
   }

   /*
    * Flow control enum
    */

   public enum Flow
   {
      UN_HANDLED(null),
      HANDLED(null),
      CONTINUE(HANDLED),
      PROCEED(CONTINUE),
      INCLUDE(HANDLED),
      ABORT_REQUEST(HANDLED),
      FORWARD(ABORT_REQUEST),
      CHAIN(HANDLED);

      private Flow parent;

      private Flow(final Flow flow)
      {
         this.parent = flow;
      }

      public boolean is(final Flow other)
      {
         if (other == null)
         {
            return false;
         }

         Flow t = this;
         while (t != null)
         {
            if (other == t)
            {
               return true;
            }
            t = t.parent;
         }
         return false;
      }

      public boolean contains(final Flow other)
      {
         if (other == null)
         {
            return false;
         }

         Flow t = other;
         while (t != null)
         {
            if (this == t)
            {
               return true;
            }
            t = t.parent;
         }
         return false;
      }
   }

   @Override
   public String toString()
   {
      return "Rewrite [flow=" + flow + ", dispatchResource=" + dispatchResource + "]";
   }

   @Override
   public RewriteContext getRewriteContext()
   {
      RewriteContext context = (RewriteContext) request.getAttribute(RewriteFilter.CONTEXT_KEY);
      if (context == null)
      {
         throw new RewriteException("RewriteContext was null. Something is seriously wrong, " +
                  "or you are attempting to access this event outside of the Rewrite lifecycle.");
      }
      return context;
   }
}
