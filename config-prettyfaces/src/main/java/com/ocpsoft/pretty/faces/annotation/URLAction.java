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
package com.ocpsoft.pretty.faces.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Specifies an action method to be called after URL parameters have been parsed
 * and assigned into beans.
 * </p>
 * <p>
 * If the class containing the action method is annotated with a
 * {@link URLMapping} annotation, the action will automatically be added to this
 * mapping. You can also add the action to a foreign mapping by referencing it
 * with the <code>mappingId</code> attribute.
 * </p>
 * <p>
 * You can use {@link URLActions} as a container if you want to add multiple
 * {@link URLAction} annotations to a single method.
 * </p>
 * 
 * @author Christian Kaltepoth
 * @see URLMapping
 * @see URLActions
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface URLAction
{

   /**
    * <p>
    * For use with {@link URLAction#phaseId()}
    * </p>
    */
   public enum PhaseId
   {
      ANY_PHASE,
      RESTORE_VIEW,
      APPLY_REQUEST_VALUES,
      PROCESS_VALIDATIONS,
      UPDATE_MODEL_VALUES,
      INVOKE_APPLICATION,
      RENDER_RESPONSE;
   }

   /**
    * <p>
    * Optional ID of the mapping to add this action to. If no ID is given, the
    * action will be added to the mapping specified on the class the annotated
    * method belongs to.
    * </p>
    */
   String mappingId() default "";

   /**
    * <p>
    * Optional boolean (default true), if set to false, this action method will
    * not occur on form postback.
    * </p>
    * 
    */
   boolean onPostback() default true;

   /**
    * <p>
    * {@link PhaseId} value (default after RESTORE_VIEW) if set to a valid JSF
    * PhaseId, the action will occur immediately before the specified Phase (or
    * immediately after RESTORE_VIEW).
    * </p>
    * <p>
    * Note however, that if the phase does not occur, neither will your action
    * method.
    * </p>
    * <p>
    * If ANY_PHASE is specified, the action method will fire on EVERY phase.
    * </p>
    */
   PhaseId phaseId() default PhaseId.RESTORE_VIEW;
   
   /**
    * Whether child mappings inherit this action or not.
    */
   boolean inheritable() default false;

}
