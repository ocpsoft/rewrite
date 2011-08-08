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

import com.ocpsoft.rewrite.EvaluationContext;
import com.ocpsoft.rewrite.config.Condition;
import com.ocpsoft.rewrite.config.ConditionBuilder;
import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.servlet.config.parameters.impl.ConditionParameterBuilder;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface ParameterizedCondition
{

   ConditionParameterBuilder where(String param);

   ConditionParameterBuilder where(String param, String pattern);

   ConditionParameterBuilder where(String param, String pattern, ParameterBinding binding);

   ConditionParameterBuilder where(String param, ParameterBinding binding);

   ConditionBuilder and(Condition condition);

   ConditionBuilder andNot(Condition condition);

   ConditionBuilder or(Condition condition);

   ConditionBuilder orNot(Condition condition);

   boolean evaluate(Rewrite event, EvaluationContext context);

}
