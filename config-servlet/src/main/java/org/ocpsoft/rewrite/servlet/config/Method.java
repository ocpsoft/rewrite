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

import org.ocpsoft.rewrite.bind.Bindable;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.Bindings;
import org.ocpsoft.rewrite.bind.DefaultBindable;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * Responsible for asserting on the {@link HttpServletRequest#getMethod()} property.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Method extends HttpCondition implements Bindable<Method>
{
   private final HttpMethod method;

   @SuppressWarnings("rawtypes")
   private final DefaultBindable<?> bindable = new DefaultBindable();

   private enum HttpMethod
   {
      GET, POST, HEAD, OPTIONS, PUT, DELETE, TRACE
   }

   private Method(final HttpMethod method)
   {
      this.method = method;
   }

   /**
    * Return a {@link Method} condition that ensures the current {@link HttpServletRequest#getMethod()} is of GET
    */
   public static Method isGet()
   {
      return new Method(HttpMethod.GET);
   }

   /**
    * Return a {@link Method} condition that ensures the current {@link HttpServletRequest#getMethod()} is of POST
    */
   public static Method isPost()
   {
      return new Method(HttpMethod.POST);
   }

   /**
    * Return a {@link Method} condition that ensures the current {@link HttpServletRequest#getMethod()} is of HEAD
    */
   public static Method isHead()
   {
      return new Method(HttpMethod.HEAD);
   }

   /**
    * Return a {@link Method} condition that ensures the current {@link HttpServletRequest#getMethod()} is of OPTIONS
    */
   public static Method isOptions()
   {
      return new Method(HttpMethod.OPTIONS);
   }

   /**
    * Return a {@link Method} condition that ensures the current {@link HttpServletRequest#getMethod()} is of PUT
    */
   public static Method isPut()
   {
      return new Method(HttpMethod.PUT);
   }

   /**
    * Return a {@link Method} condition that ensures the current {@link HttpServletRequest#getMethod()} is of DELETE
    */
   public static Method isDelete()
   {
      return new Method(HttpMethod.DELETE);
   }

   /**
    * Return a {@link Method} condition that ensures the current {@link HttpServletRequest#getMethod()} is of TRACE
    */
   public static Method isTrace()
   {
      return new Method(HttpMethod.TRACE);
   }

   @Override
   public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      if (this.method.equals(HttpMethod.valueOf(event.getRequest().getMethod())))
      {
         Bindings.enqueueSubmission(event, context, bindable, method.name());
         return true;
      }
      return false;
   }

   @Override
   public Method bindsTo(final Binding binding)
   {
      bindable.bindsTo(binding);
      return this;
   }

}