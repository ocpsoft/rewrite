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
package com.ocpsoft.rewrite;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class BaseRewriteEvent<IN extends ServletRequest, OUT extends ServletResponse> implements
         MutableRewriteEvent<IN, OUT>
{

   private IN request;
   private OUT response;
   protected Flow flow;
   protected String dispatchResource;

   public BaseRewriteEvent(final IN request, final OUT response)
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

   public Flow getFlow()
   {
      return flow;
   }

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

   protected enum Flow
   {
      UN_HANDLED(null),
      HANDLED(null),
      CONTINUE(HANDLED),
      PROCEED(CONTINUE),
      HALT_HANDLING(HANDLED),
      INCLUDE(HALT_HANDLING),
      ABORT_REQUEST(HALT_HANDLING),
      FORWARD(ABORT_REQUEST),
      CHAIN(HALT_HANDLING);

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
}
