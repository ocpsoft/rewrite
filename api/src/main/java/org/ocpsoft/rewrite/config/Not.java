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

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * Evaluates all provided {@link Condition} instances. If all provided conditions return <code>true</code>, this
 * condition returns <code>false</code>. If any provided conditions return <code>false</code>, this condition returns
 * <code>true</code>.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public final class Not extends DefaultConditionBuilder implements CompositeCondition
{
   private final Condition condition;

   private Not(final Condition condition)
   {
      this.condition = condition;
   }

   /**
    * Evaluates all provided {@link Condition} instances. If all provided conditions return <code>true</code>, this
    * condition returns <code>false</code>. If any provided conditions return <code>false</code>, this condition returns
    * <code>true</code>.
    */
   public static Not any(final Condition condition)
   {
      return new Not(condition);
   }

   @Override
   public boolean evaluate(final Rewrite event, final EvaluationContext context)
   {
      Conditions.incrementNegationCount(context, 1);
      try
      {
         return condition.evaluate(event, context) != true;
      }
      finally
      {
         Conditions.incrementNegationCount(context, -1);
      }
   }

   @Override
   public List<Condition> getConditions()
   {
      return Arrays.asList(condition);
   }

   @Override
   public String toString()
   {
      return "Not.any(" + condition + ")";
   }


}
