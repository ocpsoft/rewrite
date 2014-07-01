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
package org.ocpsoft.rewrite.servlet.event;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.ocpsoft.rewrite.AbstractRewrite;
import org.ocpsoft.rewrite.context.Context;
import org.ocpsoft.rewrite.event.Flow;
import org.ocpsoft.rewrite.exception.RewriteException;
import org.ocpsoft.rewrite.servlet.RewriteLifecycleContext;

/**
 * Base implementation of {@link InboundServletRewriteEvent}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class BaseRewrite<IN extends ServletRequest, OUT extends ServletResponse> extends AbstractRewrite
{

   private IN request;
   private OUT response;
   protected Flow flow;
   protected String dispatchResource;
   private Context context;
   private ServletContext servletContext;

   public BaseRewrite(final IN request, final OUT response, final ServletContext servletContext)
   {
      this.servletContext = servletContext;
      flow = ServletRewriteFlow.UN_HANDLED;
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
      this.flow = ServletRewriteFlow.ABORT_REQUEST;
   }

   /**
    * Continue processing {@link org.ocpsoft.rewrite.config.Rule} definitions for the current {@link ServletRequest},
    * even if a call to {@link #abort()}, {@link #handled()}, {@link #forward(String)}, or any other life-cycle control
    * method has previously been made.
    */
   public void proceed()
   {
      this.flow = ServletRewriteFlow.PROCEED;
   }

   /**
    * Stop processing {@link org.ocpsoft.rewrite.config.Rule} definitions and pass control of the the current
    * {@link ServletRequest} to the underlying application, even if a call to {@link #abort()}, {@link #proceed()},
    * {@link #forward(String)}, or any other life-cycle control method has previously been made.
    */
   public void handled()
   {
      this.flow = ServletRewriteFlow.HANDLED;
   }

   /**
    * Once {@link org.ocpsoft.rewrite.config.Rule} processing has completed, perform a
    * {@link RequestDispatcher#include(ServletRequest, ServletResponse)} of the given resource target.
    */
   public void include(final String resource)
   {
      this.dispatchResource = resource;
      this.flow = ServletRewriteFlow.INCLUDE;
   }

   /**
    * Once {@link org.ocpsoft.rewrite.config.Rule} processing has completed, perform a
    * {@link RequestDispatcher#forward(ServletRequest, ServletResponse)} to the given resource target.
    */
   public void forward(final String resource)
   {
      this.dispatchResource = resource;
      this.flow = ServletRewriteFlow.FORWARD;
   }

   /*
    * Getters
    */
   /**
    * Get the current {@link ServletRewriteFlow} state.
    */
   @Override
   public Flow getFlow()
   {
      return flow;
   }

   /**
    * Set the current {@link ServletRewriteFlow} state.
    */
   public void setFlow(final ServletRewriteFlow flow)
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
    * Get the current {@link ServletContext} object.
    */
   public ServletContext getServletContext()
   {
      return servletContext;
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
   public enum ServletRewriteFlow implements Flow
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

      private ServletRewriteFlow parent;

      private ServletRewriteFlow(final ServletRewriteFlow flow)
      {
         this.parent = flow;
      }

      /**
       * Return true if this {@link ServletRewriteFlow} is a descendant of the given value.
       */
      public boolean is(final ServletRewriteFlow other)
      {
         if (other == null)
         {
            return false;
         }

         ServletRewriteFlow t = this;
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
       * Return true if the given {@link ServletRewriteFlow} is a descendant of <code>this</code>.
       */
      public boolean contains(final ServletRewriteFlow other)
      {
         if (other == null)
         {
            return false;
         }

         ServletRewriteFlow t = other;
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

      @Override
      public boolean isHandled()
      {
         return is(HANDLED);
      }

      @Override
      public boolean is(Flow flow)
      {
         if (flow instanceof ServletRewriteFlow)
            return is((ServletRewriteFlow) flow);
         return false;
      }
   }

   @Override
   public String toString()
   {
      return "Rewrite [flow=" + flow + ", dispatchResource=" + dispatchResource + "]";
   }

   /**
    * Get the {@link org.ocpsoft.rewrite.context.Context} for the current {@link org.ocpsoft.rewrite.event.Rewrite}.
    */
   @Override
   public Context getRewriteContext()
   {
      if (this.context == null)
      {
         Context context = (Context) request.getAttribute(RewriteLifecycleContext.LIFECYCLE_CONTEXT_KEY);
         if (context == null)
         {
            throw new RewriteException("RewriteContext was null. Something is seriously wrong, " +
                     "or you are attempting to access this event outside of the Rewrite lifecycle.");
         }
         this.context = context;
      }
      return this.context;
   }
}
