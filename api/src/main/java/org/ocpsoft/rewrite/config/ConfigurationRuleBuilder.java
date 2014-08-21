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

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.common.util.Iterators;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.ParameterizedRule;
import org.ocpsoft.rewrite.spi.ConfigurationRuleBuilderInterceptor;

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

   private final List<ConfigurationRuleBuilderInterceptor> interceptors;

   @SuppressWarnings("unchecked")
   ConfigurationRuleBuilder(final ConfigurationBuilder config, final RuleBuilder rule)
   {
      this.interceptors = Iterators.asList(ServiceLoader.load(ConfigurationRuleBuilderInterceptor.class));
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

   @Override
   public ConfigurationRuleParameterBuilder where(String name)
   {
      return new ConfigurationRuleParameterBuilder(this, rule.where(name));
   }

   @Override
   public ConfigurationRuleBuilder when(Condition condition)
   {
      for (ConfigurationRuleBuilderInterceptor interceptor : interceptors) {
         condition = interceptor.when(condition);
      }

      rule.when(condition);
      return this;
   }

   @Override
   public ConfigurationRuleBuilderWhen when(Condition condition, Condition... conditions)
   {
      List<Condition> list = new LinkedList<Condition>();
      list.add(condition);
      list.addAll(Arrays.asList(conditions));

      for (ConfigurationRuleBuilderInterceptor interceptor : interceptors) {
         list = interceptor.when(list);
      }

      rule.when(And.all(list.toArray(new Condition[list.size()])));
      return this;
   }

   @Override
   public ConfigurationRuleBuilder perform(Operation operation)
   {
      for (ConfigurationRuleBuilderInterceptor interceptor : interceptors) {
         operation = interceptor.perform(operation);
      }

      rule.perform(operation);
      return this;
   }

   @Override
   public ConfigurationRuleBuilderPerform perform(Operation operation, Operation... operations)
   {
      List<Operation> list = new LinkedList<Operation>();
      list.add(operation);
      list.addAll(Arrays.asList(operations));

      for (ConfigurationRuleBuilderInterceptor interceptor : interceptors) {
         list = interceptor.perform(list);
      }

      rule.perform(Perform.all(list.toArray(new Operation[list.size()])));
      return this;
   }

   @Override
   public ConfigurationRuleBuilder otherwise(Operation operation)
   {
      for (ConfigurationRuleBuilderInterceptor interceptor : interceptors) {
         operation = interceptor.otherwise(operation);
      }

      wrapped.addOtherwiseRule(rule)
               .when(Not.any(rule))
               .perform(operation);

      return this;
   }

   @Override
   public ConfigurationRuleBuilderOtherwise otherwise(Operation operation, Operation... operations)
   {
      List<Operation> list = new LinkedList<Operation>();
      list.add(operation);
      list.addAll(Arrays.asList(operations));

      for (ConfigurationRuleBuilderInterceptor interceptor : interceptors) {
         list = interceptor.otherwise(list);
      }

      wrapped.addOtherwiseRule(rule)
               .when(Not.any(rule))
               .perform(Perform.all(list.toArray(new Operation[list.size()])));

      return this;
   }

   @Override
   public ConfigurationRuleBuilder withPriority(int priority)
   {
      for (ConfigurationRuleBuilderInterceptor interceptor : interceptors) {
         priority = interceptor.withPriority(priority);
      }

      rule.withPriority(priority);
      return this;
   }

   @Override
   public ConfigurationRuleBuilder withId(String id)
   {
      for (ConfigurationRuleBuilderInterceptor interceptor : interceptors) {
         id = interceptor.withId(id);
      }

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
