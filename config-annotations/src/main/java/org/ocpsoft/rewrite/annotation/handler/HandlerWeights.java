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
package org.ocpsoft.rewrite.annotation.handler;

public class HandlerWeights
{

   /**
    * Suggested weight for handlers which build the basic structure of the rule. This is for example the case for
    * handlers that define the rule itself, add parameter bindings or operations.
    */
   public static final int WEIGHT_TYPE_STRUCTURAL = 100;

   /**
    * Suggested weight for handlers which enrich the structure built by other handlers. Typical examples are handles
    * that add conditions to the rule or that add validation or conversion to parameter bindings.
    */
   public static final int WEIGHT_TYPE_ENRICHING = 200;

}
