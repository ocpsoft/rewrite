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
import org.ocpsoft.rewrite.param.RegexParameterizedPatternParser;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * A {@link Condition} that inspects the value of {@link HttpServletRequest#getHeader(String)} "User-Agent"
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class UserAgent extends HttpCondition
{

   /**
    * Create a {@link Condition} asserting that the user-agent matches the given pattern.
    * <p>
    * User agent expressions may be parameterized:
    * <p>
    * <code>
    * UserAgent.matches("BlackBerry PlayBook 3.4")<br/>
    * UserAgent.matches("BlackBerry PlayBook {version}")<br/>
    * UserAgent.matches("{agent}")<br/>
    * ...
    * </code>
    * 
    * @param name {@link ParameterizedPattern} matching the request parameter name.
    * 
    * @see ConfigurationRuleParameterBuilder#where(String) {@link HttpServletRequest#getParameterMap()}
    */
   public static UserAgent matches(final String pattern)
   {
      return new PatternUserAgent(pattern);
   }

   /**
    * Create a {@link Condition} asserting that the user-agent is a mobile device.
    */
   public static ClassificationUserAgent isMobile()
   {
      return new ClassificationUserAgent(Classification.MOBILE) {
         @Override
         public String toString()
         {
            return "UserAgent.isMobile()";
         }
      };
   }

   /**
    * Create a {@link Condition} asserting that the user-agent is a tablet device.
    */
   public static ClassificationUserAgent isTablet()
   {
      return new ClassificationUserAgent(Classification.TABLET) {
         @Override
         public String toString()
         {
            return "UserAgent.isTablet()";
         }
      };
   }

   /**
    * Create a {@link Condition} asserting that the user-agent is a non-mobile device.
    */
   public static ClassificationUserAgent isDesktop()
   {
      return new ClassificationUserAgent(Classification.DESKTOP) {
         @Override
         public String toString()
         {
            return "UserAgent.isDesktop()";
         }
      };
   }

   private static enum Classification
   {
      MOBILE, TABLET, DESKTOP
   }

   private abstract static class ClassificationUserAgent extends UserAgent
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
            return !util.detectMobileQuick() && !util.detectTierTablet();
         case MOBILE:
            return util.detectMobileQuick();
         case TABLET:
            return util.detectTierTablet();
         }

         return false;
      }
   }

   private static class PatternUserAgent extends UserAgent implements Parameterized
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
         return agent != null && expression.parse(agent).submit(event, context);
      }

      @Override
      public String toString()
      {
         return expression.toString();
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
}