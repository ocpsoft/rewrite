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

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import com.ocpsoft.pretty.faces.util.FacesMessagesUtils;

/**
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public class MultiPageMessagesSupport implements PhaseListener
{

   private static final long serialVersionUID = 1250469273857785274L;
   private final FacesMessagesUtils messagesUtils = new FacesMessagesUtils();

   public PhaseId getPhaseId()
   {
      return PhaseId.ANY_PHASE;
   }

   public void beforePhase(final PhaseEvent event)
   {
      FacesContext facesContext = event.getFacesContext();
      messagesUtils.saveMessages(facesContext, facesContext.getExternalContext().getSessionMap());

      if (PhaseId.RENDER_RESPONSE.equals(event.getPhaseId()))
      {
         /*
          * Check to see if we are "naturally" in the RENDER_RESPONSE phase. If
          * we have arrived here and the response is already complete, then the
          * page is not going to show up: don't display messages yet.
          */
         if (!facesContext.getResponseComplete())
         {
            messagesUtils.restoreMessages(facesContext, facesContext.getExternalContext().getSessionMap());
         }
      }
   }

   /*
    * Save messages into the session after every phase.
    */
   public void afterPhase(final PhaseEvent event)
   {
      if (!PhaseId.RENDER_RESPONSE.equals(event.getPhaseId()))
      {
         FacesContext facesContext = event.getFacesContext();
         messagesUtils.saveMessages(facesContext, facesContext.getExternalContext().getSessionMap());
      }
   }
}