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

import org.ocpsoft.rewrite.util.Visitor;

/**
 * This class allows {@link Visitor} instances to walk through a {@link Rule} tree which may contain
 * {@link CompositeCondition} or {@link CompositeOperation} elements containing other rules.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class RuleVisit
{

   private final Rule root;

   /**
    * Initialize class with the supplied root {@link Rule}
    */
   public RuleVisit(Rule root)
   {
      this.root = root;
   }

   /**
    * Submit the supplied {@link Visitor} and apply it to all {@link Rule} instances in the tree
    */
   public void accept(Visitor<Rule> visitor)
   {
      visit(root, visitor);
   }

   /**
    * Method to call the {@link Visitor} for the supplied {@link Rule} and recursively calls itself, if the {@link Rule}
    * is a {@link CompositeRule}.
    */
   private void visit(Rule rule, Visitor<Rule> visitor)
   {
      visitor.visit(rule);

      if (rule instanceof CompositeRule) {
         if (((CompositeRule) rule).getRules() != null)
         {
            for (Rule child : ((CompositeRule) rule).getRules()) {
               visit(child, visitor);
            }
         }
      }
   }

}
