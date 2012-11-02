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
package com.ocpsoft.pretty.faces.beans;

import java.util.List;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.PrettyException;
import com.ocpsoft.pretty.faces.config.mapping.UrlAction;
import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;
import com.ocpsoft.pretty.faces.util.FacesElUtils;
import com.ocpsoft.pretty.faces.util.FacesMessagesUtils;
import com.ocpsoft.pretty.faces.util.FacesStateUtils;
import com.ocpsoft.pretty.faces.util.PhaseIdComparator;

/**
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public class ActionExecutor
{
   private static final Log log = LogFactory.getLog(ActionExecutor.class);
   private static final FacesElUtils elUtils = new FacesElUtils();
   private final FacesMessagesUtils mu = new FacesMessagesUtils();

   public void executeActions(final FacesContext context, final PhaseId currentPhaseId, final UrlMapping mapping)
   {
      List<UrlAction> actions = mapping.getActions();
      for (UrlAction action : actions)
      {
         if (shouldExecute(action, currentPhaseId, FacesStateUtils.isPostback(context)))
         {
            try
            {
               PrettyContext prettyContext = PrettyContext.getCurrentInstance(context);
               log.trace("Invoking method: " + action + ", on request: " + prettyContext.getRequestURL());
               Object result = elUtils.invokeMethod(context, action.getAction().getELExpression());
               if (result != null)
               {
                  mu.saveMessages(context, context.getExternalContext().getSessionMap());
                  String outcome = result.toString();
                  if (!"".equals(outcome))
                  {
                     NavigationHandler handler = context.getApplication().getNavigationHandler();
                     handler.handleNavigation(context, prettyContext.getCurrentViewId(), outcome);
                  }
                  return;
               }
            }
            catch (Exception e)
            {
               throw new PrettyException("Exception occurred while processing <" + mapping.getId() + ":"
                        + action.getAction() + "> " + e.getMessage(), e);
            }
         }
      }
   }

   boolean shouldExecute(final UrlAction action, final PhaseId currentPhaseId, final boolean isPostback)
   {
      boolean result = false;
      if (PhaseIdComparator.equals(action.getPhaseId(), currentPhaseId)
               || PhaseIdComparator.equals(action.getPhaseId(), PhaseId.ANY_PHASE))
      {
         if (action.onPostback())
         {
            result = true;
         }
         else
         {
            if (!isPostback)
            {
               result = true;
            }
         }
      }
      return result;
   }
}
