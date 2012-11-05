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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.component.UIParameter;

import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.config.PrettyConfig;
import com.ocpsoft.pretty.faces.config.mapping.PathParameter;
import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;
import com.ocpsoft.pretty.faces.url.QueryString;
import com.ocpsoft.pretty.faces.url.URL;
import com.ocpsoft.pretty.faces.util.PrettyURLBuilder;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class UrlMappingRuleAdaptor implements Rule
{
   public static final String REWRITE_MAPPING_ID_KEY = "com.ocpsoft.mappingId";
   private static Logger log = Logger.getLogger(UrlMappingRuleAdaptor.class);
   private final UrlMapping mapping;

   public UrlMappingRuleAdaptor(final UrlMapping mapping)
   {
      this.mapping = mapping;
   }

   private String rewritePrettyMappings(final PrettyConfig config, final String contextPath, final String url)
   {
      String result = url;

      if (url != null)
      {
         List<UIParameter> uiParams = new ArrayList<UIParameter>();

         QueryString qs = QueryString.build("");
         if (url.contains("?"))
         {
            qs.addParameters(url);

            // remove own own metadata
            qs.removeParameter("com.ocpsoft.mappingId");
         }
         Map<String, String[]> queryParams = qs.getParameterMap();

         List<PathParameter> pathParams = mapping.getPatternParser().getPathParameters();

         int pathParamsFound = 0;
         for (PathParameter p : pathParams)
         {
            UIParameter uip = new UIParameter();
            String[] values = queryParams.get(p.getName());
            if ((values != null) && (values.length > 0))
            {
               String value = values[0];
               uip.setValue(value);
               if ((value != null) && !"".equals(value))
               {
                  pathParamsFound++;
               }
            }
            queryParams.remove(p.getName());
            uiParams.add(uip);
         }

         for (Entry<String, String[]> entry : queryParams.entrySet())
         {
            UIParameter uip = new UIParameter();
            uip.setName(entry.getKey());
            uip.setValue(entry.getValue());
            uiParams.add(uip);
         }

         if (pathParams.size() == pathParamsFound)
         {
            PrettyURLBuilder builder = new PrettyURLBuilder();
            result = contextPath + builder.build(mapping, true, uiParams);
         }
      }
      return result;
   }

   @Override
   public String getId()
   {
      return toString();
   }

   @Override
   public boolean evaluate(final Rewrite event, final EvaluationContext context)
   {
      if ((event instanceof HttpInboundServletRewrite)
               && mapping.matches(new URL(((HttpServletRewrite)event).getRequestURL())))
      {
         return true;
      }
      else if ((event instanceof HttpOutboundServletRewrite)
               && mapping.isOutbound()) {
         String outboundURL = ((HttpOutboundServletRewrite) event).getOutboundURL();
         if (outboundURL.startsWith(((HttpServletRewrite) event).getContextPath()))
         {
            outboundURL = outboundURL.substring(((HttpServletRewrite) event).getContextPath().length());
         }

         QueryString queryString = QueryString.build(outboundURL);
         String mappingId = queryString.getParameter(REWRITE_MAPPING_ID_KEY);

         if (((mappingId == null) && outboundURL.startsWith(mapping.getViewId()))
                  || mapping.getId().equals(mappingId))
         {
            return true;
         }
      }
      return false;
   }

   @Override
   public void perform(final Rewrite event, final EvaluationContext ec)
   {
      PrettyContext context = PrettyContext.getCurrentInstance(((HttpServletRewrite) event).getRequest());

      if (event instanceof HttpInboundServletRewrite)
      {
         URL url = URL.build(((HttpServletRewrite) event).getRequestURL());
         if (context.shouldProcessDynaview())
         {
            log.trace("Forwarding mapped request [" + url.toURL() + "] to dynaviewId [" + context.getDynaViewId() + "]");
            ((HttpInboundServletRewrite) event).forward(context.getDynaViewId());
         }
         else
         {
            String viewId = mapping.getViewId();
            log.trace("Forwarding mapped request [" + url.toURL() + "] to resource [" + viewId + "]");
            if (url.decode().toURL().equals(viewId))
            {
               ((HttpServletRewrite) event).proceed();
            }
            else
            {
               ((HttpInboundServletRewrite) event).forward(viewId);
            }
         }
      }
      else if ((event instanceof HttpOutboundServletRewrite)
               && mapping.isOutbound())
      {
         HttpOutboundServletRewrite outboundRewrite = (HttpOutboundServletRewrite) event;
         String newUrl = rewritePrettyMappings(context.getConfig(), ((HttpServletRewrite) event).getContextPath(),
                  outboundRewrite.getOutboundURL());
         outboundRewrite.setOutboundURL(newUrl);
      }

   }

   @Override
   public String toString()
   {
      return "UrlMappingRuleAdaptor [mapping=" + mapping + "]";
   }
}
