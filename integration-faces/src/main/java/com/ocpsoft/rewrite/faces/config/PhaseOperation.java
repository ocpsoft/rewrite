/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.ocpsoft.rewrite.faces.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.faces.event.PhaseId;
import javax.servlet.http.HttpServletRequest;

import com.ocpsoft.common.pattern.Weighted;
import com.ocpsoft.common.pattern.WeightedComparator;
import com.ocpsoft.rewrite.config.Operation;
import com.ocpsoft.rewrite.config.OperationBuilder;
import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.servlet.config.HttpOperation;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

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

   private Set<PhaseId> beforePhases = new HashSet<PhaseId>();
   private Set<PhaseId> afterPhases = new HashSet<PhaseId>();

   public abstract void performOperation(final HttpServletRewrite event, final EvaluationContext context);

   public Set<PhaseId> getBeforePhases()
   {
      return beforePhases;
   }

   public Set<PhaseId> getAfterPhases()
   {
      return afterPhases;
   }

   public OperationBuilder before(final PhaseId... phases)
   {
      if (phases != null)
         this.beforePhases.addAll(Arrays.asList(phases));
      return this;
   }

   public OperationBuilder after(final PhaseId... phases)
   {
      if (phases != null)
         this.afterPhases.addAll(Arrays.asList(phases));
      return this;
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

   public HttpServletRewrite getEvent()
   {
      return event;
   }

   public EvaluationContext getContext()
   {
      return context;
   }

   public static PhaseOperation<?> enqueue(final Operation operation)
   {
      return enqueue(operation, 0);
   }

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
