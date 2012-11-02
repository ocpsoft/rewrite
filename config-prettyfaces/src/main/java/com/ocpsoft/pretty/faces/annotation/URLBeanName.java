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

import org.ocpsoft.rewrite.el.spi.BeanNameResolver;

/**
 * <p>
 * Annotation specifying the name that can be used to access instances of this
 * bean in EL expressions.
 * </p>
 * <p>
 * Please not that PrettyFaces is able to auto-detect the name in most
 * environments. You will only need to place this annotation on your beans, if
 * you use an uncommon way for declaring your beans or are running in a special
 * class loading environment that prevents reflection.
 * </p>
 * 
 * <p>
 * If your way of declaring beans is not directly supported by PrettyFaces, you
 * can also create your own bean name resolver. This way you won't need to place
 * the {@link URLBeanName} annotation on your beans and instead let the resolver
 * determine the bean name. To create your own resolver, you will have to:
 * </p>
 * 
 * <ul>
 * <li>Build your own implementation of the {@link BeanNameResolver} interface.</li>
 * <li>Create a file
 * <code>META-INF/services/com.ocpsoft.pretty.faces.el.BeanNameResolver</code>
 * and put the fully-qualified class name of your implementation class in there.
 * </li>
 * </ul>
 * 
 * @author Christian Kaltepoth
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface URLBeanName {

   /**
    * <p>
    * The name of the bean in the EL context.
    * </p>
    */
   String value();

}
