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
package com.ocpsoft.rewrite.servlet.config;

import java.util.Collections;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import com.ocpsoft.rewrite.util.Assert;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Header extends HttpCondition
{
   private final Pattern name;
   private final Pattern value;

   private Header(final String nameRegex, final String valueRegex)
   {
      Assert.notNull(nameRegex, "Header name pattern cannot be null.");
      Assert.notNull(valueRegex, "Header value pattern cannot be null.");
      this.name = Pattern.compile(nameRegex);
      this.value = Pattern.compile(valueRegex);
   }

   /**
    * Return a {@link Header} condition that matches against both header name and values.
    * 
    * @param nameRegex Regular expression matching the header name
    * @param valueRegex Regular expression matching the header value
    */
   public static Header matches(final String nameRegex, final String valueRegex)
   {
      return new Header(nameRegex, valueRegex);
   }

   /**
    * Return a {@link Header} condition that matches only against the existence of a header with a name matching the
    * given pattern. The header value is ignored.
    * 
    * @param nameRegex Regular expression matching the header name
    */
   public static Header exists(final String nameRegex)
   {
      return new Header(nameRegex, ".*");
   }

   /**
    * Return a {@link Header} condition that matches only against the existence of a header with value matching the
    * given pattern. The header name is ignored.
    * 
    * @param valueRegex Regular expression matching the header value
    */
   public static Header valueExists(final String valueRegex)
   {
      return new Header(".*", valueRegex);
   }

   @Override
   public boolean evaluateHttp(final HttpServletRewrite event)
   {
      HttpServletRequest request = event.getRequest();
      for (String header : Collections.list(request.getHeaderNames()))
      {
         if (name.matcher(header).matches() && matchesValue(request, header))
         {
            return true;
         }
      }
      return false;
   }

   private boolean matchesValue(final HttpServletRequest request, final String header)
   {
      for (String contents : Collections.list(request.getHeaders(header)))
      {
         if (value.matcher(contents).matches())
         {
            return true;
         }
      }
      return false;
   }
}
