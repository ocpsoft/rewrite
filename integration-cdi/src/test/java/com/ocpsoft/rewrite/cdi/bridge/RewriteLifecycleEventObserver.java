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
package com.ocpsoft.rewrite.cdi.bridge;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;

import com.ocpsoft.rewrite.cdi.events.AfterInboundRewrite;
import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.servlet.event.RewriteBase.Flow;
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
      Rewrite rewrite = event.getRewrite();
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
      if (!event.getFlow().is(Flow.HANDLED))
         event.sendStatusCode(200);
   }
}
