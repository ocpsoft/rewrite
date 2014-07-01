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

import javax.servlet.http.HttpServletResponse;

import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * An {@link Operation} responsible for sending status codes via {@link HttpServletResponse#setStatus(int)} and
 * {@link HttpServletResponse#flushBuffer()}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class SendStatus extends HttpOperation
{
   private final int code;
   private String message;

   private SendStatus(final int code)
   {
      this.code = code;
   }

   private SendStatus(final int code, String message)
   {
      this.code = code;
      this.message = message;
   }

   @Override
   public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      if (event instanceof HttpInboundServletRewrite) {
         if (getMessage() != null)
            ((HttpInboundServletRewrite) event).sendStatusCode(this.getCode(), this.getMessage());
         else
            ((HttpInboundServletRewrite) event).sendStatusCode(this.getCode());
      }
   }

   protected int getCode()
   {
      return code;
   }

   protected String getMessage()
   {
      return message;
   }

   /**
    * Create an {@link Operation} that will send an HTTP status code to the browser, then call
    * {@link HttpInboundServletRewrite#abort()}
    */
   public static SendStatus code(final int code)
   {
      return new SendStatus(code) {
         @Override
         public String toString()
         {
            return "SendStatus.code(" + code + ")";
         }
      };
   }

   /**
    * Create an {@link Operation} that will send an HTTP error code to the browser, then call
    * {@link HttpInboundServletRewrite#abort()}
    */
   public static SendStatus error(final int code)
   {
      return new SendError(code, null) {
         @Override
         public String toString()
         {
            return "SendStatus.error(" + code + ")";
         }
      };
   }

   /**
    * Create an {@link Operation} that will send an HTTP error code and message to the browser, then call
    * {@link HttpInboundServletRewrite#abort()}
    */
   public static SendStatus error(final int code, final String message)
   {
      return new SendError(code, message) {
         @Override
         public String toString()
         {
            return "SendStatus.error(" + code + ", \"" + message + "\")";
         }
      };
   }

   private abstract static class SendError extends SendStatus
   {
      public SendError(final int code, String message)
      {
         super(code, message);
      }

      @Override
      public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
      {
         if (event instanceof HttpInboundServletRewrite)
         {
            if (getMessage() != null)
               ((HttpInboundServletRewrite) event).sendErrorCode(this.getCode(), this.getMessage());
            else
               ((HttpInboundServletRewrite) event).sendErrorCode(this.getCode());

         }
      }
   }

}
