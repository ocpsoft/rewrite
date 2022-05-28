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

package com.ocpsoft.pretty.faces.util;

import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class PhaseIdComparator
{

   /**
    * @param phaseId
    * @param currentPhaseId
    * @return
    */
   public static boolean equals(PhaseId phaseId, jakarta.faces.event.PhaseId currentPhaseId)
   {
      switch (phaseId)
      {
      case ANY_PHASE:
         return jakarta.faces.event.PhaseId.ANY_PHASE.equals(currentPhaseId);
      case RESTORE_VIEW:
         return jakarta.faces.event.PhaseId.RESTORE_VIEW.equals(currentPhaseId);
      case APPLY_REQUEST_VALUES:
         return jakarta.faces.event.PhaseId.APPLY_REQUEST_VALUES.equals(currentPhaseId);
      case PROCESS_VALIDATIONS:
         return jakarta.faces.event.PhaseId.PROCESS_VALIDATIONS.equals(currentPhaseId);
      case UPDATE_MODEL_VALUES:
         return jakarta.faces.event.PhaseId.UPDATE_MODEL_VALUES.equals(currentPhaseId);
      case INVOKE_APPLICATION:
         return jakarta.faces.event.PhaseId.INVOKE_APPLICATION.equals(currentPhaseId);
      case RENDER_RESPONSE:
         return jakarta.faces.event.PhaseId.RENDER_RESPONSE.equals(currentPhaseId);
      }
      return false;
   }
}
