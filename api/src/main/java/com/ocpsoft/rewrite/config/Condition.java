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
package com.ocpsoft.rewrite.config;

import com.ocpsoft.rewrite.EvaluationContext;
import com.ocpsoft.rewrite.event.Rewrite;

/**
 * A condition that must be met in order for evaluation to return true. You may create custom {@link Condition}
 * implementations. If creating custom implementations, you should likely extend {@link ConditionBuilder}, which adds
 * logical operators to any class extending it.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface Condition
{
   /**
    * Evaluate this condition against the given {@link Rewrite} event. If this condition does not apply to the given
    * event, it must return false. If the condition applies and is satisfied, return true.
    */
   boolean evaluate(Rewrite event, EvaluationContext context);
}
