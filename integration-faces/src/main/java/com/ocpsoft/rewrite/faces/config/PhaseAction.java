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
import java.util.List;

import javax.faces.event.PhaseId;
import javax.servlet.http.HttpServletRequest;

import com.ocpsoft.rewrite.bind.Retrieval;
import com.ocpsoft.rewrite.bind.Submission;
import com.ocpsoft.rewrite.config.Invoke;
import com.ocpsoft.rewrite.config.OperationBuilder;
import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.logging.Logger;
import com.ocpsoft.rewrite.services.ServiceLoader;
import com.ocpsoft.rewrite.servlet.config.HttpOperation;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import com.ocpsoft.rewrite.spi.InvocationResultHandler;

/**
 * Invoke an action before or after a given JavaServer Faces {@link PhaseId}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class PhaseAction extends HttpOperation
{
   private static final String QUEUED_ACTIONS = PhaseAction.class + "_QUEUED";
   private static final Logger log = Logger.getLogger(Invoke.class);
   private final Submission submission;
   private final Retrieval retrieval;
   private List<PhaseId> beforePhases = new ArrayList<PhaseId>();
   private List<PhaseId> afterPhases = new ArrayList<PhaseId>();

   private PhaseAction(final Submission submission, final Retrieval retrieval)
   {
      this.submission = submission;
      this.retrieval = retrieval;
   }

   @Override
   public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      HttpServletRequest request = event.getRequest();
      List<QueuedPhaseAction> actions = getQueuedPhaseActions(request);

      if (actions == null)
      {
         actions = new ArrayList<QueuedPhaseAction>();
         request.setAttribute(QUEUED_ACTIONS, actions);
      }

      actions.add(new QueuedPhaseAction(event, context, this));
   }

   @SuppressWarnings("unchecked")
   public static List<QueuedPhaseAction> getQueuedPhaseActions(final HttpServletRequest request)
   {
      List<QueuedPhaseAction> actions = (List<QueuedPhaseAction>) request.getAttribute(QUEUED_ACTIONS);
      return actions;
   }

   @SuppressWarnings("unchecked")
   public void invokeAction(final HttpServletRewrite event, final EvaluationContext context)
   {
      Object result = null;
      if ((submission == null) && (retrieval != null))
      {
         result = retrieval.retrieve(event, context);
         log.debug("Invoked binding [" + retrieval + "] returned value [" + result + "]");
      }
      else if (retrieval != null)
      {
         Object converted = submission.convert(event, context, retrieval.retrieve(event, context));
         result = submission.submit(event, context, converted);
         log.debug("Invoked binding [" + submission + "] returned value [" + result + "]");
      }
      else
      {
         log.warn("No binding specified for Invocation.");
      }

      if (result != null)
      {
         ServiceLoader<InvocationResultHandler> providers = ServiceLoader.load(InvocationResultHandler.class);
         if (!providers.iterator().hasNext())
         {
            log.debug("No instances of [" + InvocationResultHandler.class.getName()
                     + "] were registered to handing binding invocation result [" + result + "]");
         }

         for (InvocationResultHandler handler : providers) {
            handler.handle(event, context, result);
         }
      }
   }

   public List<PhaseId> getBeforePhases()
   {
      return beforePhases;
   }

   public List<PhaseId> getAfterPhases()
   {
      return afterPhases;
   }

   /**
    * Invoke the given {@link Retrieval} and process {@link InvocationResultHandler} instances on the result value (if
    * any.)
    * <p>
    * By default, this action is invoked after {@link PhaseId#RESTORE_VIEW}
    */
   public static PhaseAction retrieveFrom(final Retrieval retrieval)
   {
      return (PhaseAction) new PhaseAction(null, retrieval).after(PhaseId.RESTORE_VIEW);
   }

   /**
    * Invoke {@link Submission#submit(Rewrite, EvaluationContext, Object)}, use the result of the given
    * {@link Retrieval#retrieve(Rewrite, EvaluationContext)} as the value for this submission. Process
    * {@link InvocationResultHandler} instances on the result value (if any.)
    * <p>
    * By default, this action is invoked after {@link PhaseId#RESTORE_VIEW}
    */
   public static PhaseAction submitTo(final Submission to, final Retrieval from)
   {
      return (PhaseAction) new PhaseAction(to, from).after(PhaseId.RESTORE_VIEW);
   }

   public OperationBuilder before(final PhaseId... phases)
   {
      if (phases == null)
         this.beforePhases = new ArrayList<PhaseId>();
      else
         this.beforePhases = Arrays.asList(phases);

      return this;
   }

   public OperationBuilder after(final PhaseId... phases)
   {
      if (phases == null)
         this.afterPhases = new ArrayList<PhaseId>();
      else
         this.afterPhases = Arrays.asList(phases);

      return this;
   }

}
