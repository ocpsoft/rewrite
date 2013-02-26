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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.event.PhaseId;

import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.Convertable;
import org.ocpsoft.rewrite.bind.Converter;
import org.ocpsoft.rewrite.bind.Submission;
import org.ocpsoft.rewrite.bind.Validatable;
import org.ocpsoft.rewrite.bind.Validator;
import org.ocpsoft.rewrite.config.CompositeOperation;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.servlet.config.HttpOperation;
import org.ocpsoft.rewrite.servlet.config.SendStatus;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.util.ValueHolderUtil;

/**
 * Wraps & holds a {@link Submission} until before or after a given JavaServer Faces {@link PhaseId}. This means that
 * validation and conversion are also deferred to within the Faces lifecycle.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:fabmars@gmail.com">Fabien Marsaud</a>
 */
public class PhaseBinding extends HttpOperation implements Binding, Convertable<PhaseBinding>,
         Validatable<PhaseBinding>, CompositeOperation
{
   private final Set<PhaseId> beforePhases = new HashSet<PhaseId>();
   private final Set<PhaseId> afterPhases = new HashSet<PhaseId>();

   private final Submission deferred;
   private Object deferredValue;
   private Parameter<?> deferredParameter;

   private Validator<?> validator;
   private Converter<?> converter;
   private Operation operation = SendStatus.error(404);

   private PhaseBinding(Submission binding)
   {
      this.deferred = binding;
   }

   /**
    * Process the given {@link Submission} during the Faces life-cycle.
    */
   public static PhaseBinding to(Submission submission)
   {
      return new PhaseBinding(submission);
   }

   @Override
   public void performHttp(HttpServletRewrite event, EvaluationContext context)
   {
      Object value = deferredValue;
      if (converter != null)
         value = ValueHolderUtil.convert(event, context, converter, value);

      if (validator != null && !ValueHolderUtil.validates(event, context, validator, value))
      {
         if (operation != null)
            operation.perform(event, context);
      }
      else
      {
         deferred.submit(event, context, deferredParameter, value);
      }
   }

   @Override
   public Object retrieve(Rewrite event, EvaluationContext context, Parameter<?> parameter)
   {
      throw new IllegalStateException("PhaseBinding does not support retrieval.");
   }

   @Override
   public Object submit(Rewrite event, EvaluationContext context, Parameter<?> parameter, Object value)
   {
      deferredParameter = parameter;
      deferredValue = value;

      /**
       * Must occur before queued {@link PhaseAction} instances during the same phase.
       */
      PhaseOperation.enqueue(this, -5)
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

   @Override
   public <X extends Validator<?>> PhaseBinding validatedBy(Class<X> type)
   {
      validator = ValueHolderUtil.resolveValidator(type);
      return this;
   }

   @Override
   public PhaseBinding validatedBy(Validator<?> validator)
   {
      this.validator = validator;
      return this;
   }

   @Override
   public <X extends Converter<?>> PhaseBinding convertedBy(Class<X> type)
   {
      converter = ValueHolderUtil.resolveConverter(type);
      return this;
   }

   @Override
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
   public List<Operation> getOperations()
   {
      ArrayList<Operation> operations = new ArrayList<Operation>();
      if (operation != null)
         operations.add(operation);
      return operations;
   }
}
