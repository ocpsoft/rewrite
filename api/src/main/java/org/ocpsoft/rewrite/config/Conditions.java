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
public class Conditions
{
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
