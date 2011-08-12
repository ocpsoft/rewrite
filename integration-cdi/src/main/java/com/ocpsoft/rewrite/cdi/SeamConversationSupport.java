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
package com.ocpsoft.rewrite.cdi;

import javax.enterprise.event.Observes;
import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.conversation.spi.SeamConversationContext;

import com.ocpsoft.rewrite.cdi.events.AfterRewriteLifecycle;
import com.ocpsoft.rewrite.cdi.events.BeforeRewriteLifecycle;
import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.logging.Logger;
import com.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class SeamConversationSupport
{
   Logger log = Logger.getLogger(SeamConversationSupport.class);

   public void setup(@Observes final BeforeRewriteLifecycle event, final SeamConversationContext<HttpServletRequest> scc)
   {
      Rewrite rewrite = event.getRewrite();
      if (rewrite instanceof HttpInboundServletRewrite)
      {
         HttpServletRequest request = ((HttpServletRewrite) rewrite).getRequest();
         DispatcherType dispatchType = request.getDispatcherType();
         if (DispatcherType.REQUEST.equals(dispatchType))
         {
            String cid = request.getParameter("cid");
            log.debug("Binding conversation with id [" + cid + "] to Request.");
            scc.associate(request).activate(cid);
         }
      }
   }

   public void teardown(@Observes final AfterRewriteLifecycle event,
            final SeamConversationContext<HttpServletRequest> scc)
   {
      Rewrite rewrite = event.getRewrite();
      if (rewrite instanceof HttpInboundServletRewrite)
      {
         HttpServletRequest request = ((HttpServletRewrite) rewrite).getRequest();
         DispatcherType dispatchType = request.getDispatcherType();
         if (DispatcherType.REQUEST.equals(dispatchType))
         {
            log.debug("Unbinding any active conversation from Request.");
            scc.dissociate(request);
         }
      }
   }
}
