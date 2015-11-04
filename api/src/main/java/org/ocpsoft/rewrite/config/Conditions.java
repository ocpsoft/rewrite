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
import java.util.List;

import org.ocpsoft.rewrite.config.DefaultConditionBuilder.DefaultConditionBuilderInternal;
import org.ocpsoft.rewrite.context.EvaluationContext;
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
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * Utility for creating and wrapping {@link Condition} instances.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public final class Conditions
{
   public static final String NEGATION_COUNT_KEY = Conditions.class.getCanonicalName() + "_NotCount";

   /**
    * Indicates whether the current {@link Condition} is being negated.
    */
   public static boolean isNegated(final EvaluationContext context)
   {
      return getNegationCount(context) % 2 == 1;
   }

   /**
    * Returns the number of "Not"s that have been evaluated in the current evaluation process.
    *
    * For example, when(Not.any(MyCondition)) would have a "NotCount" of "1" during the evaluation of "MyCondition".
    *
    * when(Not.any(Not.any(MyCondition))) would have a "NotCount" of "2" during the evaluation of "MyCondition".
    *
    * This is useful for conditions that may have side effects, as they will know whether or not their condition is being
    * negated.
    */
   public static int getNegationCount(final EvaluationContext context)
   {
      if (context == null)
         return 0;

      Integer count = (Integer)context.get(NEGATION_COUNT_KEY);
      return count == null ? 0 : count;
   }

   /**
    * This increments the number of negations, allowing us to determine (during evaluation) whether the results of a particular
    * {@link Condition} will be negated.
    */
   static void incrementNegationCount(final EvaluationContext context, int adjustment)
   {
      if (context == null)
         return;

      Integer count = Conditions.getNegationCount(context);
      count += adjustment;
      context.put(NEGATION_COUNT_KEY, count);
   }


   /**
    * Return a new {@link DefaultConditionBuilder} that evaluates to {@link True} when
    * {@link #evaluate(Rewrite, EvaluationContext)} is invoked.
    */
   public static ConditionBuilder create()
   {
      return new DefaultConditionBuilder() {
         @Override
         public boolean evaluate(Rewrite event, EvaluationContext context)
         {
            return true;
         }

         @Override
         public String toString()
         {
            return "";
         }
      };
   }

   /**
    * Wrap a given {@link Condition} as a new {@link DefaultConditionBuilder} that evaluates the the original
    * {@link Condition} when {@link #evaluate(Rewrite, EvaluationContext)} is invoked.
    */
   public static ConditionBuilder wrap(final Condition condition)
   {
      if (condition == null)
         return create();

      if (condition instanceof ConditionBuilder)
         return (ConditionBuilder) condition;

      return new DefaultConditionBuilderInternal(condition) {
         @Override
         public boolean evaluate(Rewrite event, EvaluationContext context)
         {
            return condition.evaluate(event, context);
         }

         @Override
         public String toString()
         {
            return condition.toString();
         }

         @Override
         public List<Condition> getConditions()
         {
            return Arrays.asList(condition);
         }
      };
   }
}
