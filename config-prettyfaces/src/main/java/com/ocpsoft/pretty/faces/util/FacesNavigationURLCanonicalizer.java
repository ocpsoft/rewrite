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
package com.ocpsoft.pretty.faces.util;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 * <p>
 * This class holds different JSF utility methods.
 * </p>
 * 
 * @author Christian Kaltepoth
 */
public class FacesNavigationURLCanonicalizer
{

   /**
    * This method reads all required values from the {@link FacesContext} and
    * then delegates the call to
    * {@link #normalizeRequestURI(String, String, String)}.
    * 
    * @param context
    *           The {@link FacesContext}
    * @param viewId
    *           the view id
    * @return the URI without additional FacesServlet mappings
    */
   public static String normalizeRequestURI(final FacesContext context, final String viewId)
   {
      ExternalContext externalContext = context.getExternalContext();
      return normalizeRequestURI(externalContext.getRequestServletPath(), externalContext.getRequestPathInfo(), viewId);
   }
   
   /**
    * Prune the url-pattern from the <code>viewId</code> if prefix mapped
    * FacesServlet, otherwise return the original URI.
    * 
    * @param servletPath
    *           the servlet path
    * @param requestPathInfo
    *           the path info
    * @param viewId
    *           the view id
    * @return the URI without additional FacesServlet mappings
    * @see javax.servlet.http.HttpServletRequest#getServletPath()
    * @see javax.servlet.http.HttpServletRequest#getPathInfo()
    */
   public static String normalizeRequestURI(final String servletPath, final String requestPathInfo, final String viewId)
   {
      
      // We must not process empty viewIds
      if (viewId == null)
      {
         return null;
      }

      /*
       * code from com.sun.faces.util.Util#getMappingForRequest(String servletPath, String pathInfo)
       * and  com.sun.faces.application.view.MultiViewHandler#normalizeRequestURI(String uri, String mapping)
       */
      if (servletPath != null)
      {
         if ((requestPathInfo != null || servletPath.indexOf('.') < 0) && viewId.startsWith(servletPath + '/'))
         {
            return viewId.substring(servletPath.length());
         }
      }

      return viewId;
   }

}
