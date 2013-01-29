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
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.ParameterizedPatternParser;
import org.ocpsoft.rewrite.param.RegexParameterizedPatternBuilder;
import org.ocpsoft.rewrite.param.RegexParameterizedPatternParser;
import org.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * A {@link org.ocpsoft.rewrite.config.Condition} that inspects the value of {@link HttpServletRequest#getServerName()}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Hostname extends HttpCondition implements Parameterized
{
   private final ParameterizedPatternParser expression;

   private Hostname(final String pattern)
   {
      Assert.notNull(pattern, "Domain must not be null.");
      this.expression = new RegexParameterizedPatternParser(pattern);
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
    * By default, matching parameter values are bound to the {@link org.ocpsoft.rewrite.context.EvaluationContext}. See
    * also {@link #where(String)}
    */
   public static Hostname matches(final String pattern)
   {
      return new Hostname(pattern);
   }

   @Override
   public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      String hostName = null;

      if (event instanceof HttpOutboundServletRewrite)
      {
         hostName = event.getAddress().getHost();
         if (hostName == null)
            hostName = event.getRequest().getServerName();
      }
      else
         hostName = event.getRequest().getServerName();

      return (hostName != null && expression.matches(event, context, hostName));
   }

   /**
    * Get the underlying {@link RegexParameterizedPatternBuilder} for this {@link Hostname}
    * <p>
    * See also: {@link #where(String)}
    */
   public ParameterizedPatternParser getExpression()
   {
      return expression;
   }

   @Override
   public String toString()
   {
      return expression.toString();
   }

   public ParameterizedPatternParser getDomainExpression()
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