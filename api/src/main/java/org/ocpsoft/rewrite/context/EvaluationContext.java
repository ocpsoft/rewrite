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
package org.ocpsoft.rewrite.context;

import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.config.Rule;

/**
 * {@link Context} object spanning the lifecycle of a single {@link Rule} evaluation. This includes both
 * {@link Condition} evaluation and {@link Operation} invocation.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface EvaluationContext extends Context
{
   /**
    * Return the current {@link RewriteState} for this {@link EvaluationContext}. During {@link Condition} evaluation,
    * this will return {@link RewriteState#EVALUATING}. While performing configured {@link Operation} instances, this
    * will return {@link RewriteState#PERFORMING}.
    */
   RewriteState getState();

   /**
    * Add a new {@link Operation} to be performed if all conditions of this rule are met. Pre-Operation instances are
    * performed before the standard {@link Operation}.
    */
   void addPreOperation(Operation operation);

   /**
    * Add a new {@link Operation} to be performed if all conditions of this rule are met. Post-Operation instances are
    * performed after the standard {@link Operation}.
    */
   void addPostOperation(Operation operation);
}
