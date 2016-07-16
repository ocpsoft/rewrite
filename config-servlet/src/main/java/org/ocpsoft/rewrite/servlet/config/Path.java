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

import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.config.ConfigurationRuleParameterBuilder;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.param.ConfigurableParameter;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.ParameterizedPattern;
import org.ocpsoft.rewrite.param.ParameterizedPatternParser;
import org.ocpsoft.rewrite.param.RegexConstraint;
import org.ocpsoft.rewrite.param.RegexParameterizedPatternParser;
import org.ocpsoft.rewrite.servlet.config.bind.RequestBinding;
import org.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.servlet.spi.RequestParameterProvider;
import org.ocpsoft.urlbuilder.Address;
import org.ocpsoft.urlbuilder.AddressBuilder;

/**
 * A {@link Condition} that inspects the value of {@link HttpServletRewrite#getRequestPath()}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class Path extends HttpCondition implements Parameterized
{
   private final ParameterizedPatternParser expression;
   private boolean withRequestBinding = false;
   private String captureIn;

   private Path(final String pattern)
   {
      Assert.notNull(pattern, "Path must not be null.");
      this.expression = new RegexParameterizedPatternParser("[^/]+", pattern);
   }

   /**
    * Create a {@link Condition} that compares the current {@link Address#getPath()} to the given pattern.
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
    * @param pattern {@link ParameterizedPattern} matching the path.
    * 
    * @see ConfigurationRuleParameterBuilder#where(String)
    */
   public static Path matches(final String pattern)
   {
      return new Path(pattern) {
         @Override
         public String toString()
         {
            return "Path.matches(\"" + pattern + "\")";
         }
      };
   }

   /**
    * Capture the entire path portion of the {@link Address} into the given {@link Parameter}.
    * 
    * @param param the name of the {@link Parameter} to which the entire path portion of the {@link Address} will be
    *           bound.
    */
   public static Path captureIn(final String param)
   {
      Path path = new Path("{" + param + "}") {
         @Override
         public String toString()
         {
            return "Path.captureIn(\"" + param + "\")";
         }
      };
      path.captureIn = param;
      return path;
   }

   /**
    * Bind each path {@link Parameter} to the corresponding request parameter by name.
    * <p>
    * 
    * @see {@link ConfigurationRuleParameterBuilder#where(String)} {@link HttpServletRequest#getParameterMap()}
    *      {@link RequestParameterProvider}
    */
   public Path withRequestBinding()
   {
      withRequestBinding = true;
      return this;
   }

   @Override
   public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      String url = null;

      if (event instanceof HttpOutboundServletRewrite)
      {
         url = ((HttpOutboundServletRewrite) event).getOutboundAddress().getPath();
         if (url == null) // e.g an external url like http://ocpsoft.org (without trailing slash) or an anchor link have
                          // a null path
            return false;
      }
      else
         url = AddressBuilder.begin().pathDecoded(event.getInboundAddress().getPath()).buildLiteral().toString();

      String contextPath = event.getContextPath();
      if (!contextPath.equals("/") && url.startsWith(contextPath))
         url = url.substring(contextPath.length());

      return expression.parse(url).submit(event, context);
   }

   /**
    * Get the underlying {@link ParameterizedPattern} for this {@link Path}
    */
   public ParameterizedPatternParser getExpression()
   {
      return expression;
   }

   @Override
   public Set<String> getRequiredParameterNames()
   {
      Set<String> result = new LinkedHashSet<String>();
      if (captureIn != null)
         result.add(captureIn);
      else
         result.addAll(expression.getRequiredParameterNames());
      return result;
   }

   @Override
   public void setParameterStore(ParameterStore store)
   {
      if (captureIn != null)
      {
         Parameter<?> parameter = store.get(captureIn);
         if (parameter instanceof ConfigurableParameter<?>)
            ((ConfigurableParameter<?>) parameter).constrainedBy(new RegexConstraint(".*"));
      }
      if (withRequestBinding)
      {
         for (String param : getRequiredParameterNames()) {
            Parameter<?> parameter = store.get(param);
            if (parameter instanceof ConfigurableParameter<?>)
               ((ConfigurableParameter<?>) parameter).bindsTo(RequestBinding.parameter(param));
         }
         withRequestBinding = true;
      }

      expression.setParameterStore(store);
   }
}