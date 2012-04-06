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

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * Builder for fluently defining new composite {@link Rule} instances.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class RuleBuilder implements RelocatableRule
{
   private Integer priority = null;
   private String id = "";
   private Condition condition = new True();
   private Operation operation;

   protected RuleBuilder()
   {}

   /**
    * Returns a new {@link RuleBuilder} instance.
    */
   public static RuleBuilder define()
   {
      return new RuleBuilder();
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
      this.condition = condition;
      return this;
   }

   /**
    * Perform the given {@link Operation} when this {@link Rule} is executed.
    */
   public RuleBuilder perform(final Operation operation)
   {
      this.operation = operation;
      return this;
   }

   @Override
   public boolean evaluate(final Rewrite event, final EvaluationContext context)
   {
      return condition.evaluate(event, context);
   }

   @Override
   public void perform(final Rewrite event, final EvaluationContext context)
   {
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
         condition = DefaultConditionBuilder.create();
      else if (!(condition instanceof DefaultConditionBuilder))
         condition = DefaultConditionBuilder.wrap(condition);

      return (DefaultConditionBuilder) condition;
   }

   /**
    * Return the underlying {@link OperationBuilder}
    * 
    * @return
    */
   public DefaultOperationBuilder getOperationBuilder()
   {
      if (operation == null)
         operation = DefaultOperationBuilder.create();
      else if (!(operation instanceof DefaultOperationBuilder))
         operation = DefaultOperationBuilder.wrap(operation);

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

}
