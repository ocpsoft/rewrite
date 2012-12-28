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
package org.ocpsoft.rewrite.servlet.config.rule;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.config.ConditionBuilder;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.ParameterizedPatternParser;
import org.ocpsoft.rewrite.servlet.config.DispatchType;
import org.ocpsoft.rewrite.servlet.config.Forward;
import org.ocpsoft.rewrite.servlet.config.IPath;
import org.ocpsoft.rewrite.servlet.config.IPath.PathParameter;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.Query;
import org.ocpsoft.rewrite.servlet.config.Redirect;
import org.ocpsoft.rewrite.servlet.config.Substitute;
import org.ocpsoft.rewrite.servlet.config.bind.Request;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;
import org.ocpsoft.rewrite.servlet.util.QueryStringBuilder;

/**
 * {@link org.ocpsoft.rewrite.config.Rule} that creates a bi-directional rewrite rule between an externally facing URL
 * and an internal server resource URL
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Join implements IJoin
{
   private static final String JOIN_DISABLED_KEY = Join.class.getName() + "_DISABLED";
   private static final String JOIN_DISABLED_RESET_NEXT_KEY = Join.class.getName() + "_DISABLED_RESET_NEXT";

   private static final String CURRENT_JOIN = Join.class.getName() + "_current";

   private String id;

   private final String requestPattern;
   private String resourcePattern;
   private final IPath requestPath;
   private IPath resourcePath;

   private Operation operation;
   private Condition condition;

   private boolean inboundCorrection = false;

   private boolean chainingDisabled = true;

   private List<String> pathRequestParameters;

   private ConditionBuilder outboundConditionCache;

   protected Join(final String pattern, boolean requestBinding)
   {
      this.requestPattern = pattern;
      this.requestPath = Path.matches(pattern);
      if (requestBinding)
         requestPath.withRequestBinding();
   }

   /**
    * The client-facing URL path to which this {@link Join} will apply. Parameters of the form <code>"{n}"</code> will
    * be bound by name to the request parameter map via {@link Request#parameter(String)}, and subsequently used to
    * build the outbound URL by extracting values from the query string
    * <code>"?n=value"<code>. To disable request parameter binding and outbound URL rewriting, instead use {@link #nonBindingPath(String)}
    */
   public static IJoin path(final String pattern)
   {
      return new Join(pattern, true);
   }

   /**
    * The client-facing URL path to which this {@link Join} will apply. Parameters will not be bound to the request
    * parameter map. To enable request parameter binding and outbound URL rewriting, instead use {@link #path(String)}.
    */
   public static Join nonBindingPath(String pattern)
   {
      return new Join(pattern, false);
   }

   /**
    * Retrieve the {@link Join} that was invoked on the current request; if no {@link Join} was invoked, return null.
    */
   public static Join getCurrentJoin(final HttpServletRequest request)
   {
      return (Join) request.getAttribute(CURRENT_JOIN);
   }

   @Override
   public IJoin to(final String resource)
   {
      if (this.resourcePattern != null)
      {
         throw new IllegalStateException("Cannot set resource path more than once.");
      }
      this.resourcePattern = resource;
      this.resourcePath = Path.matches(resource);

      List<String> parameters = getPathRequestParameters();
      if (outboundConditionCache == null)
      {
         this.outboundConditionCache = resourcePath;
         for (int i = 0; i < parameters.size(); i++) {
            String name = parameters.get(i);
            outboundConditionCache = outboundConditionCache.and(Query.parameterExists(name));
         }
      }

      return this;
   }

   @Override
   public IJoin withInboundCorrection()
   {
      this.inboundCorrection = true;
      return this;
   }

   @Override
   public boolean evaluate(final Rewrite event, final EvaluationContext context)
   {
      if (event instanceof HttpInboundServletRewrite)
      {
         if (!isChainingDisabled(event) && requestPath.evaluate(event, context)
                  && ((condition == null) || condition.evaluate(event, context)))
         {
            if (operation != null)
               context.addPreOperation(operation);
            return true;
         }
         else if (inboundCorrection
                  && resourcePath.andNot(DispatchType.isForward()).evaluate(event, context)
                  && ((condition == null) || condition.evaluate(event, context)))
         {
            List<String> parameters = getPathRequestParameters();
            for (String param : parameters) {
               if (!Query.parameterExists(param).evaluate(event, context))
               {
                  return false;
               }
            }
            context.addPreOperation(Redirect.permanent(((HttpInboundServletRewrite) event).getContextPath()
                     + requestPattern));
            return true;
         }
      }
      else if ((event instanceof HttpOutboundServletRewrite))
      {
         if (outboundConditionCache.evaluate(event, context)
                  && ((condition == null) || condition.evaluate(event, context)))
         {
            if (operation != null)
               context.addPreOperation(operation);

            return true;
         }
      }

      return false;
   }

   private boolean isChainingDisabled(Rewrite event)
   {
      return Boolean.TRUE.equals(event.getRewriteContext().get(JOIN_DISABLED_KEY));
   }

   private List<String> getPathRequestParameters()
   {
      /*
       * For performance purposes - think and profile this if changed.
       */
      if (pathRequestParameters == null)
      {
         List<String> nonQueryParameters = resourcePath.getPathExpression().getParameterNames();
         List<String> queryParameters = requestPath.getPathExpression().getParameterNames();
         queryParameters.removeAll(nonQueryParameters);
         pathRequestParameters = queryParameters;
      }

      return pathRequestParameters;
   }

   @Override
   public void perform(final Rewrite event, final EvaluationContext context)
   {
      if (event instanceof HttpInboundServletRewrite)
      {
         saveCurrentJoin(((HttpInboundServletRewrite) event).getRequest());
         if (chainingDisabled)
         {
            event.getRewriteContext().put(JOIN_DISABLED_KEY, true);
         }
         Forward.to(resourcePattern).perform(event, context);
      }

      else if (event instanceof HttpOutboundServletRewrite)
      {
         List<String> parameters = getPathRequestParameters();

         String outboundURL = ((HttpOutboundServletRewrite) event).getOutboundAddress().toString();
         QueryStringBuilder query = QueryStringBuilder.createNew();
         if (outboundURL.contains("?"))
         {
            query.addParameters(outboundURL);
            for (String string : parameters) {
               List<String> values = query.removeParameter(string);
               if (values.size() > 1)
               {
                  query.addParameter(string, values.subList(1, values.size()).toArray(new String[] {}));
               }
            }
         }

         Substitute.with(requestPattern + query.toQueryString()).perform(event, context);
         if (chainingDisabled)
         {
            ((HttpOutboundServletRewrite) event).handled();
         }
      }
   }

   private void saveCurrentJoin(final HttpServletRequest request)
   {
      request.setAttribute(CURRENT_JOIN, this);
   }

   @Override
   public JoinParameter where(final String parameter)
   {
      List<PathParameter> params = new ArrayList<PathParameter>();
      if (requestPath.getPathExpression().getParameterMap().containsKey(parameter)) {
         params.add(requestPath.where(parameter));
      }
      if (resourcePath.getPathExpression().getParameterMap().containsKey(parameter)) {
         params.add(resourcePath.where(parameter));
      }

      if (!params.isEmpty()) {
         return new JoinParameter(this, params.toArray(new PathParameter[params.size()]));
      }

      throw new IllegalArgumentException("No such parameter [" + parameter + "] exists.");
   }

   @Override
   public JoinParameter where(final String param, final Binding binding)
   {
      return where(param).bindsTo(binding);
   }

   @Override
   public String getId()
   {
      return id;
   }

   @Override
   public IJoin when(final Condition condition)
   {
      this.condition = condition;
      return this;
   }

   @Override
   public IJoin perform(final Operation operation)
   {
      this.operation = operation;
      return this;
   }

   @Override
   public IJoin withId(final String id)
   {
      this.id = id;
      return this;
   }

   @Override
   public String toString()
   {
      return "Join [url=" + requestPattern + ", to=" + resourcePattern + ", id=" + id + ", inboundCorrection="
               + inboundCorrection + "]";
   }

   @Override
   public IJoin withChaining()
   {
      this.chainingDisabled = false;
      return this;
   }

   @Override
   public ParameterizedPatternParser getPathExpression()
   {
      return requestPath.getPathExpression();
   }

   @Override
   public ParameterizedPatternParser getResourcexpression()
   {
      return resourcePath.getPathExpression();
   }

}
