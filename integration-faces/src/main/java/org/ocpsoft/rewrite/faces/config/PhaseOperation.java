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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.faces.event.PhaseId;
import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.common.pattern.Weighted;
import org.ocpsoft.common.pattern.WeightedComparator;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.config.HttpOperation;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * An {@link Operation} that wraps another operation to be performed during the JavaServer Faces lifecycle.
 * 
 * @author <a href="mailto:fabmars@gmail.com">Fabien Marsaud</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class PhaseOperation<T extends PhaseOperation<T>> extends HttpOperation implements Weighted
{
   private static final String REQUEST_KEY = PhaseOperation.class.getName() + "_DEFERRED";

   private final Set<PhaseId> beforePhases = new HashSet<PhaseId>();
   private final Set<PhaseId> afterPhases = new HashSet<PhaseId>();

   /**
    * Perform the wrapped operation before or after the specified phases.
    */
   public abstract void performOperation(final HttpServletRewrite event, final EvaluationContext context);

   /**
    * Create an {@link Operation} that will defer a given {@link Operation} until one or more specified JavaServer Faces
    * {@link PhaseId} instances specified via {@link #before(PhaseId...)} or {@link #after(PhaseId...)}.
    */
   public static PhaseOperation<?> enqueue(final Operation operation)
   {
      return enqueue(operation, 0);
   }

   /**
    * Create an {@link Operation} that will enqueue a given {@link Operation} to be performed before or after one or
    * more JavaServer Faces {@link PhaseId} instances specified via {@link #before(PhaseId...)} or
    * {@link #after(PhaseId...)}.
    */
   @SuppressWarnings("rawtypes")
   public static PhaseOperation<?> enqueue(final Operation operation, final int priority)
   {
      return new PhaseOperation() {

         @Override
         public int priority()
         {
            return priority;
         }

         @Override
         public void performOperation(HttpServletRewrite event, EvaluationContext context)
         {
            operation.perform(event, context);
         }

         @Override
         public String toString()
         {
            return "PhaseOperation.enqueue(" + operation + ", " + priority + ")";
         }
      };
   }

   /**
    * Get the phases before which this {@link PhaseOperation} will be performed.
    */
   public Set<PhaseId> getBeforePhases()
   {
      return beforePhases;
   }

   /**
    * Get the phases after which this {@link PhaseOperation} will be performed.
    */
   public Set<PhaseId> getAfterPhases()
   {
      return afterPhases;
   }

   /**
    * Perform this {@link PhaseOperation} before the given phases (Except {@link PhaseId#RESTORE_VIEW}). The deferred
    * {@link Operation} will be performed once for each {@link PhaseId} provided.
    */
   @SuppressWarnings("unchecked")
   public T before(final PhaseId... phases)
   {
      if (phases != null)
         this.beforePhases.addAll(Arrays.asList(phases));
      return (T) this;
   }

   /**
    * Perform this {@link PhaseOperation} after the given phases (Except {@link PhaseId#RENDER_RESPONSE}). The deferred
    * {@link Operation} will be performed once for each {@link PhaseId} provided.
    */
   @SuppressWarnings("unchecked")
   public T after(final PhaseId... phases)
   {
      if (phases != null)
         this.afterPhases.addAll(Arrays.asList(phases));
      return (T) this;
   }

   @Override
   public final void performHttp(HttpServletRewrite event, EvaluationContext context)
   {
      getSortedPhaseOperations(event.getRequest()).add(new DeferredOperation(event, context, this));
   }

   @SuppressWarnings("unchecked")
   public static ArrayList<DeferredOperation> getSortedPhaseOperations(HttpServletRequest request)
   {
      ArrayList<DeferredOperation> operations = (ArrayList<DeferredOperation>) request.getAttribute(REQUEST_KEY);
      if (operations == null)
      {
         operations = new ArrayList<DeferredOperation>();
         request.setAttribute(REQUEST_KEY, operations);
      }

      Collections.sort(operations, new WeightedComparator());

      return operations;
   }

   public static class DeferredOperation implements Weighted
   {

      private final HttpServletRewrite event;
      private final EvaluationContext context;
      private final PhaseOperation<?> operation;

      public DeferredOperation(HttpServletRewrite event, EvaluationContext context, PhaseOperation<?> operation)
      {
         this.event = event;
         this.context = context;
         this.operation = operation;
      }

      @Override
      public int priority()
      {
         return operation.priority();
      }

      public HttpServletRewrite getEvent()
      {
         return event;
      }

      public EvaluationContext getContext()
      {
         return context;
      }

      public PhaseOperation<?> getOperation()
      {
         return operation;
      }

   }

}
