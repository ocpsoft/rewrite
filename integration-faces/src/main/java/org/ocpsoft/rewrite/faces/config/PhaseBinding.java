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
package org.ocpsoft.rewrite.faces.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.faces.event.PhaseId;

import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.Submission;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.Converter;
import org.ocpsoft.rewrite.param.Validator;
import org.ocpsoft.rewrite.servlet.config.HttpOperation;
import org.ocpsoft.rewrite.servlet.config.SendStatus;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.util.ValueHolderUtil;

/**
 * An {@link Operation} that wraps & holds a {@link Submission} until before or after a given JavaServer Faces
 * {@link PhaseId}. Validation and conversion of the given {@link Submission} are also deferred to within the Faces
 * lifecycle.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:fabmars@gmail.com">Fabien Marsaud</a>
 */
public abstract class PhaseBinding implements Binding
{
   private final Set<PhaseId> beforePhases = new HashSet<PhaseId>();
   private final Set<PhaseId> afterPhases = new HashSet<PhaseId>();

   private final Submission deferred;

   private Validator<?> validator;
   private Converter<?> converter;
   private Operation operation = SendStatus.error(404);

   private PhaseBinding(Submission binding)
   {
      this.deferred = binding;
   }

   /**
    * Create a {@link Binding} to process the given {@link Submission} during the Faces life-cycle.
    */
   public static PhaseBinding to(final Submission submission)
   {
      return new PhaseBinding(submission) {
         @Override
         public String toString()
         {
            return "PhaseBinding.to(" + submission + ")";
         }
      };
   }

   /**
    * Perform this {@link PhaseBinding} before the given phases (Except {@link PhaseId#RESTORE_VIEW}). The deferred
    * {@link Submission} will be performed once for each {@link PhaseId} provided.
    */
   public PhaseBinding before(final PhaseId... phases)
   {
      if (phases != null)
         this.beforePhases.addAll(Arrays.asList(phases));
      return this;
   }

   /**
    * Perform this {@link PhaseBinding} after the given phases (Except {@link PhaseId#RENDER_RESPONSE}). The deferred
    * {@link Submission} will be performed once for each {@link PhaseId} provided.
    */
   public PhaseBinding after(final PhaseId... phases)
   {
      if (phases != null)
         this.afterPhases.addAll(Arrays.asList(phases));
      return this;
   }

   /**
    * Validate the {@link Binding} value using the given {@link Validator}.
    */
   public PhaseBinding validatedBy(Validator<?> validator)
   {
      this.validator = validator;
      return this;
   }

   /**
    * Convert the {@link Binding} value using the given {@link Converter}.
    */
   public PhaseBinding convertedBy(Converter<?> converter)
   {
      this.converter = converter;
      return this;
   }

   /**
    * On validation failure, perform the given {@link Operation}; defaults to {@link SendStatus#code(int)} error code
    * 404 unless otherwise specified.
    */
   public PhaseBinding onValidationFailure(Operation operation)
   {
      this.operation = operation;
      return this;
   }

   @Override
   public Object retrieve(Rewrite event, EvaluationContext context)
   {
      throw new IllegalStateException("PhaseBinding does not support retrieval.");
   }

   @Override
   public Object submit(Rewrite event, EvaluationContext context, Object value)
   {

      /**
       * Must occur before queued {@link PhaseAction} instances during the same phase.
       */
      PhaseOperation.enqueue(new PhaseBindingOperation(value), -5)
               .before(beforePhases.toArray(new PhaseId[] {}))
               .after(afterPhases.toArray(new PhaseId[] {}))
               .perform(event, context);

      return null;
   }

   @Override
   public boolean supportsRetrieval()
   {
      return false;
   }

   @Override
   public boolean supportsSubmission()
   {
      return true;
   }

   private class PhaseBindingOperation extends HttpOperation
   {
      private Object value;

      public PhaseBindingOperation(Object value)
      {
         this.value = value;
      }

      @Override
      public void performHttp(HttpServletRewrite event, EvaluationContext context)
      {

         Object converted = value;

         if (converter != null)
            converted = ValueHolderUtil.convert(event, context, converter, value);

         if (validator != null && !ValueHolderUtil.validates(event, context, validator, converted))
         {
            if (operation != null)
               operation.perform(event, context);
         }
         else
         {
            deferred.submit(event, context, converted);
         }
      }

   }

}
