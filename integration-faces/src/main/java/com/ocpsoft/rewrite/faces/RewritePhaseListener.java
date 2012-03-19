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
package com.ocpsoft.rewrite.faces;

import java.util.List;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;

import com.ocpsoft.logging.Logger;
import com.ocpsoft.rewrite.faces.config.DeferredOperation;
import com.ocpsoft.rewrite.faces.config.PhaseAction;
import com.ocpsoft.rewrite.faces.config.PhaseBinding;
import com.ocpsoft.rewrite.faces.config.QueuedPhaseAction;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class RewritePhaseListener implements PhaseListener
{
   private static final long serialVersionUID = 6706075446314218089L;
   private static Logger log = Logger.getLogger(RewritePhaseListener.class);

   @Override
   public PhaseId getPhaseId()
   {
      return PhaseId.ANY_PHASE;
   }

   @Override
   public void afterPhase(final PhaseEvent event)
   {
      handleAfterPhaseActions(event);
      if (!PhaseId.RENDER_RESPONSE.equals(event.getPhaseId()))
      {
         handleAfterPhaseInjections(event);
         handleNavigation(event);
      }
      else
      {
         FacesContext facesContext = event.getFacesContext();
         HttpServletRequest request = ((HttpServletRequest) facesContext.getExternalContext().getRequest());
         PhaseBinding.removeDefferredOperations(request);
      }
   }

   @Override
   public void beforePhase(final PhaseEvent event)
   {
      handleBeforePhaseActions(event);
      if (!PhaseId.RESTORE_VIEW.equals(event.getPhaseId()))
         handleBeforePhaseInjections(event);
      if (PhaseId.RENDER_RESPONSE.equals(event.getPhaseId()))
         handleNavigation(event);
   }

   private void handleBeforePhaseActions(final PhaseEvent event)
   {
      FacesContext facesContext = event.getFacesContext();
      HttpServletRequest request = ((HttpServletRequest) facesContext.getExternalContext().getRequest());

      List<QueuedPhaseAction> actions = PhaseAction.getQueuedPhaseActions(request);
      if (actions != null)
         for (QueuedPhaseAction action : actions) {
            if (action.getBeforePhases().contains(event.getPhaseId())
                     || action.getBeforePhases().contains(PhaseId.ANY_PHASE))
            {
               action.perform();
            }
         }
   }

   private void handleAfterPhaseActions(final PhaseEvent event)
   {
      FacesContext facesContext = event.getFacesContext();
      HttpServletRequest request = ((HttpServletRequest) facesContext.getExternalContext().getRequest());

      List<QueuedPhaseAction> actions = PhaseAction.getQueuedPhaseActions(request);
      if (actions != null)
         for (QueuedPhaseAction action : actions) {
            if (action.getAfterPhases().contains(event.getPhaseId())
                     || action.getAfterPhases().contains(PhaseId.ANY_PHASE))
            {
               action.perform();
            }
         }
   }

   private void handleBeforePhaseInjections(final PhaseEvent event)
   {
      FacesContext facesContext = event.getFacesContext();
      HttpServletRequest request = ((HttpServletRequest) facesContext.getExternalContext().getRequest());

      List<DeferredOperation> operations = PhaseBinding.getDeferredOperations(request);
      if (operations != null)
         for (DeferredOperation operation : operations) {
            if (!operation.isConsumed()
                     && (event.getPhaseId().equals(operation.getBeforePhase())
                              || PhaseId.ANY_PHASE.equals(operation.getBeforePhase())))
            {
               operation.performDeferredOperation();
            }
         }
   }

   private void handleAfterPhaseInjections(final PhaseEvent event)
   {
      FacesContext facesContext = event.getFacesContext();
      HttpServletRequest request = ((HttpServletRequest) facesContext.getExternalContext().getRequest());

      List<DeferredOperation> operations = PhaseBinding.getDeferredOperations(request);
      if (operations != null)
         for (DeferredOperation operation : operations) {
            if (!operation.isConsumed()
                     && (event.getPhaseId().equals(operation.getAfterPhase())
                              || PhaseId.ANY_PHASE.equals(operation.getAfterPhase())))
            {
               operation.performDeferredOperation();
            }
         }
   }

   public void handleNavigation(final PhaseEvent event)
   {
      FacesContext facesContext = event.getFacesContext();
      HttpServletRequest request = ((HttpServletRequest) facesContext.getExternalContext().getRequest());
      String navigationCase = (String) request.getAttribute(NavigatingInvocationResultHandler.QUEUED_NAVIGATION);
      if (navigationCase != null)
      {
         request.setAttribute(NavigatingInvocationResultHandler.QUEUED_NAVIGATION, null);
         NavigationHandler navigationHandler = facesContext.getApplication().getNavigationHandler();
         log.debug("Passing queued " + PhaseAction.class.getName() + " result [" + navigationCase
                  + "] to NavigationHandler.handleNavigation()");
         navigationHandler.handleNavigation(facesContext, "", navigationCase);
      }
   }

}
