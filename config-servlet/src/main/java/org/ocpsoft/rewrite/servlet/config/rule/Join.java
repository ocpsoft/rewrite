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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.config.ConditionBuilder;
import org.ocpsoft.rewrite.config.ConditionVisit;
import org.ocpsoft.rewrite.config.ConfigurationRuleParameterBuilder;
import org.ocpsoft.rewrite.config.ParameterizedCallback;
import org.ocpsoft.rewrite.config.ParameterizedConditionVisitor;
import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.InboundRewrite;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.ParameterizedPattern;
import org.ocpsoft.rewrite.servlet.config.DispatchType;
import org.ocpsoft.rewrite.servlet.config.Forward;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.Query;
import org.ocpsoft.rewrite.servlet.config.Redirect;
import org.ocpsoft.rewrite.servlet.config.Substitute;
import org.ocpsoft.rewrite.servlet.config.bind.RequestBinding;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.servlet.spi.RequestParameterProvider;
import org.ocpsoft.rewrite.servlet.util.QueryStringBuilder;
import org.ocpsoft.urlbuilder.Address;

/**
 * {@link Rule} that creates a bi-directional rewrite rule between an externally facing {@link Address} and an internal
 * server resource {@link Address} for the purposes of changing the {@link Address} with which the internal server
 * resource is accessible.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@SuppressWarnings("deprecation")
public class Join implements Rule, JoinPath, Parameterized
{
   private static final String JOIN_DISABLED_KEY = Join.class.getName() + "_DISABLED";

   private static final String CURRENT_JOIN = Join.class.getName() + "_current";

   private String id;

   private final String requestPattern;
   private String resourcePattern;
   private final Path requestPath;
   private Path resourcePath;

   private boolean inboundCorrection = false;
   private boolean chainingDisabled = true;
   private boolean bindingEnabled = true;

   private Set<String> pathRequestParameters;

   private ConditionBuilder outboundConditionCache;

   private ParameterStore store;

   protected Join(final String pattern, boolean requestBinding)
   {
      this.requestPattern = pattern;
      this.requestPath = Path.matches(pattern);
      if (requestBinding)
         requestPath.withRequestBinding();
      this.bindingEnabled = requestBinding;
   }

   /**
    * Create a {@link Rule} specifying the inbound request {@link Address} to which this {@link Join} will apply. Any
    * {@link Parameter} instances defined in the given pattern will be bound by default to the
    * {@link HttpServletRequest#getParameterMap()} via the {@link RequestParameterProvider} SPI.
    * <p>
    * To disable {@link RequestBinding} parameter {@link Binding}, instead use {@link #pathNonBinding(String)}.
    * <p>
    * The given pattern may be parameterized:
    * <p>
    * <code>
    *    /example/{param} <br>
    *    /example/{param1}/sub/{param2} <br>
    *    ...
    * </code>
    * <p>
    * 
    * @param pattern {@link ParameterizedPattern} matching the requested path.
    * 
    * @see ConfigurationRuleParameterBuilder#where(String)
    */
   public static JoinPath path(final String pattern)
   {
      return new Join(pattern, true);
   }

   /**
    * Create a {@link Rule} specifying the inbound request {@link Address} to which this {@link Join} will apply. Any
    * {@link Parameter} instances defined in the given pattern will <b>NOT</b> be bound by default to the
    * {@link HttpServletRequest#getParameterMap()}.
    * <p>
    * To enable {@link RequestBinding} parameter {@link Binding}, instead use {@link #path(String)}.
    * <p>
    * The given pattern may be parameterized:
    * <p>
    * <code>
    *    /example/{param} <br>
    *    /example/{param1}/sub/{param2} <br>
    *    ...
    * </code>
    * <p>
    * 
    * @param pattern {@link ParameterizedPattern} matching the requested path.
    * 
    * @see ConfigurationRuleParameterBuilder#where(String)
    */
   public static JoinPath pathNonBinding(String pattern)
   {
      return new Join(pattern, false);
   }

   /**
    * Retrieve the {@link Join} that was invoked on the current {@link HttpServletRequest}; if no {@link Join} was
    * invoked, return <code>null</code>.
    */
   public static Join getCurrentJoin(final HttpServletRequest request)
   {
      return (Join) request.getAttribute(CURRENT_JOIN);
   }

   @Override
   public Join to(final String resource)
   {
      if (this.resourcePattern != null)
      {
         throw new IllegalStateException("Cannot set resource path more than once.");
      }
      this.resourcePattern = resource;
      this.resourcePath = Path.matches(resource);

      Set<String> parameters = getPathRequestParameters();
      if (outboundConditionCache == null)
      {
         this.outboundConditionCache = resourcePath;
         for (String name : parameters) {
            Query parameter = Query.parameterExists(name);
            outboundConditionCache = outboundConditionCache.and(parameter);
         }
      }

      return this;
   }

   /**
    * Specifies that requests for the original internal resource path specified by {@link Join#to(String)} will be
    * redirected to the updated path specified by {@link Join#path(String)}.
    */
   public Join withInboundCorrection()
   {
      this.inboundCorrection = true;
      return this;
   }

   /**
    * Enable the target of this {@link Join}, specified by {@link Join#to(String)}, to be intercepted by the
    * {@link Join#path(String)} of another {@link Join} instance. If not activated, subsequent matching {@link Join}
    * instances will not be evaluated on the current {@link InboundRewrite} instance.
    */
   public Join withChaining()
   {
      this.chainingDisabled = false;
      return this;
   }

   @Override
   public boolean evaluate(final Rewrite event, final EvaluationContext context)
   {
      if (event instanceof HttpInboundServletRewrite)
      {
         if (!isChainingDisabled(event) && requestPath.evaluate(event, context))
         {
            return true;
         }
         else if (inboundCorrection && resourcePath.andNot(DispatchType.isForward()).evaluate(event, context))
         {
            Set<String> parameters = getPathRequestParameters();
            for (String param : parameters) {
               Query query = Query.parameterExists(param);
               query.setParameterStore(store);
               if (!query.evaluate(event, context))
               {
                  return false;
               }
            }
            Redirect redirect = Redirect.permanent(((HttpInboundServletRewrite) event).getContextPath()
                     + requestPattern);
            redirect.setParameterStore(store);
            context.addPreOperation(redirect);
            return true;
         }
      }
      else if ((event instanceof HttpOutboundServletRewrite))
      {
         if (outboundConditionCache.evaluate(event, context))
         {
            return true;
         }
      }

      return false;
   }

   private boolean isChainingDisabled(Rewrite event)
   {
      return Boolean.TRUE.equals(event.getRewriteContext().get(JOIN_DISABLED_KEY));
   }

   private Set<String> getPathRequestParameters()
   {
      /*
       * For performance purposes - think and profile this if changed.
       */
      if (pathRequestParameters == null)
      {
         Set<String> nonQueryParameters = resourcePath.getRequiredParameterNames();
         Set<String> queryParameters = requestPath.getRequiredParameterNames();
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
         Forward forward = Forward.to(resourcePattern);
         forward.setParameterStore(store);
         forward.perform(event, context);
      }

      else if (event instanceof HttpOutboundServletRewrite)
      {
         Set<String> parameters = getPathRequestParameters();

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

         Address outboundAddress = ((HttpOutboundServletRewrite) event).getOutboundAddress();

         Substitute substitute = Substitute.with(requestPattern + query.toQueryString());
         substitute.setParameterStore(store);

         substitute.perform(event, context);

         Address rewrittenAddress = ((HttpOutboundServletRewrite) event).getOutboundAddress();
         String rewrittenPath = rewrittenAddress.getPath();
         String contextPath = ((HttpServletRewrite) event).getContextPath();
         if (!contextPath.equals("/") && rewrittenPath.startsWith(contextPath))
            rewrittenPath = rewrittenPath.substring(contextPath.length());

         if (!outboundAddress.equals(rewrittenAddress)
                  && !requestPath.getExpression().parse(rewrittenPath).submit(event, context))
         {
            ((HttpOutboundServletRewrite) event).setOutboundAddress(rewrittenAddress);
         }
         else if (chainingDisabled)
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
   public String getId()
   {
      return id;
   }

   @Override
   public String toString()
   {
      String result = "Join.";

      if (bindingEnabled)
         result += "path";
      else
         result += "pathNonBinding";

      result += "(\"" + requestPattern + "\").to(\"" + resourcePattern + "\")";
      if (inboundCorrection)
         result += ".withInboundCorrection()";
      if (!chainingDisabled)
         result += ".withChaning()";
      return result;
   }

   @Override
   public Set<String> getRequiredParameterNames()
   {
      final Set<String> result = new LinkedHashSet<String>();
      result.addAll(requestPath.getRequiredParameterNames());
      result.addAll(resourcePath.getRequiredParameterNames());
      if (outboundConditionCache != null)
      {
         ParameterizedConditionVisitor visitor = new ParameterizedConditionVisitor(new ParameterizedCallback() {
            @Override
            public void call(Parameterized parameterized)
            {
               result.addAll(parameterized.getRequiredParameterNames());
            }
         });

         new ConditionVisit(outboundConditionCache).accept(visitor);
      }
      return result;
   }

   @Override
   public void setParameterStore(final ParameterStore store)
   {
      this.store = store;
      requestPath.setParameterStore(store);
      resourcePath.setParameterStore(store);
      if (outboundConditionCache != null)
      {
         ParameterizedConditionVisitor visitor = new ParameterizedConditionVisitor(new ParameterizedCallback() {
            @Override
            public void call(Parameterized parameterized)
            {
               parameterized.setParameterStore(store);
            }
         });

         new ConditionVisit(outboundConditionCache).accept(visitor);
      }
   }

}
