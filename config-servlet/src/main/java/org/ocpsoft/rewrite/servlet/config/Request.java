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

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * Responsible for performing actions on the current {@link HttpServletRequest}
 * 
 * @author Christian Kaltepoth
 */
public abstract class Request extends HttpOperation
{

   /**
    * Creates an {@link HttpOperation} that calls {@link HttpServletRequest#setCharacterEncoding(String)} on the current
    * request.
    * 
    * @param encoding The encoding to apply
    */
   public static Request setCharacterEncoding(final String encoding)
   {
      return new Request() {
         @Override
         public void performHttp(HttpServletRewrite event, EvaluationContext context)
         {
            try {
               event.getRequest().setCharacterEncoding(encoding);
            }
            catch (UnsupportedEncodingException e) {
               throw new IllegalArgumentException("Failed to set encoding " + encoding, e);
            }
         }

         @Override
         public String toString()
         {
            return "Request.setCharacterEncoding(\"" + encoding + "\")";
         }
      };
   }

   /**
    * Creates an {@link HttpOperation} that calls {@link HttpServletRequest#setCharacterEncoding(String)} on the current
    * request.
    * 
    * @param charset The character set to apply
    */
   public static Request setCharacterEncoding(final Charset charset)
   {
      return setCharacterEncoding(charset.name());
   }

   /**
    * Creates an {@link HttpOperation} that calls {@link HttpServletRequest#setAttribute(String, Object)} on the current
    * request.
    * 
    * @param name The name of the request attribute
    * @param value The value to set
    */
   public static Request setAttribute(final String name, final Object value)
   {
      return new Request() {
         @Override
         public void performHttp(HttpServletRewrite event, EvaluationContext context)
         {
            event.getRequest().setAttribute(name, value);
         }

         @Override
         public String toString()
         {
            return "Request.setAttribute(\"" + name + "\", \"" + value + "\")";
         }
      };
   }

}
