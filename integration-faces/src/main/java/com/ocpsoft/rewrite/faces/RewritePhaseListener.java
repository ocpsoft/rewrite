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

import java.util.ArrayList;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;

import com.ocpsoft.logging.Logger;
import com.ocpsoft.rewrite.faces.config.PhaseAction;
import com.ocpsoft.rewrite.faces.config.PhaseOperation;

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
      if (!PhaseId.RENDER_RESPONSE.equals(event.getPhaseId()))
      {
         handleAfterPhaseOperations(event);
         handleNavigation(event);
      }
   }

   @Override
   public void beforePhase(final PhaseEvent event)
   {
      if (!PhaseId.RESTORE_VIEW.equals(event.getPhaseId()))
      {
         handleBeforePhaseOperations(event);
      }

      if (PhaseId.RENDER_RESPONSE.equals(event.getPhaseId()))
         handleNavigation(event);
   }

   private void handleBeforePhaseOperations(final PhaseEvent event)
   {
      FacesContext facesContext = event.getFacesContext();
      HttpServletRequest request = ((HttpServletRequest) facesContext.getExternalContext().getRequest());

      ArrayList<PhaseOperation<?>> operations = PhaseOperation.getSortedPhaseOperations(request);
      if (operations != null)
      {
         for (PhaseOperation<?> operation : operations) {
            if (operation.getBeforePhases().contains(event.getPhaseId())
                     || operation.getBeforePhases().contains(PhaseId.ANY_PHASE))
            {
               operation.performOperation(operation.getEvent(), operation.getContext());
            }
         }
      }
   }

   private void handleAfterPhaseOperations(final PhaseEvent event)
   {
      FacesContext facesContext = event.getFacesContext();
      HttpServletRequest request = ((HttpServletRequest) facesContext.getExternalContext().getRequest());

      ArrayList<PhaseOperation<?>> operations = PhaseOperation.getSortedPhaseOperations(request);
      if (operations != null)
      {
         for (PhaseOperation<?> operation : operations) {
            if (operation.getAfterPhases().contains(event.getPhaseId())
                     || operation.getAfterPhases().contains(PhaseId.ANY_PHASE))
            {
               operation.performOperation(operation.getEvent(), operation.getContext());
            }
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
