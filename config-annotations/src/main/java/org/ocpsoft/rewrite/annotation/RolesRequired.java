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

import org.ocpsoft.rewrite.servlet.config.JAASRoles;

/**
 * <p>
 * Adds a {@link JAASRoles} condition to the current rule. This allows to restrict the rule to users with the given
 * role. Rewrite uses {@link HttpServletRequest#isUserInRole(String)} for checking the roles.
 * </p>
 * 
 * <pre>
 * {@literal @}Join(path = "/admin/users", to = "/admin/user-list.html")
 * {@literal @}RolesRequired("admin")
 * public class MyClass {
 * 
 * }
 * </pre>
 * 
 * @author Christian Kaltepoth
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RolesRequired
{

   /**
    * The roles required for the rule to match.
    */
   String[] value();

}
