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

import java.util.Map;

import org.ocpsoft.common.util.Assert;

import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.Bindings;
import org.ocpsoft.rewrite.bind.Evaluation;
import org.ocpsoft.rewrite.bind.ParameterizedPattern;
import org.ocpsoft.rewrite.bind.RegexConditionParameterBuilder;
import org.ocpsoft.rewrite.bind.RegexParameter;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.param.ConditionParameterBuilder;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.param.ParameterizedCondition;
import org.ocpsoft.rewrite.servlet.config.bind.Request;
import org.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * A {@link org.ocpsoft.rewrite.config.Condition} that inspects the value of {@link org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite#getRequestPath()}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Path extends HttpCondition implements
        ParameterizedCondition<ConditionParameterBuilder<RegexConditionParameterBuilder, String>, String>
{
   private final ParameterizedPattern expression;

   private Path(final String pattern)
   {
      Assert.notNull(pattern, "Path must not be null.");
      this.expression = new ParameterizedPattern("[^/]+", pattern);

      for (Parameter<String> parameter : expression.getParameters().values()) {
         parameter.bindsTo(Evaluation.property(parameter.getName()));
      }
   }

   /**
    * Inspect the current request URL, comparing against the given pattern.
    * <p>
    * The given pattern may be parameterized using the following format:
    * <p>
    * <code>
    *    /example/{param} <br>
    *    /example/{value}/sub/{value2} <br>
    *    ... and so on
    * </code>
    * <p>
    * By default, matching parameter values are bound to the {@link org.ocpsoft.rewrite.context.EvaluationContext}. See also {@link #where(String)}
    */
   public static Path matches(final String pattern)
   {
      return new Path(pattern);
   }

   /**
    * Bind each path parameter to the corresponding request parameter by name. By default, matching values are bound to
    * the {@link org.ocpsoft.rewrite.context.EvaluationContext}.
    * <p>
    * See also {@link #where(String)}
    */
   public Path withRequestBinding()
   {
      for (Parameter<String> parameter : expression.getParameters().values()) {
         parameter.bindsTo(Request.parameter(parameter.getName()));
      }
      return this;
   }

   @Override
   public RegexConditionParameterBuilder where(final String param)
   {
      return new RegexConditionParameterBuilder(this, expression.getParameter(param));
   }

   @Override
   public RegexConditionParameterBuilder where(final String param, final String pattern)
   {
      return where(param).matches(pattern);
   }

   @Override
   public RegexConditionParameterBuilder where(final String param, final String pattern,
            final Binding binding)
   {
      return where(param, pattern).bindsTo(binding);
   }

   @Override
   public RegexConditionParameterBuilder where(final String param, final Binding binding)
   {
      return where(param).bindsTo(binding);
   }

   @Override
   public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      String requestURL = null;

      if (event instanceof HttpOutboundServletRewrite)
      {
         requestURL = ((HttpOutboundServletRewrite) event).getOutboundURL().split("\\?")[0];
         if (requestURL.startsWith(event.getContextPath()))
         {
            requestURL = requestURL.substring(event.getContextPath().length());
         }
      }
      else
         requestURL = event.getRequestPath();

      if (expression.matches(event, context, requestURL))
      {
         Map<RegexParameter, String[]> parameters = expression.parse(event, context, requestURL);
         if (Bindings.enqueuePreOperationSubmissions(event, context, parameters))
            return true;
      }
      return false;
   }

   /**
    * Get the underlying {@link ParameterizedPattern} for this {@link Path}
    * <p>
    * See also: {@link #where(String)}
    */
   public ParameterizedPattern getPathExpression()
   {
      return expression;
   }

   @Override
   public String toString()
   {
      return expression.toString();
   }
}