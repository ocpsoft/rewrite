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

import javax.faces.validator.Validator;

/**
 * <p>
 * Annotation representing a validation rule attached to an individual path
 * parameter or a query parameter.
 * </p>
 * 
 * @author Christian Kaltepoth
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.PARAMETER, ElementType.FIELD } )
@Inherited
@Documented
public @interface URLValidator {

   /**
    * <p>
    * A pattern validator for this mapping. Validators may be attached to
    * individual parameters in each dynamic URL. Only used when validating path
    * parameters.
    * </p>
    */
   int index() default -1;

   /**
    * <p>
    * The pretty:mappingId or #{bean.method} to evaluate, should validation
    * fail.
    * </p>
    */
   String onError() default "";

   /**
    * <p>
    * The IDs of the JSF Validator objects to attach and process before bean
    * value injection.
    * </p>
    */
   String[] validatorIds() default {};
   
   /**
    * <p>
    * EL method binding referring to a method performing the validation.
    * The referenced method must have the same signature as
    * {@link Validator#validate(javax.faces.context.FacesContext, javax.faces.component.UIComponent, Object)}.
    * </p>
    * 
    * @see Validator
    */
   String validator() default "";

}
