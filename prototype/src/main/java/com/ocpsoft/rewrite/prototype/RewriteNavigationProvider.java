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
package com.ocpsoft.rewrite.prototype;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.mvc.spi.NavigationProvider;

/**
 * This class will be provided by the MVC framework, and will use whatever kind of configuration we decide to go with.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class RewriteNavigationProvider implements NavigationProvider<Flow>
{
   @Override
   public boolean handles(final Object result)
   {
      return (result instanceof Flow);
   }

   @Override
   public boolean navigate(final HttpServletRequest req, final HttpServletResponse resp, final Flow result)
   {
      // Always redirect. Otherwise, we have form issues.
      resp.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
      resp.setHeader("Location",
               resp.encodeRedirectURL(req.getContextPath() + "/" + result.name().toLowerCase() + ".mvc"));
      try {
         resp.flushBuffer();
      }
      catch (IOException e) {
         throw new RuntimeException(e);
      }
      return true;
   }
}
