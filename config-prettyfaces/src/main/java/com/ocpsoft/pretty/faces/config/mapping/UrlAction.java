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
package com.ocpsoft.pretty.faces.config.mapping;

import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.el.ConstantExpression;
import com.ocpsoft.pretty.faces.el.PrettyExpression;

/**
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public class UrlAction
{
   private PrettyExpression action;
   private PhaseId phaseId = PhaseId.RESTORE_VIEW;
   private boolean onPostback = true;
   private boolean inheritable = false;

   /**
    * Create a new {@link UrlAction} with empty values
    */
   public UrlAction()
   {
   }

   /**
    * Creates a new {@link UrlAction} and creates a {@link ConstantExpression}
    * for the supplied EL method binding
    * 
    * @param action String representation of the EL action method
    */
   public UrlAction(final String action)
   {
      this.action = new ConstantExpression(action);
   }

   /**
    * Creates a new {@link UrlAction} and initialize it with the supplied
    * {@link PrettyExpression}
    * 
    * @param action The expression
    */
   public UrlAction(final PrettyExpression action)
   {
      this.action = action;
   }

   /**
    * Creates a new {@link UrlAction} and creates a {@link ConstantExpression}
    * for the supplied EL method binding
    * 
    * @param action String representation of the EL action method
    * @param phaseId Phase ID to set
    */
   public UrlAction(final String action, final PhaseId phaseId)
   {
      this.action = new ConstantExpression(action);
      this.phaseId = phaseId;
   }

   public PhaseId getPhaseId()
   {
      return phaseId;
   }

   public boolean onPostback()
   {
      return onPostback;
   }

   public void setOnPostback(final boolean onPostback)
   {
      this.onPostback = onPostback;
   }

   public void setPhaseId(final PhaseId phaseId)
   {
      this.phaseId = phaseId;
   }

   public PrettyExpression getAction()
   {
      return action;
   }

   public void setAction(final PrettyExpression action)
   {
      this.action = action;
   }

   /**
    * Extra setter method creating a {@link ConstantExpression}. Used only for
    * Digester only.
    * 
    * @param action String representation of the EL expression
    */
   public void setAction(final String action)
   {
      this.action = new ConstantExpression(action);
   }

   public boolean isInheritable()
   {
      return inheritable;
   }

   public void setInheritable(boolean inheritable)
   {
      this.inheritable = inheritable;
   }
   
   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + (action == null ? 0 : action.hashCode());
      result = prime * result + (phaseId == null ? 0 : phaseId.hashCode());
      return result;
   }

   @Override
   public boolean equals(final Object obj)
   {
      if (this == obj)
      {
         return true;
      }
      if (obj == null)
      {
         return false;
      }
      if (!(obj instanceof UrlAction))
      {
         return false;
      }
      UrlAction other = (UrlAction) obj;
      if (action == null)
      {
         if (other.action != null)
         {
            return false;
         }
      }
      else if (!action.equals(other.action))
      {
         return false;
      }
      if (phaseId == null)
      {
         if (other.phaseId != null)
         {
            return false;
         }
      }
      else if (!phaseId.equals(other.phaseId))
      {
         return false;
      }
      return true;
   }

   @Override
   public String toString()
   {
      return "UrlAction [action=" + action + ", onPostback=" + onPostback + ", phaseId=" + phaseId + ", inheritable=" + inheritable + "]";
   }

}
