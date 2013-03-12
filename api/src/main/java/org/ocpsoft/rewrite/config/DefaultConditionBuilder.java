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
 * Used as a base class to create fluent relationships between {@link Condition} objects; this class adds logical
 * operators to any class extending it.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class DefaultConditionBuilder implements ConditionBuilder
{

   @Override
   public ConditionBuilder and(final Condition condition)
   {
      if (condition == null)
         return this;
      return And.all(this, condition);
   }

   @Override
   public ConditionBuilder andNot(final Condition condition)
   {
      if (condition == null)
         return this;
      return And.all(this, Not.any(condition));
   }

   @Override
   public ConditionBuilder or(final Condition condition)
   {
      if (condition == null)
         return this;
      return Or.any(this, condition);
   }

   @Override
   public ConditionBuilder orNot(final Condition condition)
   {
      if (condition == null)
         return this;
      return Or.any(this, Not.any(condition));
   }
}
