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
package org.ocpsoft.rewrite.prettyfaces;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.rewrite.servlet.spi.RequestParameterProvider;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.config.PrettyConfig;
import com.ocpsoft.pretty.faces.config.mapping.PathParameter;
import com.ocpsoft.pretty.faces.url.QueryString;
import com.ocpsoft.pretty.faces.url.URL;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class PrettyFacesRequestParameterProvider implements RequestParameterProvider
{
   @Override
   public Map<String, String[]> getAdditionalParameters(final ServletRequest request, final ServletResponse response)
   {
      PrettyContext context = PrettyContext.getCurrentInstance((HttpServletRequest)request);
      PrettyConfig config = context.getConfig();

      URL url = context.getRequestURL();
      if (config.isURLMapped(url))
      {
         List<PathParameter> params = context.getCurrentMapping().getPatternParser().parse(url);
         QueryString query = QueryString.build(params);

         return query.getParameterMap();
      }
      return null;
   }

   @Override
   public int priority()
   {
      return 0;
   }

}
