/*
 * Copyright 2013 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.ParameterizedRule;

/**
 * An intermediate stage {@link Rule} configuration.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ConfigurationRuleBuilder extends ConfigurationBuilder implements
         ParameterizedRule,
         ConfigurationRuleBuilderCustom,
         ConfigurationRuleBuilderWhen,
         ConfigurationRuleBuilderPerform,
         ConfigurationRuleBuilderOtherwise,
         ConfigurationRuleBuilderWithId,
         ConfigurationRuleBuilderWithPriority,
         ConfigurationRuleBuilderWithPriorityAndId
{
   private final ConfigurationBuilder wrapped;
   private final RuleBuilder rule;

   ConfigurationRuleBuilder(final ConfigurationBuilder config, final RuleBuilder rule)
   {
      this.wrapped = config;
      this.rule = rule;
   }

   @Override
   public ConfigurationRuleBuilder addRule(final Rule rule)
   {
      return wrapped.addRule(rule);
   }

   @Override
   public ConfigurationRuleBuilderCustom addRule()
   {
      return wrapped.addRule();
   }

   /**
    * Configure the {@link Parameter} with the given name.
    */
   @Override
   public ConfigurationRuleParameterBuilder where(String name)
   {
      return new ConfigurationRuleParameterBuilder(this, rule.where(name));
   }

   /**
    * Set the {@link Condition} of this {@link Rule} instance.
    */
   @Override
   public ConfigurationRuleBuilder when(final Condition condition)
   {
      rule.when(condition);
      return this;
   }

   /**
    * Perform the given {@link Operation} when the conditions set in this {@link Rule} are met.
    */
   @Override
   public ConfigurationRuleBuilder perform(final Operation operation)
   {
      rule.perform(operation);
      return this;
   }

   /**
    * Perform the given {@link Operation} when the conditions set in this {@link Rule} fail to be met.
    */
   @Override
   public ConfigurationRuleBuilder otherwise(final Operation operation)
   {
      wrapped.addOtherwiseRule(rule)
               .when(Not.any(rule))
               .perform(operation);

      return this;
   }

   /**
    * Set the priority of this {@link Rule} instance. If {@link #priority()} differs from the priority of the
    * {@link ConfigurationProvider} from which this rule was returned, then relocate this rule to its new priority
    * position in the compiled rule set.
    */
   @Override
   public ConfigurationRuleBuilder withPriority(int priority)
   {
      rule.withPriority(priority);
      return this;
   }

   /**
    * Set the ID for the current {@link Rule}. This may be used in logging and for rule lookup purposes.
    */
   @Override
   public ConfigurationRuleBuilder withId(String id)
   {
      rule.withId(id);
      return this;
   }

   /**
    * Provides access to the {@link RuleBuilder} for the current {@link Rule}.
    */
   public RuleBuilder getRuleBuilder()
   {
      return rule;
   }

   @Override
   public List<Rule> getRules()
   {
      return wrapped.getRules();
   }

   @Override
   public List<RuleBuilder> getRuleBuilders()
   {
      return wrapped.getRuleBuilders();
   }

   @Override
   public ParameterStore getParameterStore()
   {
      return rule.getParameterStore();
   }

   @Override
   public String getId()
   {
      return rule.getId();
   }

   @Override
   public boolean evaluate(Rewrite event, EvaluationContext context)
   {
      return rule.evaluate(event, context);
   }

   @Override
   public void perform(Rewrite event, EvaluationContext context)
   {
      rule.perform(event, context);
   }

   @Override
   public Set<String> getRequiredParameterNames()
   {
      Set<String> result = new HashSet<String>();
      if (rule instanceof Parameterized)
         result.addAll(((Parameterized) rule).getRequiredParameterNames());
      return result;
   }

   @Override
   public void setParameterStore(ParameterStore store)
   {
      if (rule instanceof Parameterized)
         ((Parameterized) rule).setParameterStore(store);
   }

   @Override
   public ConfigurationRuleBuilderWithMetadata withMetadata(Object key, Object value)
   {
      rule.put(key, value);
      return this;
   }
}
