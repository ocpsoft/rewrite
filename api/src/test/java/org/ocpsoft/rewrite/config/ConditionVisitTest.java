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

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.ocpsoft.rewrite.util.Visitor;

public class ConditionVisitTest
{

   @Test
   public void testConditionVisit()
   {

      // setup condition tree
      Condition condition1 = new True();
      Condition condition2 = new True();
      Condition condition3 = new True();
      Condition root = And.all(condition1, Or.any(condition2, condition3));

      // set of visited conditions for verification
      final Set<Condition> visited = new HashSet<Condition>();

      // visit the tree
      new ConditionVisit(root).accept(new Visitor<Condition>() {
         @Override
         public void visit(Condition e)
         {
            visited.add(e);
         }
      });

      // all 5 conditions should have been visited (3x True, 1x And, 1x Or)
      assertEquals(5, visited.size());

   }

}
