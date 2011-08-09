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
package com.ocpsoft.rewrite.faces;

import javax.servlet.http.HttpServletRequest;

import com.ocpsoft.rewrite.servlet.http.HttpRewriteLifecycleListener;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class OriginalURLRewriteLifecycleListener extends HttpRewriteLifecycleListener
{
   private static final String ORIGINAL_URL = OriginalURLRewriteLifecycleListener.class.getName()
            + "_originalRequestURL";

   @Override
   public void beforeInboundLifecycle(HttpServletRewrite event)
   {
      String originalURL = event.getContextPath() + event.getRequestURL() + event.getRequestQueryStringSeparator()
               + event.getRequestQueryString();
      event.getRequest().setAttribute(ORIGINAL_URL, originalURL);
   }

   public static String getOriginalRequestURL(HttpServletRequest request)
   {
      return (String) request.getAttribute(ORIGINAL_URL);
   }

   @Override
   public void beforeInboundRewrite(HttpServletRewrite event)
   {}

   @Override
   public void afterInboundRewrite(HttpServletRewrite event)
   {}

   @Override
   public void beforeOutboundRewrite(HttpServletRewrite event)
   {}

   @Override
   public void afterOutboundRewrite(HttpServletRewrite event)
   {}

   @Override
   public void afterInboundLifecycle(HttpServletRewrite event)
   {}

   @Override
   public int priority()
   {
      return 0;
   }

}
