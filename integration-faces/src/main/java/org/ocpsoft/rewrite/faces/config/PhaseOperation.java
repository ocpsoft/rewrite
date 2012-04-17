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
 * This class encapsulates another operation that should be performed during the JSF lifecycle
 *
 * @author <a href="mailto:fabmars@gmail.com">Fabien Marsaud</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class PhaseOperation<T extends PhaseOperation<T>> extends HttpOperation implements Weighted
{
   private static final String REQUEST_KEY = PhaseOperation.class.getName() + "_DEFERRED";
   private HttpServletRewrite event;
   private EvaluationContext context;

   private final Set<PhaseId> beforePhases = new HashSet<PhaseId>();
   private final Set<PhaseId> afterPhases = new HashSet<PhaseId>();

   /**
    * Perform operation before or after the specified phases.
    */
   public abstract void performOperation(final HttpServletRewrite event, final EvaluationContext context);

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

   /**
    * Invoked during the rewrite process, just add "this" to a queue of deferred bindings in the request
    */
   @Override
   public final void performHttp(HttpServletRewrite event, EvaluationContext context)
   {
      this.event = event;
      this.context = context;
      getSortedPhaseOperations(event.getRequest()).add(this);
   }

   @SuppressWarnings("unchecked")
   public static ArrayList<PhaseOperation<?>> getSortedPhaseOperations(HttpServletRequest request)
   {
      ArrayList<PhaseOperation<?>> operations = (ArrayList<PhaseOperation<?>>) request.getAttribute(REQUEST_KEY);
      if (operations == null)
      {
         operations = new ArrayList<PhaseOperation<?>>();
         request.setAttribute(REQUEST_KEY, operations);
      }

      Collections.sort(operations, new WeightedComparator());

      return operations;
   }

   /**
    * Get the {@link HttpServletRewrite} with which this {@link PhaseOperation} was deferred.
    */
   public HttpServletRewrite getEvent()
   {
      return event;
   }

   /**
    * Get the {@link EvaluationContext} with which this {@link PhaseOperation} was deferred.
    */
   public EvaluationContext getContext()
   {
      return context;
   }

   /**
    * Defer the given {@link Operation} until the specified phases.
    */
   public static PhaseOperation<?> enqueue(final Operation operation)
   {
      return enqueue(operation, 0);
   }

   /**
    * Enqueue an {@link org.ocpsoft.rewrite.config.Operation} to be performed before or after one or many JavaServer
    * Faces life-cycle phases, specified via invoking {@link #before(PhaseId...)} or {@link #after(PhaseId...)}.
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
      };
   }
}
