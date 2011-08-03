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

import com.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class SendStatus extends HttpInboundOperation
{
   private final int code;

   public SendStatus(final int code)
   {
      this.code = code;
   }

   @Override
   public void performInbound(final HttpInboundServletRewrite event)
   {
      event.sendStatusCode(code);
   }

   /**
    * Send an HTTP status code to the browser, then call {@link HttpInboundServletRewrite#abort()}
    */
   public static SendStatus code(final int code)
   {
      return new SendStatus(code);
   }

}
