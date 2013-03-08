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
package org.ocpsoft.rewrite.context;

import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * Represents the the current {@link Rewrite} state during {@link Rule} execution.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public enum RewriteState
{
   /**
    * The system is currently evaluating configured {@link Condition} instances.
    */
   EVALUATING,

   /**
    * The system is currently performing configured {@link Operation} instances.
    */
   PERFORMING;

   /**
    * Return <code>true</code> if this instance is equal to {@link RewriteState#EVALUATING}.
    */
   public boolean isEvaluating()
   {
      return EVALUATING.equals(this);
   }

   /**
    * Return <code>true</code> if this instance is equal to {@link RewriteState#PERFORMING}.
    */
   public boolean isPerforming()
   {
      return PERFORMING.equals(this);
   }
}
