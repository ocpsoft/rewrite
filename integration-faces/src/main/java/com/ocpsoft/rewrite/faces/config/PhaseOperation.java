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

import javax.faces.event.PhaseId;

import com.ocpsoft.rewrite.config.Operation;
import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * This class encapsulates another operation that should be performed during the JSF lifecycle
 * 
 * @author <a href="mailto:fabmars@gmail.com">Fabien Marsaud</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class PhaseOperation implements HttpOperation
{
   private final Rewrite event;
   private final EvaluationContext context;
   private final Operation pendingOperation;
   private final PhaseId beforePhase;
   private final PhaseId afterPhase;
   private boolean consumed;

   public PhaseOperation(final Operation operation)
   {
      this.pendingOperation = operation;

      if (pendingOperation instanceof PhaseBinding) {
         PhaseBinding phaseBinding = (PhaseBinding) pendingOperation;
         this.beforePhase = phaseBinding.getBeforePhase();
         this.afterPhase = phaseBinding.getAfterPhase();
      }
      else
      {
         this.beforePhase = null;
         this.afterPhase = PhaseId.RESTORE_VIEW;
      }
   }

   public Operation getPendingOperation()
   {
      return pendingOperation;
   }

   public PhaseId getBeforePhase()
   {
      return beforePhase;
   }

   public PhaseId getAfterPhase()
   {
      return afterPhase;
   }

   public boolean isConsumed()
   {
      return consumed;
   }

   /**
    * Invoked during the rewrite process, just add "this" to a queue of deferred bindings in the request
    */
   @Override
   public void performHttp(HttpServletRewrite event, EvaluationContext context)
   {
      this.event = event;
      this.context = context;
      PhaseBinding.getDeferredOperations(this.event.getRequest()).add(this);
   }

   /**
    * Invokes the nested pending operation
    */
   public void performDeferredOperation()
   {
      consumed = true;
      pendingOperation.perform(event, context);
   }
}


