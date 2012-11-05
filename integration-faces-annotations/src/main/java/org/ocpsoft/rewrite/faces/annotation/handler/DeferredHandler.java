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
package org.ocpsoft.rewrite.faces.annotation.handler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.faces.event.PhaseId;

import org.ocpsoft.rewrite.annotation.api.ClassContext;
import org.ocpsoft.rewrite.annotation.api.FieldContext;
import org.ocpsoft.rewrite.annotation.api.HandlerChain;
import org.ocpsoft.rewrite.annotation.api.MethodContext;
import org.ocpsoft.rewrite.annotation.handler.HandlerWeights;
import org.ocpsoft.rewrite.annotation.spi.AnnotationHandler;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.faces.annotation.Deferred;
import org.ocpsoft.rewrite.faces.annotation.Phase;
import org.ocpsoft.rewrite.faces.config.PhaseBinding;
import org.ocpsoft.rewrite.faces.config.PhaseOperation;

public class DeferredHandler implements AnnotationHandler<Deferred>
{

   @Override
   public Class<Deferred> handles()
   {
      return Deferred.class;
   }

   @Override
   public int priority()
   {
      /*
       * The handler needs to wrap the complete binding/operation so it gets deferred completely.
       */
      return HandlerWeights.WEIGHT_TYPE_ENRICHING - 10;
   }

   @Override
   public void process(ClassContext context, Deferred annotation, HandlerChain chain)
   {

      // first let subsequent handlers wrap or enrich the binding/operation
      chain.proceed();

      if (context instanceof FieldContext) {

         Field field = ((FieldContext) context).getJavaField();

         // locate the binding previously created by @ParameterBinding
         Binding binding = (Binding) context.get(Binding.class);
         if (binding != null) {

            PhaseBinding phaseBinding = PhaseBinding.to(binding);

            // configure the target phase
            if (annotation.before() == Phase.NONE && annotation.after() == Phase.NONE) {
               phaseBinding.after(PhaseId.RESTORE_VIEW);
            }
            else if (annotation.before() != Phase.NONE && annotation.after() == Phase.NONE) {
               phaseBinding.before(annotation.before().getPhaseId());
            }
            else if (annotation.before() == Phase.NONE && annotation.after() != Phase.NONE) {
               phaseBinding.after(annotation.after().getPhaseId());
            }
            else {
               throw new IllegalStateException("Error processing field " + field
                        + ": You cannot use after() and before() at the same time.");
            }

            // replace existing binding builder
            context.put(Binding.class, phaseBinding);

         }

      }

      if (context instanceof MethodContext) {

         Method method = ((MethodContext) context).getJavaMethod();

         // locate the operation previously created by @RequestAction
         Operation operation = (Operation) context.get(Operation.class);
         if (operation != null) {

            PhaseOperation<?> deferred = PhaseOperation.enqueue(operation, 10);

            // configure the target phase
            if (annotation.before() == Phase.NONE && annotation.after() == Phase.NONE) {
               deferred.after(PhaseId.RESTORE_VIEW);
            }
            else if (annotation.before() != Phase.NONE && annotation.after() == Phase.NONE) {
               deferred.before(annotation.before().getPhaseId());
            }
            else if (annotation.before() == Phase.NONE && annotation.after() != Phase.NONE) {
               deferred.after(annotation.after().getPhaseId());
            }
            else {
               throw new IllegalStateException("Error processing field " + method
                        + ": You cannot use after() and before() at the same time.");
            }

            // replace existing binding builder
            context.put(Operation.class, deferred);

         }

      }

   }

}
