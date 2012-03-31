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

import java.util.List;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ConfigurationRuleBuilder extends ConfigurationBuilder
{
   private final ConfigurationBuilder wrapped;
   private final RuleBuilder rule;

   ConfigurationRuleBuilder(final ConfigurationBuilder config, final RuleBuilder rule)
   {
      this.wrapped = config;
      this.rule = rule;
   }

   @Override
   public ConfigurationBuilder addRule(final Rule rule)
   {
      return wrapped.addRule(rule);
   }

   @Override
   public ConfigurationRuleBuilder defineRule()
   {
      return wrapped.defineRule();
   }

   /**
    * Set the {@link Condition} of this {@link Rule} instance.
    */
   public ConfigurationRuleBuilder when(final Condition condition)
   {
      rule.when(condition);
      return this;
   }

   /**
    * Specify the {@link Operation} to be performed when this {@link Rule} is invoked. (May be a composite
    * {@link Operation})
    */
   public ConfigurationRuleBuilder perform(final Operation operation)
   {
      rule.perform(operation);
      return this;
   }

   /**
    * Set the priority of this {@link Rule} instance. If {@link #priority()} differs from the priority of the
    * {@link ConfigurationProvider} from which this rule was returned, then relocate this rule to its new priority
    * position in the compiled rule set.
    */
   public ConfigurationRuleBuilder withPriority(int priority)
   {
      rule.withPriority(priority);
      return this;
   }

   @Override
   public List<Rule> getRules()
   {
      return wrapped.getRules();
   }

}
