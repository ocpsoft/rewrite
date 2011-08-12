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
package com.ocpsoft.rewrite.servlet.config.parameters;

import com.ocpsoft.rewrite.config.Condition;
import com.ocpsoft.rewrite.config.ConditionBuilder;

/**
 * Interface used to declare a given {@link Condition} as being capable of supporting parameterization.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface ParameterizedCondition<T extends ParameterizedCondition<T>> extends Parameterized<T>, Condition
{
   /**
    * Chain this {@link Condition} with another {@link Condition} to be performed in series; both conditions must
    * evaluate to true.
    */
   ConditionBuilder and(Condition condition);

   /**
    * Chain this {@link Condition} with another {@link Condition} to be performed in series; the chained
    * {@link Condition} must evaluate to false.
    */
   ConditionBuilder andNot(Condition condition);

   /**
    * Chain this {@link Condition} with another {@link Condition} to be performed in series; only one must evaluate to
    * true.
    */
   ConditionBuilder or(Condition condition);

   /**
    * Chain this {@link Condition} with another {@link Condition} to be performed in series; this {@link Condition} must
    * evaluate to true, or the given {@link Condition} must evaluate to false.
    */
   ConditionBuilder orNot(Condition condition);

}
