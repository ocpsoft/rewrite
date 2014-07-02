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
package org.ocpsoft.rewrite.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ocpsoft.common.util.Strings;
import org.ocpsoft.rewrite.context.Context;

/**
 * A fluent builder for defining {@link Configuration} objects.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ConfigurationBuilder implements Configuration, ConfigurationBuilderRoot
{
   private final List<RuleBuilder> rules = new ArrayList<RuleBuilder>();

   ConfigurationBuilder()
   {}

   @Override
   public List<Rule> getRules()
   {
      return Collections.<Rule> unmodifiableList(rules);
   }

   public List<RuleBuilder> getRuleBuilders()
   {
      return Collections.unmodifiableList(rules);
   }

   /**
    * Begin defining a new fluent {@link Configuration}.
    */
   public static ConfigurationBuilder begin()
   {
      return new ConfigurationBuilder();
   }

   /**
    * Add a new fluent {@link Rule}.
    */
   @Override
   public ConfigurationRuleBuilderCustom addRule()
   {
      RuleBuilder rule = RuleBuilder.define();
      setDefaultLocation(rule);
      rules.add(rule);
      return new ConfigurationRuleBuilder(this, rule);
   }

   ConfigurationRuleBuilderCustom addOtherwiseRule(RuleBuilder origin)
   {
      RuleBuilder rule = RuleBuilder.define();

      /**
       * .otherwise() clauses should create a Rule that shares state with its origin.
       */
      rule.contextMap = origin.contextMap;
      rules.add(rule);
      return new ConfigurationRuleBuilder(this, rule);
   }

   /**
    * Add a {@link Rule}.
    */
   @Override
   public ConfigurationRuleBuilder addRule(Rule rule)
   {
      RuleBuilder wrapped = null;
      if (rule instanceof RuleBuilder)
         wrapped = (RuleBuilder) rule;
      else
         wrapped = RuleBuilder.wrap(rule);

      rules.add(wrapped);
      setDefaultLocation(wrapped);
      return new ConfigurationRuleBuilder(this, wrapped);
   }

   private void setDefaultLocation(Rule rule)
   {
      if (rule instanceof Context)
      {
         Throwable throwable = new Throwable().fillInStackTrace();
         StackTraceElement[] trace = throwable.getStackTrace();

         StackTraceElement location = null;
         for (StackTraceElement element : trace) {
            if (!element.getClassName().startsWith("org.ocpsoft.rewrite.config"))
            {
               location = element;
               break;
            }
         }

         if (location != null && ((Context) rule).get(RuleMetadata.PROVIDER_LOCATION) == null)
         {
            ((Context) rule).put(RuleMetadata.PROVIDER_LOCATION, location.toString());
         }
      }
   }

   @Override
   public String toString()
   {
      return "ConfigurationBuilder.begin()\n" + Strings.join(rules, "\n");
   }

}
