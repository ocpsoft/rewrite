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

import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.ParameterizedPatternParameter;
import org.ocpsoft.rewrite.param.RegexParameterizedPatternParser;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * A {@link org.ocpsoft.rewrite.config.Condition} that inspects the value of {@link HttpServletRequest#getScheme()}
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class UserAgent extends HttpCondition
{

   /**
    * Condition asserting that the user-agent matches the given pattern. (May be parameterized using {param}
    * expressions.)
    */
   public static UserAgent matches(final String pattern)
   {
      return new PatternUserAgent(pattern);
   }

   /**
    * Condition asserting that the user-agent is a mobile device.
    */
   public static ClassificationUserAgent isMobile()
   {
      return new ClassificationUserAgent(Classification.MOBILE);
   }

   /**
    * Condition asserting that the user-agent is a tablet device.
    */
   public static ClassificationUserAgent isTablet()
   {
      return new ClassificationUserAgent(Classification.TABLET);
   }

   /**
    * Condition asserting that the user-agent is a non-mobile device.
    */
   public static ClassificationUserAgent isDesktop()
   {
      return new ClassificationUserAgent(Classification.DESKTOP);
   }

   private static enum Classification
   {
      MOBILE, TABLET, DESKTOP
   }

   private static class ClassificationUserAgent extends UserAgent
   {
      private final Classification type;

      private ClassificationUserAgent(final Classification type)
      {
         Assert.notNull(type, "Classification must not be null.");
         this.type = type;
      }

      @Override
      public boolean evaluateHttp(HttpServletRewrite event, EvaluationContext context)
      {
         String userAgentStr = event.getRequest().getHeader("user-agent");
         String httpAccept = event.getRequest().getHeader("Accept");
         UserAgentUtil util = new UserAgentUtil(userAgentStr, httpAccept);

         switch (type)
         {
         case DESKTOP:
            return !util.isMobile();
         case MOBILE:
            return util.isMobile();
         case TABLET:
            return util.isTablet();
         }

         return false;
      }
   }

   private static class PatternUserAgent extends UserAgent implements
            Parameterized<ParameterizedPatternParameter, String>
   {
      private final RegexParameterizedPatternParser expression;

      private PatternUserAgent(final String pattern)
      {
         Assert.notNull(pattern, "Scheme must not be null.");
         this.expression = new RegexParameterizedPatternParser(pattern);
      }

      @Override
      public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
      {
         String agent = event.getRequest().getHeader("user-agent");
         return agent != null && expression.matches(event, context, agent);
      }

      @Override
      public String toString()
      {
         return expression.toString();
      }

      @Override
      public ParameterStore<ParameterizedPatternParameter> getParameterStore()
      {
         return expression.getParameterStore();
      }
   }
}