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

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public abstract class BaseRewriteEvent implements RewriteEvent
{
   protected enum Flow
   {
         UN_HANDLED(null),
         HANDLED(null),
            PROCEED(HANDLED),
            ABORTED(HANDLED),
            FORWARD(HANDLED);

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

   private ServletRequest request;
   private ServletResponse response;
   private Flow flow;
   private String forwardResource;

   public BaseRewriteEvent(final ServletRequest request, final ServletResponse response)
   {
      flow = Flow.UN_HANDLED;
      this.request = request;
      this.response = response;
   }

   protected Flow getFlow()
   {
      return flow;
   }

   /*
    * Mutators
    */

   @Override
   public void abort()
   {
      this.flow = Flow.ABORTED;
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
   public void forward(final String resource)
   {
      this.forwardResource = resource;
      this.flow = Flow.FORWARD;
   }

   protected void setResponse(final HttpServletResponse response)
   {
      this.response = response;
   }

   public void setRequest(final HttpServletRequest request)
   {
      this.request = request;
   }

   /*
    * Getters
    */

   @Override
   public ServletRequest getRequest()
   {
      return request;
   }

   @Override
   public ServletResponse getResponse()
   {
      return response;
   }

   @Override
   public void setRequest(final ServletRequest request)
   {
      this.request = request;
   }

   @Override
   public void setResponse(final ServletResponse response)
   {
      this.response = response;
   }
}
