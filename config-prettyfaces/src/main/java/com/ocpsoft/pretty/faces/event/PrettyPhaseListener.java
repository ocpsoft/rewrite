/*
 * Copyright 2010 Lincoln Baxter, III
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
package com.ocpsoft.pretty.faces.event;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.beans.ActionExecutor;
import com.ocpsoft.pretty.faces.beans.ParameterInjector;
import com.ocpsoft.pretty.faces.beans.ParameterValidator;
import com.ocpsoft.pretty.faces.config.dynaview.DynaviewEngine;
import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;
import com.ocpsoft.pretty.faces.util.FacesMessagesUtils;

/**
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public class PrettyPhaseListener implements PhaseListener
{
   private static final long serialVersionUID = 2345410822999587673L;
   private final FacesMessagesUtils messagesUtils = new FacesMessagesUtils();
   private final ActionExecutor executor = new ActionExecutor();
   private final ParameterInjector injector = new ParameterInjector();
   private final ParameterValidator validator = new ParameterValidator();
   private final DynaviewEngine dynaview = new DynaviewEngine();

   public PhaseId getPhaseId()
   {
      return PhaseId.ANY_PHASE;
   }

   public void beforePhase(final PhaseEvent event)
   {
      FacesContext facesContext = event.getFacesContext();
      if (PhaseId.RESTORE_VIEW.equals(event.getPhaseId()))
      {
         PrettyContext prettyContext = PrettyContext.getCurrentInstance(facesContext);
         if (prettyContext.shouldProcessDynaview())
         {
            // We are only using this lifecycle to access the EL-Context.
            // End the faces lifecycle and finish processing after the Phase
            UIViewRoot viewRoot = facesContext.getViewRoot();
            if (viewRoot == null)
            {
               viewRoot = new UIViewRoot();
               viewRoot.setViewId("/com.ocpsoft.Dynaview.xhtml");
               facesContext.setViewRoot(viewRoot);
            }
            facesContext.responseComplete();
         }
      }
      else if (!facesContext.getResponseComplete())
      {
         FacesContext context = facesContext;
         messagesUtils.restoreMessages(context, context.getExternalContext().getRequestMap());
         processEvent(event);
      }
   }

   public void afterPhase(final PhaseEvent event)
   {
      if (PhaseId.RESTORE_VIEW.equals(event.getPhaseId()))
      {
         PrettyContext prettyContext = PrettyContext.getCurrentInstance(event.getFacesContext());
         boolean dynaviewViewDetermination = prettyContext.shouldProcessDynaview();

         /*
          * Validate and inject path/query parameter if one of these conditions is met:
          *   - The 'responseComplete' flag is not set (normal mapped request)
          *   - we must evaluate the view id for a dynaview request (responseComplete is set!!!)
          */
         if (!event.getFacesContext().getResponseComplete() || dynaviewViewDetermination)
         {

            // run the parameter validation before the injection
            validator.validateParameters(event.getFacesContext());

            // abort if validation failed (404 response code has already been set)
            if (event.getFacesContext().getResponseComplete() && !dynaviewViewDetermination)
            {
               return;
            }

            // validation was successful, now inject the parameters
            injector.injectParameters(event.getFacesContext());

         }

         // perform dynaview view determination
         if (dynaviewViewDetermination)
         {
            dynaview.processDynaView(prettyContext, event.getFacesContext());
         }
         
         // try to restore messages
         else if (!event.getFacesContext().getResponseComplete())
         {
            FacesContext context = event.getFacesContext();
            messagesUtils.restoreMessages(context, context.getExternalContext().getRequestMap());
            processEvent(event);
         }

      }
   }

   private void processEvent(final PhaseEvent event)
   {
      FacesContext context = event.getFacesContext();

      PrettyContext prettyContext = PrettyContext.getCurrentInstance(context);
      UrlMapping mapping = prettyContext.getCurrentMapping();
      if (mapping != null)
      {
         executor.executeActions(context, event.getPhaseId(), mapping);
      }
   }
}
