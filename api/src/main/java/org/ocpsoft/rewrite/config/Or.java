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
import java.util.Arrays;
import java.util.List;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.common.util.Strings;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * Evaluates all provided {@link Condition} instances. If any conditions return <code>true</code>, this condition
 * returns <code>true</code>. If all provided conditions return <code>false</code>, this condition returns
 * <code>false</code>.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public final class Or extends DefaultConditionBuilder implements CompositeCondition
{
   private final Condition[] conditions;

   private Or(final Condition... conditions)
   {
      this.conditions = conditions;
   }

   /**
    * Creates a new {@link Or} condition. If any provided conditions return true, this condition returns true.
    * 
    * @param conditions the array of conditions to be evaluated
    */
   public static Or any(final Condition... conditions)
   {
      Assert.notNull(conditions, "At least one condition is required.");
      Assert.assertTrue(conditions.length > 0, "At least one condition is required.");
      return new Or(flattenConditions(Arrays.asList(conditions)).toArray(new Condition[] {}));
   }

   private static List<Condition> flattenConditions(List<Condition> conditions)
   {
      List<Condition> result = new ArrayList<Condition>();
      for (Condition condition : conditions) {
         if (condition instanceof Or)
         {
            result.addAll(flattenConditions(((Or) condition).getConditions()));
         }
         else
         {
            result.add(condition);
         }
      }
      return result;
   }

   @Override
   public boolean evaluate(final Rewrite event, final EvaluationContext context)
   {
      boolean result = false;
      for (int i = 0; i < conditions.length; i++) {
         if (conditions[i].evaluate(event, context))
         {
            result = true;
         }
      }
      return result;
   }

   @Override
   public List<Condition> getConditions()
   {
      return Arrays.asList(conditions);
   }

   @Override
   public String toString()
   {
      String result = Strings.join(Arrays.asList(conditions), ".or(");
      for (int i = 0; i < conditions.length; i++) {
         result += ")";
      }
      return result;
   }
}
