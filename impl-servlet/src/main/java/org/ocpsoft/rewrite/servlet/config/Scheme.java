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

import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.Bindings;
import org.ocpsoft.rewrite.bind.Evaluation;
import org.ocpsoft.rewrite.bind.ParameterizedPattern;
import org.ocpsoft.rewrite.bind.ParameterizedPattern.RegexParameter;
import org.ocpsoft.rewrite.bind.RegexConditionParameterBuilder;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.param.ConditionParameterBuilder;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.param.ParameterizedCondition;
import org.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.servlet.util.URLBuilder;

/**
 * A {@link org.ocpsoft.rewrite.config.Condition} that inspects the value of {@link HttpServletRequest#getScheme()}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Scheme extends HttpCondition implements
ParameterizedCondition<ConditionParameterBuilder<RegexConditionParameterBuilder, String>, String>
{
   private final ParameterizedPattern expression;

   private Scheme(final String pattern)
   {
      Assert.notNull(pattern, "Scheme must not be null.");
      this.expression = new ParameterizedPattern(pattern);

      for (Parameter<String> parameter : this.expression.getParameters().values()) {
         parameter.bindsTo(Evaluation.property(parameter.getName()));
      }
   }

   /**
    * Inspect the current request scheme, comparing against the given pattern.
    * <p>
    * The given pattern may be parameterized using the following format:
    * <p>
    * <code>
    *    https
    *    {scheme}
    *    {scheme}-custom <br>
    *    ... and so on
    * </code>
    * <p>
    * By default, matching parameter values are bound to the {@link org.ocpsoft.rewrite.context.EvaluationContext}. See also {@link #where(String)}
    */
   public static Scheme matches(final String pattern)
   {
      return new Scheme(pattern);
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
      String scheme = null;

      if (event instanceof HttpOutboundServletRewrite)
      {
         String url = event.getURL();
         URLBuilder builder = URLBuilder.createFrom(url);
         scheme = builder.toURI().getScheme();
         if (scheme == null)
            scheme = event.getRequest().getScheme();
      }
      else
         scheme = event.getRequest().getScheme();

      if (scheme != null && expression.matches(event, context, scheme))
      {
         Map<RegexParameter, String[]> parameters = expression.parse(event, context, scheme);
         if (Bindings.enqueuePreOperationSubmissions(event, context, parameters))
            return true;
      }
      return false;
   }

   /**
    * Get the underlying {@link ParameterizedPattern} for this {@link Scheme}
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