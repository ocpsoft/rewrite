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
package com.ocpsoft.rewrite.util;

import java.util.List;

import org.jboss.logging.Logger;

import com.ocpsoft.rewrite.pattern.Weighted;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public final class ServiceLogger
{

   public static <T> void logLoadedServices(final Logger log, final Class<T> type, final List<? extends T> services)
   {
      log.info("Loaded [" + services.size() + "] " + type.getName() + " ["
               + joinTypeNames(services) + "]");
   }

   private static String joinTypeNames(final List<?> list)
   {
      StringBuilder result = new StringBuilder();
      for (int i = 0; i < list.size(); i++)
      {
         Object service = list.get(i);
         result.append(service.getClass().getName());
         if (service instanceof Weighted)
         {
            result.append("<" + ((Weighted) service).priority() + ">");
         }
         if ((i + 1) < list.size())
         {
            result.append(", ");
         }
      }
      return result.toString();
   }
}
