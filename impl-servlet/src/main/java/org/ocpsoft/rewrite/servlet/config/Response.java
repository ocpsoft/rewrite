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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * Responsible for adding various properties such as headers and cookies to the {@link HttpServletResponse}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class Response extends HttpOperation
{
   /**
    * Create an {@link org.ocpsoft.rewrite.config.Operation} that adds a header to the {@link HttpServletResponse}
    */
   public static Response addHeader(final String name, final String value)
   {
      return new Response() {
         @Override
         public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
         {
            event.getResponse().addHeader(name, value);
         }
      };
   }

   /**
    * Create an {@link org.ocpsoft.rewrite.config.Operation} that adds a date header to the {@link HttpServletResponse}
    */
   public static Response addDateHeader(final String name, final long value)
   {
      return new Response() {
         @Override
         public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
         {
            event.getResponse().addDateHeader(name, value);
         }
      };
   }

   /**
    * Create an {@link org.ocpsoft.rewrite.config.Operation} that adds an int header to the {@link HttpServletResponse}
    */
   public static Response addIntHeader(final String name, final int value)
   {
      return new Response() {
         @Override
         public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
         {
            event.getResponse().addIntHeader(name, value);
         }
      };
   }

   /**
    * Create an {@link org.ocpsoft.rewrite.config.Operation} that adds a {@link Cookie} to the {@link HttpServletResponse}
    */
   public static Response addCookie(final Cookie cookie)
   {
      return new Response() {
         @Override
         public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
         {
            event.getResponse().addCookie(cookie);
         }
      };
   }

   public static Response setCode(final int code)
   {
      return new Response() {
         @Override
         public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
         {
            event.getResponse().setStatus(code);
         }
      };
   }

   @Override
   public String toString()
   {
      return "Response";
   }
   
   

}
