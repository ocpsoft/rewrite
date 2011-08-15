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
package com.ocpsoft.rewrite.servlet.config;

import java.util.Map;

import com.ocpsoft.rewrite.bind.Binding;
import com.ocpsoft.rewrite.bind.Bindings;
import com.ocpsoft.rewrite.bind.ParameterizedPattern;
import com.ocpsoft.rewrite.config.Condition;
import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.param.ConditionParameterBuilder;
import com.ocpsoft.rewrite.param.Parameter;
import com.ocpsoft.rewrite.param.ParameterizedCondition;
import com.ocpsoft.rewrite.servlet.config.bind.Request;
import com.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import com.ocpsoft.rewrite.util.Assert;

/**
 * A {@link Condition} that inspects the value of {@link HttpServletRewrite#getRequestURL()}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Path extends HttpCondition implements ParameterizedCondition<ConditionParameterBuilder<String>, String>
{
   private final ParameterizedPattern expression;

   private Path(final String pattern)
   {
      Assert.notNull(pattern, "Path must not be null.");
      this.expression = new ParameterizedPattern("[^/]+", pattern);
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
    * By default, matching parameter values are bound to the {@link EvaluationContext}. See also {@link #where(String)}
    */
   public static Path matches(final String pattern)
   {
      return new Path(pattern);
   }

   /**
    * Bind each path parameter to the corresponding request parameter by name. By default, matching values are bound to
    * the {@link EvaluationContext}.
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
   public ConditionParameterBuilder<String> where(final String param)
   {
      return new ConditionParameterBuilder<String>(this, expression.getParameter(param));
   }

   @Override
   public ConditionParameterBuilder<String> where(final String param, final String pattern)
   {
      return where(param).matches(pattern);
   }

   @Override
   public ConditionParameterBuilder<String> where(final String param, final String pattern,
            final Binding binding)
   {
      return where(param, pattern).bindsTo(binding);
   }

   @Override
   public ConditionParameterBuilder<String> where(final String param, final Binding binding)
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
         requestURL = event.getRequestURL();

      if (expression.matches(requestURL))
      {
         Map<Parameter<String>, String[]> parameters = expression.parseEncoded(requestURL);
         Bindings.enqueueSubmissions(event, context, parameters);
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