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
package org.ocpsoft.rewrite.config;

/**
 * An intermediate stage {@link Rule} configuration.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ConfigurationRuleBuilderCustom
{
   /**
    * Set the {@link Condition} of this {@link Rule} instance.
    */
   ConfigurationRuleBuilderWhen when(Condition condition);

   /**
    * Set the {@link Condition}s of this {@link Rule} instance. This creates an implicit {@link And} between all
    * provided {@link Condition} instances.
    */
   ConfigurationRuleBuilderWhen when(Condition condition, Condition... conditions);

   /**
    * Perform the given {@link Operation} when the conditions set in this {@link Rule} are met.
    */
   ConfigurationRuleBuilderPerform perform(Operation operation);

   /**
    * Perform the given {@link Operation} instances when the conditions set in this {@link Rule} are met.
    */
   ConfigurationRuleBuilderPerform perform(Operation operation, Operation... operations);
}
