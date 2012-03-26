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

import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.common.util.Assert;

import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * Responsible for asserting on the {@link HttpServletRequest#getServerPort()}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ServerPort extends HttpCondition
{
   private final int[] ports;

   private ServerPort(final int... ports)
   {
      Assert.notNull(ports, "Must specify at least one valid port.");
      for (int port : ports) {

         if ((port < 1) || (port > 65535))
         {
            throw new IllegalArgumentException("Invalid port number: " + ports
                     + " - must be between 1 and 65535, inclusive.");
         }
      }
      this.ports = ports;
   }

   /**
    * Assert that the server port matches any of the given ports.
    */
   public static ServerPort is(final int... ports)
   {
      return new ServerPort(ports);
   }

   @Override
   public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      int serverPort = event.getRequest().getServerPort();
      for (int port : ports) {
         if (serverPort == port)
            return true;
      }
      return false;
   }

}