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
package com.ocpsoft.rewrite.cdi.bridge;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;

import com.ocpsoft.rewrite.cdi.events.AfterInboundRewrite;
import com.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import com.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@RequestScoped
public class RewriteLifecycleEventObserver
{
   public void rewriteInbound2(@Observes final HttpInboundServletRewrite event)
   {
      System.out.println("Inbound: " + event.getRequestURL());
      if (event.getRequestURL().equals("/redirect-301"))
      {
         event.redirectTemporary(event.getContextPath() + "/outbound");
      }
   }

   public void invokeAction(@Observes final AfterInboundRewrite event)
   {
      event.getRewrite();
   }

   public void rewriteOutbound(@Observes final HttpOutboundServletRewrite event)
   {
      if (event.getOutboundURL().equals(event.getContextPath() + "/outbound"))
      {
         event.setOutboundURL(event.getContextPath() + "/outbound-rewritten");
      }
   }

   public void rewriteInbound(@Observes final HttpInboundServletRewrite event)
   {
      String requestURL = event.getRequestURL();
      if ("/success".equals(requestURL))
         event.sendStatusCode(200);
      else if ("/outbound-rewritten".equals(requestURL))
         event.sendStatusCode(200);
   }
}
