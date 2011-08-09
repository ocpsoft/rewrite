/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.ocpsoft.rewrite.cdi;

import javax.enterprise.event.Observes;
import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;

import org.jboss.logging.Logger;
import org.jboss.seam.conversation.spi.SeamConversationContext;

import com.ocpsoft.rewrite.cdi.events.AfterInboundRewrite;
import com.ocpsoft.rewrite.cdi.events.BeforeRewriteLifecycle;
import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ConversationSupport
{
   Logger log = Logger.getLogger(ConversationSupport.class);

   public void setup(@Observes BeforeRewriteLifecycle event, SeamConversationContext<HttpServletRequest> scc)
   {
      Rewrite rewrite = event.getRewrite();
      if (rewrite instanceof HttpInboundServletRewrite)
      {
         HttpServletRequest request = ((HttpServletRewrite) rewrite).getRequest();
         DispatcherType dispatchType = request.getDispatcherType();
         if (DispatcherType.REQUEST.equals(dispatchType))
         {
            log.info("Binding conversation to Request.");
            String cid = request.getParameter("cid");
            scc.associate(request).activate(cid);
         }
      }
   }

   public void teardown(@Observes AfterInboundRewrite event, SeamConversationContext<HttpServletRequest> scc)
   {
      Rewrite rewrite = event.getRewrite();
      if (rewrite instanceof HttpInboundServletRewrite)
      {
         HttpServletRequest request = ((HttpServletRewrite) rewrite).getRequest();
         DispatcherType dispatchType = request.getDispatcherType();
         if (DispatcherType.REQUEST.equals(dispatchType))
         {
            log.info("Unbinding conversation from Request.");
            scc.dissociate(request);
         }
      }
   }
}
