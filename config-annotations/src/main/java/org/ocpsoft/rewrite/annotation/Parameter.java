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
package org.ocpsoft.rewrite.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.ocpsoft.rewrite.bind.Binding;

/**
 * Registers a {@link Binding} for a parameter. This annotation can be used to bind a parameter to a bean property.
 * 
 * <pre>
 * {@literal @}Join(path = "/user/{id}", to = "/user-details.html")
 * public class MyClass {
 * 
 *   {@literal @}Parameter
 *   private String id;
 * 
 * }
 * </pre>
 * 
 * Please note that you have to specify the parameter name manually if it differs from the field name.
 * 
 * <pre>
 * {@literal @}Join(path = "/user/{id}", to = "/user-details.html")
 * public class MyClass {
 * 
 *   {@literal @}Parameter("id")
 *   private String userId;
 * 
 * }
 * </pre>
 * 
 * @author Christian Kaltepoth
 */
@Inherited
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Parameter
{

   /**
    * The name of the parameter. If the attribute is not set, the name of the annotated field is used.
    */
   String value() default "";

}
