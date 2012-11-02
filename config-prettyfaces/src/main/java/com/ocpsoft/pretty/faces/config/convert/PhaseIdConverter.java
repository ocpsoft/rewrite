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

package com.ocpsoft.pretty.faces.config.convert;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;

import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;

public class PhaseIdConverter implements Converter
{
   @SuppressWarnings("rawtypes")
   public Object convert(final Class type, final Object value)
   {
      PhaseId result = null;
      if ("ANY_PHASE".equals(value))
      {
         result = PhaseId.ANY_PHASE;
      }
      else if ("APPLY_REQUEST_VALUES".equals(value))
      {
         result = PhaseId.APPLY_REQUEST_VALUES;
      }
      else if ("PROCESS_VALIDATIONS".equals(value))
      {
         result = PhaseId.PROCESS_VALIDATIONS;
      }
      else if ("UPDATE_MODEL_VALUES".equals(value))
      {
         result = PhaseId.UPDATE_MODEL_VALUES;
      }
      else if ("INVOKE_APPLICATION".equals(value))
      {
         result = PhaseId.INVOKE_APPLICATION;
      }
      else if ("RENDER_RESPONSE".equals(value))
      {
         result = PhaseId.RENDER_RESPONSE;
      }
      else
      {
         throw new ConversionException("Could not convert value: [" + value + "] to FacesPhaseId type.");
      }
      return result;
   }
}