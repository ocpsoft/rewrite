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
package com.ocpsoft.rewrite.servlet.config.rule;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.ocpsoft.rewrite.bind.Binding;
import com.ocpsoft.rewrite.config.Condition;
import com.ocpsoft.rewrite.config.ConditionBuilder;
import com.ocpsoft.rewrite.config.Operation;
import com.ocpsoft.rewrite.config.Rule;
import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.event.InboundRewrite;
import com.ocpsoft.rewrite.event.OutboundRewrite;
import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.param.Parameter;
import com.ocpsoft.rewrite.param.Parameterized;
import com.ocpsoft.rewrite.servlet.config.DispatchType;
import com.ocpsoft.rewrite.servlet.config.Forward;
import com.ocpsoft.rewrite.servlet.config.Path;
import com.ocpsoft.rewrite.servlet.config.QueryString;
import com.ocpsoft.rewrite.servlet.config.Redirect;
import com.ocpsoft.rewrite.servlet.config.Substitute;
import com.ocpsoft.rewrite.servlet.config.rule.Join.JoinParameterBuilder;
import com.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import com.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;

/**
 * {@link Rule} that creates a bi-directional rewrite rule between an externally facing URL and an internal server
 * resource URL
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Join implements Rule, Parameterized<JoinParameterBuilder, String>
{
   private static final String CURRENT_JOIN = Join.class.getName() + "_current";

   private String id;

   private final String pattern;
   private String resource;
   private Path resourcePath;
   private final Path path;

   private Operation operation;
   private Condition condition;

   private boolean inboundCorrection = false;

   protected Join(final String pattern)
   {
      this.pattern = pattern;
      this.path = Path.matches(pattern);
   }

   /**
    * The outward facing URL path to which this {@link Join} will apply.
    */
   public static Join path(final String pattern)
   {
      return new Join(pattern);
   }

   /**
    * The internal server resource (real or virtual) to be served.
    */
   public Join to(final String resource)
   {
      this.resource = resource;
      this.resourcePath = Path.matches(resource);
      return this;
   }

   /**
    * Redirect inbound requests for the internal resource to the outward facing URL instead.
    */
   public Join withInboundCorrection()
   {
      this.inboundCorrection = true;
      return this;
   }

   @Override
   public boolean evaluate(final Rewrite event, final EvaluationContext context)
   {
      if ((condition == null) || condition.evaluate(event, context))
      {
         if (event instanceof HttpInboundServletRewrite)
         {
            path.withRequestBinding();
            if (path.evaluate(event, context))
            {
               if (operation != null)
                  context.addPreOperation(operation);
               return true;
            }
            else if (inboundCorrection
                     && resourcePath.andNot(DispatchType.isForward()).evaluate(event, context))
            {
               List<String> names = path.getPathExpression().getParameterNames();
               for (String name : names) {
                  if (!QueryString.parameterExists(name).evaluate(event, context))
                  {
                     return false;
                  }
               }
               context.addPreOperation(Redirect.permanent(((HttpInboundServletRewrite) event).getContextPath()
                        + pattern));
               return true;
            }
         }
         else if (event instanceof HttpOutboundServletRewrite)
         {
            List<String> nonQueryParameters = resourcePath.getPathExpression().getParameterNames();

            List<String> queryParameters = path.getPathExpression().getParameterNames();
            queryParameters.removeAll(nonQueryParameters);

            ConditionBuilder outbound = resourcePath;
            for (String name : queryParameters)
            {
               outbound = outbound.and(QueryString.parameterExists(name));
            }
            return outbound.evaluate(event, context);
         }
      }

      return false;
   }

   @Override
   public void perform(final Rewrite event, final EvaluationContext context)
   {
      if (event instanceof InboundRewrite)
      {
         saveCurrentJoin(((HttpInboundServletRewrite) event).getRequest());
         Forward.to(resource).perform(event, context);
      }

      else if (event instanceof OutboundRewrite)
      {
         Substitute.with(pattern).perform(event, context);
      }
   }

   private void saveCurrentJoin(final HttpServletRequest request)
   {
      request.setAttribute(CURRENT_JOIN, this);
   }

   /**
    * Retrieve the {@link Join} that was invoked on the current request; if no {@link Join} was invoked, return null.
    */
   public static Join getCurrentJoin(final HttpServletRequest request)
   {
      return (Join) request.getAttribute(CURRENT_JOIN);
   }

   @Override
   public JoinParameterBuilder where(final String parameter)
   {
      return new JoinParameterBuilder(this, path.getPathExpression().getParameter(parameter));
   }

   @Override
   public JoinParameterBuilder where(final String param, final String pattern)
   {
      return where(param).matches(pattern);
   }

   @Override
   public JoinParameterBuilder where(final String param, final String pattern, final Binding binding)
   {
      return where(param, pattern).bindsTo(binding);
   }

   @Override
   public JoinParameterBuilder where(final String param, final Binding binding)
   {
      return where(param).bindsTo(binding);
   }

   @Override
   public String getId()
   {
      return id;
   }

   public Join when(final Condition condition)
   {
      this.condition = condition;
      return this;
   }

   public Join performInbound(final Operation operation)
   {
      this.operation = operation;
      return this;
   }

   /**
    * Set the ID of this {@link Join}.
    */
   public Join withId(final String id)
   {
      this.id = id;
      return this;
   }

   /**
    * Builder for {@link Join} specific {@link Parameter}
    * 
    * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
    * 
    */
   public class JoinParameterBuilder implements Parameterized<JoinParameterBuilder, String>
   {
      private final Join parent;
      private final Parameter<String> parameter;

      public JoinParameterBuilder(final Join link, final Parameter<String> parameter)
      {
         this.parent = link;
         this.parameter = parameter;
      }

      public Join to(final String resource)
      {
         return parent.to(resource);
      }

      public JoinParameterBuilder matches(final String pattern)
      {
         parameter.matches(pattern);
         return this;
      }

      public JoinParameterBuilder bindsTo(final Binding binding)
      {
         parameter.bindsTo(binding);
         return this;
      }

      @Override
      public JoinParameterBuilder where(final String param)
      {
         return parent.where(param);
      }

      @Override
      public JoinParameterBuilder where(final String param, final String pattern)
      {
         return parent.where(param, pattern);
      }

      @Override
      public JoinParameterBuilder where(final String param, final String pattern, final Binding binding)
      {
         return parent.where(param, pattern, binding);
      }

      @Override
      public JoinParameterBuilder where(final String param, final Binding binding)
      {
         return parent.where(param, binding);
      }

      public Join when(final Condition condition)
      {
         return parent.when(condition);
      }

      public Join perform(final Operation operation)
      {
         return parent.performInbound(operation);
      }
   }

   @Override
   public String toString()
   {
      return "Join [url=" + pattern + ", to=" + resource + ", id=" + id + ", inboundCorrection="
               + inboundCorrection + "]";
   }

}
