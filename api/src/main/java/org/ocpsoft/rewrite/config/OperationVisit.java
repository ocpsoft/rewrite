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
 * This class allows {@link Visitor}s to walk through an {@link Operation} tree which may contain
 * {@link CompositeOperation} elements.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class OperationVisit
{
   private final Operation root;

   /**
    * Initialize class with the supplied root {@link Operation}
    */
   public OperationVisit(Operation root)
   {
      this.root = root;
   }

   /**
    * Submit the supplied visitor and apply it to all {@link Operation} instances in the tree
    */
   public void accept(Visitor<Operation> visitor)
   {
      visit(root, visitor);
   }

   /**
    * Method to call the visitor for the supplied {@link Operation} and recursively calls itself, if the
    * {@link Operation} is a {@link CompositeOperation}.
    */
   private void visit(Operation operation, Visitor<Operation> visitor)
   {
      visitor.visit(operation);

      if (operation instanceof CompositeOperation) {
         if (((CompositeOperation) operation).getOperations() != null) {
            for (Operation child : ((CompositeOperation) operation).getOperations()) {
               visit(child, visitor);
            }
         }
      }
   }

}
