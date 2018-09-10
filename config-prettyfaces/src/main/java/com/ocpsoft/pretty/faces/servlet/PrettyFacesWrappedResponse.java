/*
 * Copyright 2010 Lincoln Baxter, III
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

package com.ocpsoft.pretty.faces.servlet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.component.UIParameter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.ocpsoft.pretty.PrettyException;
import com.ocpsoft.pretty.faces.config.PrettyConfig;
import com.ocpsoft.pretty.faces.config.mapping.PathParameter;
import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;
import com.ocpsoft.pretty.faces.config.rewrite.RewriteRule;
import com.ocpsoft.pretty.faces.rewrite.RewriteEngine;
import com.ocpsoft.pretty.faces.url.QueryString;
import com.ocpsoft.pretty.faces.util.PrettyURLBuilder;

/**
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public class PrettyFacesWrappedResponse extends HttpServletResponseWrapper
{
   public static final String REWRITE_MAPPING_ID_KEY = "com.ocpsoft.mappingId";

   private final RewriteEngine rewriteEngine = new RewriteEngine();

   private final PrettyConfig prettyConfig;

   private final String contextPath;

   private final HttpServletRequest request;

   public PrettyFacesWrappedResponse(final String contextPath, final HttpServletRequest request,
            final HttpServletResponse response,
            final PrettyConfig config)
   {
      super(response);
      this.request = request;
      this.contextPath = contextPath;
      this.prettyConfig = config;
   }

   // they changed the API to use URL instead of Url
   // someday they will remove it and this can be removed
   // in the meantime, mark as deprecated so people don't use it
   @Deprecated
   @Override
   public String encodeRedirectUrl(final String url)
   {
      return super.encodeRedirectURL(url);
   }

   @Override
   public String encodeRedirectURL(final String url)
   {
      return super.encodeRedirectURL(url);
   }

   // they changed the API to use URL instead of Url
   // someday they will remove it and this can be removed
   // in the meantime, mark as deprecated so people don't use it
   @Deprecated
   @Override
   public String encodeUrl(final String url)
   {
      return super.encodeURL(url);
   }

   @Override
   public String encodeURL(final String url)
   {
      String result = rewritePrettyMappings(url);

      result = rewrite(result);

      return super.encodeURL(result);
   }

   private static final Comparator<UrlMapping> ORDINAL_COMPARATOR = new Comparator<UrlMapping>()
   {
      public int compare(final UrlMapping l, final UrlMapping r)
      {
         if (l.getPatternParser().getParameterCount() < r.getPatternParser().getParameterCount())
         {
            return 1;
         }
         else if (l.getPatternParser().getParameterCount() > r.getPatternParser().getParameterCount())
         {
            return -1;
         }
         return 0;
      }
   };

   private String rewritePrettyMappings(final String url)
   {
      String result = url;

      if (url != null)
      {
         String strippedUrl = stripContextPath(url);

         List<UrlMapping> matches = new ArrayList<UrlMapping>();

         /*
          * First build an empty query string. We will add the parameters
          * only if the URL contains a ? character in the next step
          */
         final QueryString queryString = QueryString.build("");

         /*
          * Try to identify the mapping for this URL. Remove the metadata
          * from the URL if it exists.
          */
         String mappingId = null;
         if (strippedUrl.contains("?")) {
            queryString.addParameters(strippedUrl);
            mappingId = queryString.getParameter(REWRITE_MAPPING_ID_KEY);
            queryString.removeParameter(REWRITE_MAPPING_ID_KEY);
         }

         if (mappingId != null)
         {
            matches.add(prettyConfig.getMappingById(mappingId));
         }
         else
         {
            for (UrlMapping m : prettyConfig.getMappings())
            {
               if (!"".equals(m.getViewId()) && strippedUrl.startsWith(m.getViewId()))
               {
                  matches.add(m);
               }
            }
         }

         Collections.sort(matches, ORDINAL_COMPARATOR);

         Iterator<UrlMapping> iterator = matches.iterator();
         while (iterator.hasNext())
         {
            UrlMapping m = iterator.next();

            if (m.isOutbound())
            {
               List<UIParameter> uiParams = new ArrayList<UIParameter>();

               Map<String, String[]> queryParams = queryString.getParameterMap();

               List<PathParameter> pathParams = m.getPatternParser().getPathParameters();

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
                  result = contextPath + builder.build(m, true, uiParams);
                  break;
               }
            }
         }
      }
      return result;
   }

   private String rewrite(final String url)
   {
      String result = "";
      if (url != null)
      {
         String strippedUrl = stripContextPath(url);

         if (!strippedUrl.equals(url))
         {
            result = contextPath;
         }

         try
         {
            for (RewriteRule c : prettyConfig.getGlobalRewriteRules())
            {
               strippedUrl = rewriteEngine.processOutbound(request, this, c, strippedUrl);
            }
            result += strippedUrl;
         }
         catch (Exception e)
         {
            throw new PrettyException("Error occurred during canonicalization of request <[" + url + "]>", e);
         }
      }
      return result;
   }

   /**
    * If the given URL is prefixed with this request's context-path, return the URI without the context path. Otherwise
    * return the URI unchanged.
    */
   private String stripContextPath(String uri)
   {
      if (!contextPath.equals("/") && uri.startsWith(contextPath))
      {
         uri = uri.substring(contextPath.length());
      }
      return uri;
   }
}
