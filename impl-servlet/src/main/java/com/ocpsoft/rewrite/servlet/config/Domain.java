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

import javax.servlet.http.HttpServletRequest;

import com.ocpsoft.common.util.Assert;
import com.ocpsoft.rewrite.bind.Binding;
import com.ocpsoft.rewrite.bind.Bindings;
import com.ocpsoft.rewrite.bind.Evaluation;
import com.ocpsoft.rewrite.bind.ParameterizedPattern;
import com.ocpsoft.rewrite.bind.RegexConditionParameterBuilder;
import com.ocpsoft.rewrite.bind.RegexParameter;
import com.ocpsoft.rewrite.config.Condition;
import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.param.ConditionParameterBuilder;
import com.ocpsoft.rewrite.param.Parameter;
import com.ocpsoft.rewrite.param.ParameterizedCondition;
import com.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import com.ocpsoft.rewrite.servlet.util.URLBuilder;

/**
 * A {@link Condition} that inspects the value of {@link HttpServletRequest#getServerName()}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Domain extends HttpCondition implements
         ParameterizedCondition<ConditionParameterBuilder<RegexConditionParameterBuilder, String>, String>
{
   private final ParameterizedPattern expression;

   private Domain(final String pattern)
   {
      Assert.notNull(pattern, "Domain must not be null.");
      this.expression = new ParameterizedPattern(pattern);

      for (Parameter<String> parameter : this.expression.getParameters().values()) {
         parameter.bindsTo(Evaluation.property(parameter.getName()));
      }
   }

   /**
    * Inspect the current request domain, comparing against the given pattern.
    * <p>
    * The given pattern may be parameterized using the following format:
    * <p>
    * <code>
    *    example.com
    *    {domain}.com <br>
    *    www.{domain}.{suffix} <br>
    *    ... and so on
    * </code>
    * <p>
    * By default, matching parameter values are bound to the {@link EvaluationContext}. See also {@link #where(String)}
    */
   public static Domain matches(final String pattern)
   {
      return new Domain(pattern);
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
      String hostName = null;

      if (event instanceof HttpOutboundServletRewrite)
      {
         String url = event.getURL();
         URLBuilder builder = URLBuilder.build(url);
         hostName = builder.toURI().getHost();
         if (hostName == null)
            hostName = event.getRequest().getServerName();
      }
      else
         hostName = event.getRequest().getServerName();

      if (expression.matches(event, context, hostName))
      {
         Map<RegexParameter, String[]> parameters = expression.parse(event, context, hostName);
         if (Bindings.enqueuePreOperationSubmissions(event, context, parameters))
            return true;
      }
      return false;
   }

   /**
    * Get the underlying {@link ParameterizedPattern} for this {@link Domain}
    * <p>
    * See also: {@link #where(String)}
    */
   public ParameterizedPattern getExpression()
   {
      return expression;
   }

   @Override
   public String toString()
   {
      return expression.toString();
   }
}