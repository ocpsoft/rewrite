/*
 * Copyright 2011 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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
package org.ocpsoft.rewrite.faces.annotation;

import jakarta.faces.event.PhaseId;

/**
 * Defines a type-safe handle to the {@link PhaseId} constant string values.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public enum Phase
{

   NONE(null),
   RESTORE_VIEW(jakarta.faces.event.PhaseId.RESTORE_VIEW),
   APPLY_REQUEST_VALUES(jakarta.faces.event.PhaseId.APPLY_REQUEST_VALUES),
   PROCESS_VALIDATIONS(jakarta.faces.event.PhaseId.PROCESS_VALIDATIONS),
   UPDATE_MODEL_VALUES(jakarta.faces.event.PhaseId.UPDATE_MODEL_VALUES),
   INVOKE_APPLICATION(jakarta.faces.event.PhaseId.INVOKE_APPLICATION),
   RENDER_RESPONSE(jakarta.faces.event.PhaseId.RENDER_RESPONSE);

   private final jakarta.faces.event.PhaseId phaseId;

   private Phase(jakarta.faces.event.PhaseId phaseId)
   {
      this.phaseId = phaseId;
   }

   public jakarta.faces.event.PhaseId getPhaseId()
   {
      return phaseId;
   }

}
