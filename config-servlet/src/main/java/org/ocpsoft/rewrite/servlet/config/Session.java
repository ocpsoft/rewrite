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

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

import javax.servlet.http.HttpSession;

/**
 * Responsible for performing actions on the current {@link HttpSession}
 */
public abstract class Session extends HttpOperation
{

   /**
    * Creates an {@link HttpOperation} that calls {@link HttpSession#invalidate()} on the current
    * session.
    */
   public static Session invalidate()
   {
      return new Session() {
         @Override
         public void performHttp(HttpServletRewrite event, EvaluationContext context)
         {
            event.getRequest().getSession().invalidate();
         }

         @Override
         public String toString()
         {
            return "Session.invalidate()";
         }
      };
   }

   /**
    * Creates an {@link HttpOperation} that calls {@link HttpSession#setAttribute(String, Object)} on the current
    * session.
    * 
    * @param name The name of the session attribute
    * @param value The value to set
    */
   public static Session setAttribute(final String name, final Object value)
   {
      return new Session() {
         @Override
         public void performHttp(HttpServletRewrite event, EvaluationContext context)
         {
            event.getRequest().getSession().setAttribute(name, value);
         }

         @Override
         public String toString()
         {
            return "Session.setAttribute(\"" + name + "\", \"" + value + "\")";
         }
      };
   }
}
