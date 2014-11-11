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
 * A {@link Condition} that inspects the value of {@link HttpServletRequest#getServerName()}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Domain extends HttpCondition implements Parameterized
{
   private final ParameterizedPatternParser expression;

   private Domain(final String pattern)
   {
      Assert.notNull(pattern, "Domain must not be null.");
      this.expression = new RegexParameterizedPatternParser(pattern);
   }

   /**
    * Create a {@link Domain} condition to inspect the current {@link Address#getDomain()}.
    * <p>
    * The given pattern may be parameterized:
    * <p>
    * <code>
    *    example.com
    *    {domain}.com <br>
    *    www.{domain}.{suffix} <br>
    *    ... and so on
    * </code>
    * <p>
    * 
    * @param pattern {@link ParameterizedPattern} matching the domain name.
    * 
    * @see {@link ConfigurationRuleParameterBuilder#where(String)} {@link HttpServletRequest#getServerName()}
    */
   public static Domain matches(final String pattern)
   {
      return new Domain(pattern);
   }

   @Override
   public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      String hostName = null;

      if (event instanceof HttpOutboundServletRewrite)
      {
         hostName = event.getAddress().getDomain();
         if (hostName == null)
            hostName = event.getRequest().getServerName();
      }
      else
         hostName = event.getRequest().getServerName();

      return (hostName != null && expression.parse(hostName).submit(event, context));
   }

   /**
    * Get the underlying {@link ParameterizedPatternParser} for this {@link Domain}
    */
   public ParameterizedPatternParser getExpression()
   {
      return expression;
   }

   @Override
   public String toString()
   {
      return "Domain.matches(\"" + expression.getPattern() + "\")";
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