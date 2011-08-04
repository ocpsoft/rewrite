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
package com.ocpsoft.rewrite.servlet.config;

import com.ocpsoft.rewrite.EvaluationContext;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Method extends HttpCondition
{
   private final HttpMethod method;

   public enum HttpMethod
   {
      GET, POST, HEAD, OPTIONS, PUT, DELETE, TRACE
   }

   public Method(final HttpMethod method)
   {
      this.method = method;
   }

   public static Method isGet()
   {
      return new Method(HttpMethod.GET);
   }

   public static Method isPost()
   {
      return new Method(HttpMethod.POST);
   }

   public static Method isHead()
   {
      return new Method(HttpMethod.HEAD);
   }

   public static Method isOptions()
   {
      return new Method(HttpMethod.OPTIONS);
   }

   public static Method isPut()
   {
      return new Method(HttpMethod.PUT);
   }

   public static Method isDelete()
   {
      return new Method(HttpMethod.DELETE);
   }

   public static Method isTrace()
   {
      return new Method(HttpMethod.TRACE);
   }

   @Override
   public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      return this.method.equals(HttpMethod.valueOf(event.getRequest().getMethod()));
   }

}