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

import javax.enterprise.context.ConversationScoped;
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
 * Provides support for the CDI 1.0 {@link ConversationScoped} context in a pure servlet environment, which is not
 * normally accessible outside of the JavaServer Faces lifecycle.
 * <p>
 * Please note that this is implemented using <a href="http://github.com/seam/conversation">Seam Conversation</a>, and
 * you must include the correct additional dependency in order for this to function with your CDI implementation: (see
 * below)
 * <p>
 * <h3>Implementations</h3> <code>
 * <b>Weld</b> [org.jboss.seam.conversation:seam-conversation-weld]<br/>
 * <b>CanDI</b> [org.jboss.seam.conversation:seam-conversation-candi]<br/>
 * <b>OpenWebBeans</b> [org.jboss.seam.conversation:seam-conversation-owb]
 * </code>
 * <p>
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class CdiConversationSupport
{
   Logger log = Logger.getLogger(CdiConversationSupport.class);

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
