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

import java.util.List;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class RuleBuilder extends ConfigurationBuilder implements Rule
{
   private Condition condition;
   private Operation operation;

   private final ConfigurationBuilder config;

   public RuleBuilder(final ConfigurationBuilder config)
   {
      this.config = config;
   }

   public RuleBuilder(final ConfigurationBuilder config, final Rule rule)
   {
      this.config = config;
      setCondition(rule.getCondition());
      setOperation(rule.getOperation());
   }

   public RuleBuilder setCondition(final Condition condition)
   {
      this.condition = condition;
      return this;
   }

   public RuleBuilder setOperation(final Operation operation)
   {
      this.operation = operation;
      return this;
   }

   @Override
   public Condition getCondition()
   {
      return condition;
   }

   @Override
   public Operation getOperation()
   {
      return operation;
   }

   /**
    * Passthroughs to wrapped builder.
    */
   @Override
   public RuleBuilder addRule()
   {
      return config.addRule();
   }

   @Override
   public List<Rule> getRules()
   {
      return config.getRules();
   }

}
