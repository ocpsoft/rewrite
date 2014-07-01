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
package org.ocpsoft.rewrite.servlet.config;

import java.util.Arrays;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.common.util.Strings;
import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * A {@link Condition} that checks the current session roles using {@link HttpServletRequest#isUserInRole(String)}
 * 
 * @author Christian Kaltepoth
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class JAASRoles extends HttpCondition
{
   private final Collection<String> roles;

   /**
    * Create a new {@link JAASRoles} condition specifying the roles of which the current user must be a member for
    * evaluation to return <code>true</code>.
    */
   public static JAASRoles required(String... roles)
   {
      return new JAASRoles(roles);
   }

   private JAASRoles(String[] roles)
   {
      this.roles = Arrays.asList(roles);
   }

   @Override
   public boolean evaluateHttp(HttpServletRewrite event, EvaluationContext context)
   {
      HttpServletRewrite rewrite = event;

      // check if user has all required roles
      for (String role : roles) {
         if (!rewrite.getRequest().isUserInRole(role)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public String toString()
   {
      return "JAASRoles.required(\"" + Strings.join(roles, "\", \"") + "\")";
   }
}