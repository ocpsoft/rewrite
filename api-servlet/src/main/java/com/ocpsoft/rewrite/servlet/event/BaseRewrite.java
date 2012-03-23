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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.ocpsoft.rewrite.config.Rule;
import com.ocpsoft.rewrite.context.Context;
import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.exception.RewriteException;
import com.ocpsoft.rewrite.servlet.RewriteLifecycleContext;

/**
 * Base implementation of {@link InboundServletRewriteEvent}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class BaseRewrite<IN extends ServletRequest, OUT extends ServletResponse>
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

   /**
    * Abort the current {@link ServletRequest} immediately. No further action will be taken on, and the request will not
    * be passed to the application.
    */
   public void abort()
   {
      this.flow = Flow.ABORT_REQUEST;
   }

   /**
    * Continue processing {@link Rule} definitions for the current {@link ServletRequest}, even if a call to
    * {@link #abort()}, {@link #handled()}, {@link #forward(String)}, or any other life-cycle control method has
    * previously been made.
    */
   public void proceed()
   {
      this.flow = Flow.PROCEED;
   }

   /**
    * Stop processing {@link Rule} definitions and pass control of the the current {@link ServletRequest} to the
    * underlying application, even if a call to {@link #abort()}, {@link #proceed()}, {@link #forward(String)}, or any
    * other life-cycle control method has previously been made.
    */
   public void handled()
   {
      this.flow = Flow.HANDLED;
   }

   /**
    * Once {@link Rule} processing has completed, perform a
    * {@link RequestDispatcher#include(ServletRequest, ServletResponse)} of the given resource target.
    */
   public void include(final String resource)
   {
      this.dispatchResource = resource;
      this.flow = Flow.INCLUDE;
   }

   /**
    * Once {@link Rule} processing has completed, perform a
    * {@link RequestDispatcher#forward(ServletRequest, ServletResponse)} to the given resource target.
    */
   public void forward(final String resource)
   {
      this.dispatchResource = resource;
      this.flow = Flow.FORWARD;
   }

   /*
    * Getters
    */
   /**
    * Get the current {@link Flow} state.
    */
   public Flow getFlow()
   {
      return flow;
   }

   /**
    * Set the current {@link Flow} state.
    */
   public void setFlow(final Flow flow)
   {
      this.flow = flow;
   }

   /**
    * Get the current dispatch resource. This value is used when performing a {@link #forward(String)},
    * {@link #include(String)}, or redirect.
    */
   public String getDispatchResource()
   {
      return dispatchResource;
   }

   /**
    * Get the current {@link ServletRequest} object.
    */
   public IN getRequest()
   {
      return request;
   }

   /**
    * Get the current {@link ServletResponse} object.
    */
   public OUT getResponse()
   {
      return response;
   }

   /**
    * Set the current {@link ServletRequest} object.
    */
   public void setRequest(final IN request)
   {
      this.request = request;
   }

   /**
    * Set the current {@link ServletResponse} object.
    */
   public void setResponse(final OUT response)
   {
      this.response = response;
   }

   /**
    * Enum to represent the finite state of the Rewrite container.
    */
   public enum Flow
   {
      UN_HANDLED(null),
      HANDLED(null),
      PROCEED(UN_HANDLED),

      CONTINUE(HANDLED),

      INCLUDE(HANDLED),

      ABORT_REQUEST(HANDLED),
      FORWARD(ABORT_REQUEST),
      REDIRECT_TEMPORARY(ABORT_REQUEST),
      REDIRECT_PERMANENT(ABORT_REQUEST);

      private Flow parent;

      private Flow(final Flow flow)
      {
         this.parent = flow;
      }

      /**
       * Return true if this {@link Flow} is a descendant of the given value.
       */
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

      /**
       * Return true if the given {@link Flow} is a descendant of <code>this</code>.
       */
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

   /**
    * Get the {@link Context} for the current {@link Rewrite}.
    */
   public Context getRewriteContext()
   {
      Context context = (Context) request.getAttribute(RewriteLifecycleContext.CONTEXT_KEY);
      if (context == null)
      {
         throw new RewriteException("RewriteContext was null. Something is seriously wrong, " +
                  "or you are attempting to access this event outside of the Rewrite lifecycle.");
      }
      return context;
   }
}
