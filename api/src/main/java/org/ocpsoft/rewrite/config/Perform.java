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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.common.util.Strings;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * Evaluates all provided {@link Operation} instances. If all provided operations return <code>true</code>, this
 * operation returns <code>true</code>. If any provided operations return <code>false</code>, this operation returns
 * <code>false</code>.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public final class Perform extends DefaultOperationBuilder implements CompositeOperation
{
   private final Operation[] operations;

   private Perform(final Operation... operations)
   {
      this.operations = operations;
   }

   /**
    * Creates a new {@link Perform} operation. If all provided {@link Operation} instances return <code>true</code>,
    * this operation returns <code>true</code>. If any provided operations return <code>false</code>, this operation
    * returns <code>false</code>.
    * 
    * @param operations the array of operations to be evaluated
    */
   public static Perform all(final Operation... operations)
   {
      Assert.notNull(operations, "At least one operation is required.");
      Assert.assertTrue(operations.length > 0, "At least one operation is required.");
      return new Perform(flattenOperations(Arrays.asList(operations)).toArray(new Operation[] {}));
   }

   private static List<Operation> flattenOperations(List<Operation> operations)
   {
      List<Operation> result = new ArrayList<Operation>();
      for (Operation operation : operations) {
         if (operation instanceof Perform)
         {
            result.addAll(flattenOperations(((Perform) operation).getOperations()));
         }
         else
         {
            result.add(operation);
         }
      }
      return result;
   }

   @Override
   public void perform(Rewrite event, EvaluationContext context)
   {
      for (int i = 0; i < operations.length; i++) {
         operations[i].perform(event, context);
      }
   }

   @Override
   public List<Operation> getOperations()
   {
      return Arrays.asList(operations);
   }

   @Override
   public String toString()
   {
      return "Perform.all(" + Strings.join(getOperations(), ", ") + ")";
   }
}
