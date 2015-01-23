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
package org.ocpsoft.rewrite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.context.ContextBase;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.context.RewriteState;
import org.ocpsoft.rewrite.param.DefaultParameterStore;
import org.ocpsoft.rewrite.param.DefaultParameterValueStore;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.ParameterValueStore;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public class MockEvaluationContext extends ContextBase implements EvaluationContext
{
   private final List<Operation> preOperations = new ArrayList<Operation>();
   private final List<Operation> postOperations = new ArrayList<Operation>();

   public MockEvaluationContext()
   {
      put(ParameterStore.class, new DefaultParameterStore());
      put(ParameterValueStore.class, new DefaultParameterValueStore());
   }

   @Override
   public void addPreOperation(final Operation operation)
   {
      this.preOperations.add(operation);
   }

   @Override
   public void addPostOperation(final Operation operation)
   {
      this.postOperations.add(operation);
   }

   /**
    * Get an immutable view of the added pre-{@link Operation} instances.
    */
   public List<Operation> getPreOperations()
   {
      return Collections.unmodifiableList(preOperations);
   }

   /**
    * Get an immutable view of the added post-{@link Operation} instances.
    */
   public List<Operation> getPostOperations()
   {
      return Collections.unmodifiableList(postOperations);
   }

   @Override
   public RewriteState getState()
   {
      throw new IllegalStateException("not implemented");
   }
}
