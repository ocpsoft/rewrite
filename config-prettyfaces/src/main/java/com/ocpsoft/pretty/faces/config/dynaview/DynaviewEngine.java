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
package com.ocpsoft.pretty.faces.config.dynaview;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.PrettyException;
import com.ocpsoft.pretty.faces.application.PrettyRedirector;
import com.ocpsoft.pretty.faces.beans.ExtractedValuesURLBuilder;
import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;
import com.ocpsoft.pretty.faces.util.FacesElUtils;

public class DynaviewEngine
{
   public static final String DYNAVIEW = "com.ocpsoft.dynaView";

   private static final Log log = LogFactory.getLog(DynaviewEngine.class);
   private static FacesElUtils elUtils = new FacesElUtils();

   /**
    * Given the string value of the Faces Servlet mapping, return a string that is guaranteed to match when a servlet
    * forward is issued. It doesn't matter which FacesServlet we get to, as long as we get to one.
    */
   public String buildDynaViewId(final String facesServletMapping)
   {
      StringBuffer result = new StringBuffer();

      Map<Pattern, String> patterns = new LinkedHashMap<Pattern, String>();

      Pattern pathMapping = Pattern.compile("^(/.*/)\\*$");
      Pattern extensionMapping = Pattern.compile("^\\*(\\..*)$");
      Pattern defaultMapping = Pattern.compile("^/$");

      patterns.put(pathMapping, "$1" + DYNAVIEW + ".jsf");
      patterns.put(extensionMapping, "/" + DYNAVIEW + "$1");
      patterns.put(defaultMapping, "/" + DYNAVIEW + ".jsf");

      boolean matched = false;
      Iterator<Pattern> iterator = patterns.keySet().iterator();
      while ((matched == false) && iterator.hasNext())
      {
         Pattern p = iterator.next();
         Matcher m = p.matcher(facesServletMapping);
         if (m.matches())
         {
            String replacement = patterns.get(p);
            m.appendReplacement(result, replacement);
            matched = true;
         }
      }

      if (matched == false)
      {
         // This is an exact url-mapping, use it.
         result.append(facesServletMapping);
      }

      return result.toString();
   }

   /**
    * Handle DynaView processing. This method will end the Faces life-cycle.
    */
   public void processDynaView(final PrettyContext prettyContext, final FacesContext facesContext)
   {
      log.trace("Requesting DynaView processing for: " + prettyContext.getRequestURL());
      String viewId = "";
      try
      {
         viewId = prettyContext.getCurrentViewId();
         log.trace("Invoking DynaView method: " + viewId);
         Object result = computeDynaViewId(facesContext);
         if (result instanceof String)
         {
            viewId = (String) result;
            log.trace("Forwarding to DynaView: " + viewId);
            prettyContext.setDynaviewProcessed(true);
            facesContext.getExternalContext().dispatch(viewId);
            facesContext.responseComplete();
         }
      }
      catch (Exception e)
      {
         log.error("Failed to process dynaview", e);
         PrettyRedirector prettyRedirector = new PrettyRedirector();
         prettyRedirector.send404(facesContext);
         throw new PrettyException("Could not forward to view: " + viewId + "", e);
      }
   }

   /**
    * Calculate the Faces ViewId to which this request URI resolves. This method will recursively call any dynamic
    * mapping viewId functions as needed until a String viewId is returned, or supplied by a static mapping.
    * <p>
    * This phase does not support FacesNavigation or PrettyRedirecting. Its SOLE purpose is to resolve a viewId.
    * <p>
    * <i><b>Note:</b> Precondition - parameter injection must take place before this</i>
    * <p>
    * <i>Postcondition - currentViewId is set to computed View Id</i>
    * 
    * @param facesContext2
    * 
    * @return JSF viewID to which this request resolves.
    */
   public String computeDynaViewId(final FacesContext facesContext)
   {
      PrettyContext context = PrettyContext.getCurrentInstance(facesContext);
      UrlMapping urlMapping = context.getCurrentMapping();

      return calculateDynaviewId(facesContext, urlMapping);
   }

   public String calculateDynaviewId(final FacesContext facesContext, UrlMapping urlMapping)
   {
      String result = "";
      if (urlMapping != null)
      {
         PrettyContext context = PrettyContext.getCurrentInstance(facesContext);

         String viewId = urlMapping.getViewId();
         if (viewId == null)
         {
            viewId = "";
         }
         while (elUtils.isEl(viewId))
         {
            Object viewResult = elUtils.invokeMethod(facesContext, viewId);
            if (viewResult == null)
            {
               viewId = "";
               break;
            }
            else
            {
               viewId = viewResult.toString();
            }

            if (context.getConfig().isMappingId(viewId))
            {
               urlMapping = context.getConfig().getMappingById(viewId);
               viewId = urlMapping.getViewId();
               ExtractedValuesURLBuilder builder = new ExtractedValuesURLBuilder();
               result = context.getContextPath() + builder.buildURL(urlMapping).encode()
                         + builder.buildQueryString(urlMapping);
            }
            else
            {
               result = viewId;
            }
         }
         if ("".equals(viewId))
         {
            log.debug("ViewId for mapping with id <" + urlMapping.getId() + "> was blank");
         }
         result = context.stripContextPath(result);
      }
      return result;
   }
}
