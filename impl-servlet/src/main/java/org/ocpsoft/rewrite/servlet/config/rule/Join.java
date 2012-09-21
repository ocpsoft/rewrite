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
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.ParameterizedPatternImpl;
import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.config.ConditionBuilder;
import org.ocpsoft.rewrite.config.Not;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.config.DispatchType;
import org.ocpsoft.rewrite.servlet.config.Forward;
import org.ocpsoft.rewrite.servlet.config.HttpCondition;
import org.ocpsoft.rewrite.servlet.config.IPath;
import org.ocpsoft.rewrite.servlet.config.IPath.PathParameter;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.QueryString;
import org.ocpsoft.rewrite.servlet.config.Redirect;
import org.ocpsoft.rewrite.servlet.config.SimpleForward;
import org.ocpsoft.rewrite.servlet.config.SimplePath;
import org.ocpsoft.rewrite.servlet.config.SimpleSubstitute;
import org.ocpsoft.rewrite.servlet.config.Substitute;
import org.ocpsoft.rewrite.servlet.config.bind.Request;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
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

   private boolean chainingDisabled = true;
   
   private boolean simple;

   HttpCondition disabled = new HttpCondition() {
      @Override
      public boolean evaluateHttp(HttpServletRewrite event, EvaluationContext context)
      {
         return Boolean.TRUE.equals(event.getRewriteContext().get(Join.class.getName() + "_DISABLED"));
      }
   };
   
   protected Join(final String pattern, boolean requestBinding)
   {
       this(pattern, requestBinding, false);
   }

   protected Join(final String pattern, boolean requestBinding, boolean simple)
   {
      this.pattern = pattern;
      this.requestPath = simple ? SimplePath.matches(pattern) : Path.matches(pattern);
      this.simple = simple;
      if (!simple && requestBinding)
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
    * The client-facing URL path to which this {@link Join} will apply. Paramters will not be bound to the request
    * parameter map. To enable request parameter binding and outbound URL rewriting, instead use {@link #path(String)}.
    */
   public static Join nonBindingPath(String pattern)
   {
      return new Join(pattern, false);
   }
   
   /**
    * The client-facing URL path to which this {@link Join} will apply.
    * Since this join will not use patterns to match urls, parameters are not
    * supported. Any call to a method of this join that would make use of
    * parameters will result in an {@link UnsupportedOperationException}
    */
   public static IJoin simple(String path)
   {
      return new Join(path, false, true);
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
      this.resourcePath = simple ? SimplePath.matches(resource) : Path.matches(resource);
      return this;
   }

   @Override
   public IJoin withInboundCorrection()
   {
       if(simple)
       {
           throw new UnsupportedOperationException("SimpleJoin does not support inbound correction!");
       }
       
      this.inboundCorrection = true;
      return this;
   }

   @Override
   public boolean evaluate(final Rewrite event, final EvaluationContext context)
   {
      if (event instanceof HttpInboundServletRewrite)
      {
         if (Not.any(disabled).and(requestPath).evaluate(event, context)
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
       if(simple){
           return Collections.emptyList();
       }
       
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
         if (chainingDisabled)
         {
            event.getRewriteContext().put(Join.class.getName() + "_DISABLED", true);
            event.getRewriteContext().put(Join.class.getName() + "_DISABLED_RESET_NEXT", false);
         }

         if(simple)
         {
        	 SimpleForward.to(resource).perform(event, context);
         }
         else
         {
        	 Forward.to(resource).perform(event, context);
         }
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

         if(simple)
         {
        	 SimpleSubstitute.with(pattern + query.toQueryString()).perform(event, context);
         }
         else
         {
        	 Substitute.with(pattern + query.toQueryString()).perform(event, context);
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
      return "Join [url=" + pattern + ", to=" + resource + ", id=" + id + ", inboundCorrection="
               + inboundCorrection + "]";
   }

   @Override
   public IJoin withChaining()
   {
      this.chainingDisabled = false;
      return this;
   }

   @Override
   public ParameterizedPatternImpl getPathExpression()
   {
      return requestPath.getPathExpression();
   }

   @Override
   public ParameterizedPatternImpl getResourcexpression()
   {
      return resourcePath.getPathExpression();
   }

}
