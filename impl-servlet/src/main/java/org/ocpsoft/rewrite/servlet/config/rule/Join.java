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

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.ParameterizedPattern;
import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.config.ConditionBuilder;
import org.ocpsoft.rewrite.config.DefaultConditionBuilder;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.config.DispatchType;
import org.ocpsoft.rewrite.servlet.config.Forward;
import org.ocpsoft.rewrite.servlet.config.IPath;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.QueryString;
import org.ocpsoft.rewrite.servlet.config.Redirect;
import org.ocpsoft.rewrite.servlet.config.Substitute;
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
   private static final String CURRENT_JOIN = Join.class.getName() + "_current";

   private String id;

   private final String pattern;
   private String resource;
   private final IPath requestPath;
   private IPath resourcePath;

   private Operation operation;
   private Condition condition;

   private boolean inboundCorrection = false;

   protected Join(final String pattern)
   {
      this.pattern = pattern;
      this.requestPath = Path.matches(pattern);
   }

   /**
    * The outward facing URL path to which this {@link Join} will apply.
    */
   public static IJoin path(final String pattern)
   {
      return new Join(pattern);
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
      this.resource = resource;
      this.resourcePath = Path.matches(resource);
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
         requestPath.withRequestBinding();

         if (requestPath.evaluate(event, context) && ((condition == null) || condition.evaluate(event, context)))
         {
            if (operation != null)
               context.addPreOperation(operation);
            return true;
         }
         else if (inboundCorrection
                  && resourcePath.andNot(DispatchType.isForward()).evaluate(event, context)
                  && ((condition == null) || condition.evaluate(event, context)))
         {
            List<String> names = requestPath.getPathExpression().getParameterNames();
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
      else if ((event instanceof HttpOutboundServletRewrite))
      {
         List<String> parameters = getPathRequestParameters();

         ConditionBuilder outbound = resourcePath;
         for (String name : parameters)
         {
            outbound = outbound.and(QueryString.parameterExists(name));
         }
         if (outbound.evaluate(event, context) && ((condition == null) || condition.evaluate(event, context)))
         {
            if (operation != null)
               context.addPreOperation(operation);

            return true;
         }
      }

      return false;
   }

   private List<String> getPathRequestParameters()
   {
      List<String> nonQueryParameters = resourcePath.getPathExpression().getParameterNames();

      List<String> queryParameters = requestPath.getPathExpression().getParameterNames();
      queryParameters.removeAll(nonQueryParameters);
      return queryParameters;
   }

   @Override
   public void perform(final Rewrite event, final EvaluationContext context)
   {
      if (event instanceof HttpInboundServletRewrite)
      {
         saveCurrentJoin(((HttpInboundServletRewrite) event).getRequest());
         Forward.to(resource).perform(event, context);
      }

      else if (event instanceof HttpOutboundServletRewrite)
      {
         List<String> parameters = getPathRequestParameters();

         String outboundURL = ((HttpOutboundServletRewrite) event).getOutboundURL();
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

         Substitute.with(pattern + query.toQueryString()).perform(event, context);
      }
   }

   private void saveCurrentJoin(final HttpServletRequest request)
   {
      request.setAttribute(CURRENT_JOIN, this);
   }

   @Override
   public JoinParameter where(final String parameter)
   {
      return new JoinParameter(this, requestPath.getPathExpression().getParameter(parameter));
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
      return "Join [url=" + pattern + ", to=" + resource + ", id=" + id + ", inboundCorrection="
               + inboundCorrection + "]";
   }

   @Override
   public ConditionBuilder and(Condition condition)
   {
      return DefaultConditionBuilder.wrap(this.condition).and(condition);
   }

   @Override
   public ConditionBuilder andNot(Condition condition)
   {
      return DefaultConditionBuilder.wrap(this.condition).andNot(condition);
   }

   @Override
   public ConditionBuilder or(Condition condition)
   {
      return DefaultConditionBuilder.wrap(this.condition).or(condition);
   }

   @Override
   public ConditionBuilder orNot(Condition condition)
   {
      return DefaultConditionBuilder.wrap(this.condition).orNot(condition);
   }

   @Override
   public IJoin withRequestBinding()
   {
      requestPath.withRequestBinding();
      return this;
   }

   @Override
   public ParameterizedPattern getPathExpression()
   {
      return requestPath.getPathExpression();
   }

   @Override
   public ParameterizedPattern getResourcexpression()
   {
      return resourcePath.getPathExpression();
   }

}
