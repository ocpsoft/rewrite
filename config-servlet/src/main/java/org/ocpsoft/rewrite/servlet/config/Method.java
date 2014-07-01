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

import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.common.util.Strings;
import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * A {@link Condition} responsible for asserting on the {@link HttpServletRequest#getMethod()} property.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class Method extends HttpCondition
{
   private final HttpMethod method;

   private enum HttpMethod
   {
      GET, POST, HEAD, OPTIONS, PUT, DELETE, TRACE
   }

   @Override
   public String toString()
   {
      return "Method.is" + Strings.capitalize(method.name().toLowerCase()) + "()";
   }

   private Method(final HttpMethod method)
   {
      this.method = method;
   }

   /**
    * Create a {@link Method} condition that ensures the current {@link HttpServletRequest#getMethod()} is GET
    */
   public static Method isGet()
   {
      return new Method(HttpMethod.GET) {};
   }

   /**
    * Create a {@link Method} condition that ensures the current {@link HttpServletRequest#getMethod()} is POST
    */
   public static Method isPost()
   {
      return new Method(HttpMethod.POST) {};
   }

   /**
    * Create a {@link Method} condition that ensures the current {@link HttpServletRequest#getMethod()} is HEAD
    */
   public static Method isHead()
   {
      return new Method(HttpMethod.HEAD) {};
   }

   /**
    * Create a {@link Method} condition that ensures the current {@link HttpServletRequest#getMethod()} is OPTIONS
    */
   public static Method isOptions()
   {
      return new Method(HttpMethod.OPTIONS) {};
   }

   /**
    * Create a {@link Method} condition that ensures the current {@link HttpServletRequest#getMethod()} is PUT
    */
   public static Method isPut()
   {
      return new Method(HttpMethod.PUT) {};
   }

   /**
    * Create a {@link Method} condition that ensures the current {@link HttpServletRequest#getMethod()} is DELETE
    */
   public static Method isDelete()
   {
      return new Method(HttpMethod.DELETE) {};
   }

   /**
    * Create a {@link Method} condition that ensures the current {@link HttpServletRequest#getMethod()} is TRACE
    */
   public static Method isTrace()
   {
      return new Method(HttpMethod.TRACE) {};
   }

   @Override
   public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      if (this.method.equals(HttpMethod.valueOf(event.getRequest().getMethod())))
      {
         return true;
      }
      return false;
   }

}