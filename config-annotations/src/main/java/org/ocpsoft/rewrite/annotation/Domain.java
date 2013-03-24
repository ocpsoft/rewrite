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

import org.ocpsoft.rewrite.param.ParameterizedPattern;

/**
 * <p>
 * Adds a {@link org.ocpsoft.rewrite.servlet.config.Domain} condition to the current rule. This allows to restrict the
 * rule to a specific domain or to bind a substring of the domain to a bean property via the {@link Parameter}
 * annotation.
 * </p>
 * 
 * 
 * <pre>
 * {@literal @}Domain("{lang}.example.com")
 * {@literal @}Join(path = "/something", to = "/some-page.html")
 * public class MyClass {
 * 
 *   {@literal @}Parameter
 *   private String lang;
 * 
 * }
 * </pre>
 * 
 * @see {@link Parameter} {@link ParameterizedPattern}
 * 
 * @author Christian Kaltepoth
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Domain
{

   /**
    * The {@link ParameterizedPattern} used for matching the domain.
    */
   String value();
}
