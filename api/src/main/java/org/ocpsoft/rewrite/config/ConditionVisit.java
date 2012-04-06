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

/**
 * This class allows {@link Visitor}s to walk through a {@link Condition} tree which may contain
 * {@link CompositeCondition} elements.
 * 
 * @author Christian Kaltepoth
 */
public class ConditionVisit
{

   private final Condition root;

   /**
    * Initialize class with the supplied root {@link Condition}
    */
   public ConditionVisit(Condition root)
   {
      this.root = root;
   }

   /**
    * Submit the supplied visitor and apply it to all conditions in the tree
    */
   public void accept(Visitor<Condition> visitor)
   {
      visit(root, visitor);
   }

   /**
    * Method to call the visitor for the supplied condition and recursively calls itself, if the condition is a
    * {@link CompositeCondition}.
    */
   private void visit(Condition condition, Visitor<Condition> visitor)
   {

      // visit the condition itself
      visitor.visit(condition);

      // recursive call for all children if a CompositeCondition
      if (condition instanceof CompositeCondition) {
         for (Condition child : ((CompositeCondition) condition).getConditions()) {
            visit(child, visitor);
         }
      }
   }

}
