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

import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.config.ConfigurationRuleParameterBuilder;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.ParameterizedPattern;
import org.ocpsoft.rewrite.param.ParameterizedPatternParser;
import org.ocpsoft.rewrite.param.RegexParameterizedPatternParser;
import org.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.urlbuilder.Address;

/**
 * A {@link Condition} that inspects the value of {@link HttpServletRequest#getScheme()}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class Scheme extends HttpCondition implements Parameterized
{
   private final ParameterizedPatternParser expression;

   private Scheme(final String pattern)
   {
      Assert.notNull(pattern, "Scheme must not be null.");
      this.expression = new RegexParameterizedPatternParser(pattern);
   }

   /**
    * Create a {@link Condition} to inspect the current request scheme, comparing against the given pattern.
    * <p>
    * The given pattern may be parameterized using the following format:
    * <p>
    * <code>
    *    https<br/>
    *    mailto<br/>
    *    {scheme}</br>
    *    {scheme}-custom <br>
    *    ...
    * </code>
    * <p>
    * 
    * @param pattern {@link ParameterizedPattern} specifying the {@link Scheme} of the current {@link Address}.
    * 
    * @see {@link ConfigurationRuleParameterBuilder#where(String)}
    */
   public static Scheme matches(final String pattern)
   {
      return new Scheme(pattern) {
         @Override
         public String toString()
         {
            return "Scheme.matches(\"" + pattern + "\")";
         }
      };
   }

   @Override
   public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      String scheme = null;

      if (event instanceof HttpOutboundServletRewrite)
      {
         scheme = event.getAddress().getScheme();
         if (scheme == null)
            scheme = event.getRequest().getScheme();
      }
      else
         scheme = event.getRequest().getScheme();

      return (scheme != null && expression.parse(scheme).submit(event, context));
   }

   /**
    * Get the underlying {@link ParameterizedPatternParser} for this {@link Scheme}
    */
   public ParameterizedPatternParser getExpression()
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
      expression.setParameterStore(store);
   }
}