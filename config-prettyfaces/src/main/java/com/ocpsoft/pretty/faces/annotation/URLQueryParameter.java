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
 * Defines a managed query parameter of the form
 * <code>http://site.com/url?key=somevalue</code>, where if the parameter
 * exists, the value will be injected into the annotated field. This also
 * handles JSF commandLink and AJAX &lt;f:param&gt; values.
 * </p>
 * <p>
 * If the class containing the field is annotated with a {@link URLMapping}
 * annotation, the query parameter will automatically be added to this mapping.
 * You can also add the parameter to a foreign mapping by referencing it with
 * the <code>mappingId</code> attribute.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
@Documented
public @interface URLQueryParameter {

   /**
    * This is the request value key
    */
   String value();
   
   /**
    * <p>
    * Optional boolean (default true), if set to <code>false</code>, 
    * this query parameter will not be injected on form postbacks.
    * </p>
    */
   boolean onPostback() default true;
   
   /**
    * <p>
    * Optional ID of the mapping to add this query parameter to. If no ID is
    * given, the parameter will be added to the mapping specified on the class
    * the annotated method belongs to.
    * </p>
    */
   String mappingId() default "";
 
}
