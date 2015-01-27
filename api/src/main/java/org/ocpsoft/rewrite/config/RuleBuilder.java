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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ocpsoft.common.pattern.Weighted;
import org.ocpsoft.rewrite.context.Context;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.exception.RewriteException;
import org.ocpsoft.rewrite.param.ConfigurableParameter;
import org.ocpsoft.rewrite.param.DefaultParameter;
import org.ocpsoft.rewrite.param.DefaultParameterStore;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.ParameterizedRule;
import org.ocpsoft.rewrite.util.Visitor;

/**
 * Builder for fluently defining new composite {@link Rule} instances.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public final class RuleBuilder implements ParameterizedRule, RelocatableRule, CompositeCondition, CompositeOperation,
         CompositeRule, Context
{
   private final ParameterStore store;

   private Integer priority = null;
   private String id = "";
   private Condition condition;
   private Operation operation;
   protected Map<Object, Object> contextMap = new HashMap<Object, Object>();
   private Rule wrapped;

   private RuleBuilder()
   {
      store = new DefaultParameterStore();
   }

   private RuleBuilder(Rule rule)
   {
      store = new DefaultParameterStore();
      withId(rule.getId());

      if (rule instanceof Weighted)
         withPriority(((Weighted) rule).priority());

      wrapped = rule;
   }

   /**
    * Returns a new {@link RuleBuilder} instance.
    */
   public static RuleBuilder define()
   {
      return new RuleBuilder();
   }

   /**
    * Returns a new {@link RuleBuilder} instance wrapping the given {@link Rule}.
    */
   public static RuleBuilder wrap(final Rule rule)
   {
      return new RuleBuilder(rule);
   }

   /**
    * Returns a new {@link RuleBuilder} instance, set with the given {@link Rule} ID.
    */
   public static RuleBuilder define(final String id)
   {
      return define().withId(id);
   }

   /**
    * Set the ID of this {@link Rule} instance.
    */
   public RuleBuilder withId(final String id)
   {
      this.id = id;
      return this;
   }

   /**
    * Set the priority of this {@link Rule} instance. If {@link #priority()} differs from the priority of the
    * {@link ConfigurationProvider} from which this rule was returned, then relocate this rule to its new priority
    * position in the compiled rule set.
    */
   public RuleBuilder withPriority(int priority)
   {
      this.priority = priority;
      return this;
   }

   /**
    * Set the {@link Condition} of this {@link Rule} instance.
    */
   public RuleBuilder when(final Condition condition)
   {
      if (this.condition == null)
         this.condition = condition;
      else if (condition instanceof ConditionBuilder)
         this.condition = ((ConditionBuilder) this.condition).and(condition);
      else
         this.condition = Conditions.wrap(this.condition).and(condition);
      return this;
   }

   /**
    * Perform the given {@link Operation} when the conditions set in this {@link Rule} are met.
    */
   public RuleBuilder perform(final Operation operation)
   {
      if (this.operation == null)
         this.operation = operation;
      else if (operation instanceof OperationBuilder)
         this.operation = ((OperationBuilder) this.operation).and(operation);
      else
         this.operation = Operations.wrap(this.operation).and(operation);
      return this;
   }

   @Override
   public boolean evaluate(final Rewrite event, final EvaluationContext context)
   {
      context.put(ParameterStore.class, store);

      if (wrapped != null && condition != null)
         return wrapped.evaluate(event, context) && condition.evaluate(event, context);
      else if (wrapped != null)
         return wrapped.evaluate(event, context);
      else if (condition != null)
         return condition.evaluate(event, context);

      return true;
   }

   @Override
   public void perform(final Rewrite event, final EvaluationContext context)
   {
      if (wrapped != null)
         wrapped.perform(event, context);

      if (operation != null)
         operation.perform(event, context);
   }

   @Override
   public String getId()
   {
      return id;
   }

   @Override
   public int priority()
   {
      return priority == null ? 0 : priority;
   }

   @Override
   public boolean isRelocated()
   {
      return priority != null;
   }

   /**
    * Return the underlying {@link ConditionBuilder}
    */
   public DefaultConditionBuilder getConditionBuilder()
   {
      if (condition == null)
         condition = Conditions.create();
      else if (!(condition instanceof DefaultConditionBuilder))
         condition = Conditions.wrap(condition);

      return (DefaultConditionBuilder) condition;
   }

   /**
    * Return the underlying {@link OperationBuilder}
    */
   public DefaultOperationBuilder getOperationBuilder()
   {
      if (operation == null)
         operation = Operations.create();
      else if (!(operation instanceof DefaultOperationBuilder))
         operation = Operations.wrap(operation);

      return (DefaultOperationBuilder) operation;
   }

   /**
    * This method will call the supplied visitor for all conditions attached to the rule builder.
    * 
    * @param visitor visitor to process
    */
   public void accept(Visitor<Condition> visitor)
   {
      new ConditionVisit(condition).accept(visitor);
   }

   @Override
   public List<Operation> getOperations()
   {
      if (wrapped != null && operation != null)
         return Arrays.asList(wrapped, operation);

      else if (wrapped != null)
         return Arrays.asList((Operation) wrapped);

      else if (operation != null)
         return Arrays.asList(operation);

      return Collections.emptyList();
   }

   @Override
   public List<Condition> getConditions()
   {
      if (wrapped != null && condition != null)
         return Arrays.asList(wrapped, condition);

      else if (wrapped != null)
         return Arrays.asList((Condition) wrapped);

      else if (condition != null)
         return Arrays.asList(condition);

      return Collections.emptyList();
   }

   @Override
   public List<Rule> getRules()
   {
      return Arrays.asList(wrapped);
   }

   @Override
   public ParameterStore getParameterStore()
   {
      return store;
   }

   public ConfigurableParameter<?> where(String name)
   {
      assertParameterExists(name);
      Parameter<?> result = getParameterStore().get(name, new DefaultParameter(name));
      if (result instanceof ConfigurableParameter)
         return (ConfigurableParameter<?>) result;
      throw new RewriteException("Cannot configure read-only parameter [" + name + "].");
   }

   private void assertParameterExists(String name)
   {
      final Set<String> parameterNames = new LinkedHashSet<String>();

      ParameterizedCallback callback = new ParameterizedCallback()
      {
         @Override
         public void call(Parameterized parameterized)
         {
            Set<String> names = parameterized.getRequiredParameterNames();
            parameterNames.addAll(names);
         }
      };

      Visitor<Condition> conditionVisitor = new ParameterizedConditionVisitor(callback);
      new ConditionVisit(this).accept(conditionVisitor);

      Visitor<Operation> operationVisitor = new ParameterizedOperationVisitor(callback);
      new OperationVisit(this).accept(operationVisitor);

      if (!parameterNames.contains(name))
         throw new IllegalArgumentException("Parameter [" + name + "] does not exist in rule [" + this
                  + "] and cannot be configured.");
   }

   @Override
   public void clear()
   {
      if (wrapped instanceof Context)
      {
         ((Context) wrapped).clear();;
      }
      contextMap.clear();;
   }

   @Override
   public Object get(Object key)
   {
      if (wrapped instanceof Context)
      {
         return ((Context) wrapped).get(key);
      }
      return contextMap.get(key);
   }

   @Override
   public void put(Object key, Object value)
   {
      if (wrapped instanceof Context)
      {
         ((Context) wrapped).put(key, value);
      }
      contextMap.put(key, value);
   }

   @Override
   public boolean containsKey(Object key)
   {
      if (wrapped instanceof Context)
      {
         return ((Context) wrapped).containsKey(key);
      }
      return contextMap.containsKey(key);
   }

   @Override
   public Set<String> getRequiredParameterNames()
   {
      Set<String> result = new HashSet<String>();
      if (condition instanceof Parameterized)
      {
         Set<String> names = ((Parameterized) condition).getRequiredParameterNames();
         if (names != null)
            result.addAll(names);
      }
      if (operation instanceof Parameterized)
      {
         Set<String> names = ((Parameterized) operation).getRequiredParameterNames();
         if (names != null)
            result.addAll(names);
      }
      if (wrapped instanceof Parameterized)
      {
         Set<String> names = ((Parameterized) wrapped).getRequiredParameterNames();
         if (names != null)
            result.addAll(names);
      }
      return result;
   }

   @Override
   public void setParameterStore(ParameterStore store)
   {
      if (condition instanceof Parameterized)
         ((Parameterized) condition).setParameterStore(store);
      if (operation instanceof Parameterized)
         ((Parameterized) operation).setParameterStore(store);
      if (wrapped instanceof Parameterized)
         ((Parameterized) wrapped).setParameterStore(store);
   }

   @Override
   public String toString()
   {
      String result = ".addRule(";

      if (wrapped != null && !(wrapped instanceof RuleBuilder))
      {
         result += wrapped + ")";
      }
      else
      {
         result += ")";

         if (condition instanceof RuleBuilder)
         {
            String conditionToString = ((RuleBuilder) condition).conditionToString();
            if (!conditionToString.isEmpty())
               result += ".when(" + conditionToString + ")";
         }
         else if (condition != null)
            result += ".when(" + condition + ")";

         if (operation instanceof RuleBuilder)
         {
            String operationToString = ((RuleBuilder) operation).operationToString();
            if (!operationToString.isEmpty())
               result += ".perform(" + operationToString + ")";
         }
         else if (operation != null)
            result += ".perform(" + operation + ")";
      }

      if (getId() != null && !getId().isEmpty())
         result += ".withId(\"" + getId() + "\")";

      if (priority() != 0)
         result += ".withPriority(" + priority() + ")";

      return result;
   }

   protected String conditionToString()
   {
      if (condition instanceof RuleBuilder)
         return ((RuleBuilder) condition).conditionToString();

      return condition == null ? "" : condition.toString();
   }

   protected String operationToString()
   {
      if (operation instanceof RuleBuilder)
         return ((RuleBuilder) operation).conditionToString();

      return operation == null ? "" : operation.toString();
   }
}
