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
package org.ocpsoft.rewrite.servlet.config;

import java.util.Set;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.config.ConfigurationRuleParameterBuilder;
import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.InboundRewrite;
import org.ocpsoft.rewrite.param.ConfigurableParameter;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.ParameterizedPattern;
import org.ocpsoft.rewrite.param.ParameterizedPatternParser;
import org.ocpsoft.rewrite.param.RegexParameterizedPatternBuilder;
import org.ocpsoft.rewrite.param.RegexParameterizedPatternParser;
import org.ocpsoft.rewrite.servlet.config.bind.RequestBinding;
import org.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.urlbuilder.Address;

/**
 * A {@link Condition} that inspects the entire value of the current {@link Address}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class URL extends HttpCondition implements Parameterized
{
   private final ParameterizedPatternParser expression;
   private boolean requestBinding;

   private URL(final String pattern)
   {
      Assert.notNull(pattern, "URL must not be null.");
      this.expression = new RegexParameterizedPatternParser(".*", pattern);
   }

   /**
    * Create a {@link Condition} that inspects the entire value of the current {@link Address}, comparing against the
    * given pattern.
    * <p>
    * The given pattern may be parameterized:
    * <p>
    * <b>INBOUND:</b><br>
    * <code>
    *    /path/store?item=1436
    *    /path/store?item={itemId} <br>
    *    /path/{store}?item={itemId}&category={catId} <br>
    *    ...
    * </code>
    * <p>
    * <b>OUTBOUND:</b><br>
    * <code>
    * http://domain.com/path/store.html?item=1436
    * http://domain.com/path/store?item={itemId} <br>
    * /path/{store}?item={itemId}&category={catId} <br>
    * ...
    * </code>
    * <p>
    * 
    * @param url {@link ParameterizedPattern} to which the current {@link Address} must match.
    * 
    * @see {@link ConfigurationRuleParameterBuilder#where(String)}
    */
   public static URL matches(final String pattern)
   {
      return new URL(pattern) {
         @Override
         public String toString()
         {
            return "URL.matches(\"" + pattern + "\")";
         }
      };
   }

   /**
    * Create a {@link Condition} to capture the entire value of the current {@link Address} into the given
    * {@link Parameter} name. This {@link Parameter} may be referenced in other {@link Condition} and {@link Operation}
    * instances within the current {@link Rule}.
    * 
    * @see ConfigurationRuleParameterBuilder#where(String)
    */
   public static URL captureIn(final String param)
   {
      URL path = new URL("{" + param + "}") {
         @Override
         public String toString()
         {
            return "URL.captureIn(\"" + param + "\")";
         }
      };
      return path;
   }

   /**
    * Bind each {@link Parameter} value of the current {@link Address} to the corresponding request parameter by name.
    * Only takes affect on {@link InboundRewrite} events.
    */
   public URL withRequestBinding()
   {
      this.requestBinding = true;
      return this;
   }

   @Override
   public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      String requestURL = null;

      if (event instanceof HttpOutboundServletRewrite)
      {
         requestURL = ((HttpOutboundServletRewrite) event).getOutboundAddress().toString();
         String contextPath = event.getContextPath();
         if (!contextPath.equals("/") && requestURL.startsWith(contextPath))
         {
            requestURL = requestURL.substring(event.getContextPath().length());
         }
      }
      else
      {
         requestURL = event.getAddress().toString();
      }

      return expression.parse(requestURL).submit(event, context);
   }

   /**
    * Get the underlying {@link RegexParameterizedPatternBuilder} for this {@link URL}
    * <p>
    * See also: {@link #where(String)}
    */
   public ParameterizedPatternParser getSchemeExpression()
   {
      return expression;
   }

   public ParameterizedPatternParser getPathExpression()
   {
      return expression;
   }

   @Override
   public Set<String> getRequiredParameterNames()
   {
      return expression.getRequiredParameterNames();
   }

   @Override
   public void setParameterStore(ParameterStore store)
   {
      if (requestBinding)
      {
         for (String param : getRequiredParameterNames()) {
            Parameter<?> parameter = store.get(param);
            if (parameter instanceof ConfigurableParameter<?>)
               ((ConfigurableParameter<?>) parameter).bindsTo(RequestBinding.parameter(param));
         }
      }

      expression.setParameterStore(store);
   }
}