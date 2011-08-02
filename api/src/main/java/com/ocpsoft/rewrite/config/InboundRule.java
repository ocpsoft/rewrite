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


/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public abstract class InboundRule implements Rule
{
   private Condition condition;
   private Operation operation;

   public InboundRule setCondition(final Condition condition)
   {
      this.condition = condition;
      return this;
   }

   @Override
   public Condition getCondition()
   {
      return Direction.isInbound().and(condition);
   }

   public void setOperation(final InboundOperation operation)
   {
      this.operation = operation;
   }

   @Override
   public Operation getOperation()
   {
      return operation;
   }

}
