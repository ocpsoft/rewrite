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
 * Evaluates all provided conditions. If any return true, this condition returns true.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Or extends ConditionBuilder
{
   private final List<Condition> conditions;

   private Or(final Condition... conditions)
   {
      this.conditions = Arrays.asList(conditions);
   }

   /**
    * Creates a new {@link Or} condition. If any provided conditions return true, this condition returns true.
    * 
    * @param conditions the array of conditions to be evaluated
    */
   public static Or any(final Condition... conditions)
   {
      return new Or(conditions);
   }

   @Override
   public boolean evaluate(final Rewrite event, final EvaluationContext context)
   {
      boolean result = false;
      for (Condition c : conditions) {
         if (c.evaluate(event, context))
         {
            result = true;
         }
      }
      return result;
   }
}
