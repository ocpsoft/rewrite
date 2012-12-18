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
package org.ocpsoft.rewrite.cdi.bridge;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;

import org.ocpsoft.rewrite.cdi.events.AfterInboundRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;
import org.ocpsoft.urlbuilder.AddressBuilder;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@RequestScoped
public class RewriteLifecycleEventObserver
{
   public void rewriteInbound2(@Observes final HttpInboundServletRewrite event)
   {
      if (event.getInboundAddress().getPath().equals(event.getContextPath() + "/redirect-301"))
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
      if (event.getOutboundAddress().getPath().equals(event.getContextPath() + "/outbound"))
      {
         event.setOutboundAddress(AddressBuilder.create(event.getContextPath() + "/outbound-rewritten"));
      }
   }

   public void rewriteInbound(@Observes final HttpInboundServletRewrite event)
   {
      String requestURL = event.getInboundAddress().getPath();
      if ((event.getContextPath() + "/success").equals(requestURL))
         event.sendStatusCode(200);
      else if ((event.getContextPath() + "/outbound-rewritten").equals(requestURL))
         event.sendStatusCode(200);
   }
}
