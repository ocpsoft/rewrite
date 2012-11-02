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
package com.ocpsoft.pretty.faces2.component;

import javax.faces.component.behavior.ClientBehaviorBase;
import javax.faces.component.behavior.ClientBehaviorContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.PrettyException;
import com.ocpsoft.pretty.faces.beans.ExtractedValuesURLBuilder;
import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;

/**
 * Simple JSF2 client behavior that changes the current URL by setting <code>window.location.href</code>.
 * 
 * @author Christian Kaltepoth
 */
public class LocationBehavior extends ClientBehaviorBase
{

   private final static Log log = LogFactory.getLog(LocationBehavior.class);

   private String mappingId;

   private String url;

   @Override
   public String getScript(final ClientBehaviorContext behaviorContext)
   {

      // use URL from url attribute if available
      if ((url != null) && (url.trim().length() > 0))
      {
         return buildScriptInternal(url.trim());
      }

      // Is there a mapping id?
      if ((mappingId == null) || (mappingId.trim().length() == 0))
      {
         log.error("Please set either 'mappingId' or 'url' attribute!");
         return null;
      }

      // try to obtain PrettyContext
      PrettyContext prettyContext = PrettyContext.getCurrentInstance(behaviorContext.getFacesContext());
      if (prettyContext == null)
      {
         log.error("Cannot build script because PrettyContext is not available!");
         return null;
      }

      // find UrlMapping in configuration
      UrlMapping mapping = prettyContext.getConfig().getMappingById(mappingId.trim());
      if (mapping == null)
      {
         log.error("Cannot find URL mapping with id: " + mappingId);
         return null;
      }

      try
      {

         // build path to redirect to
         ExtractedValuesURLBuilder builder = new ExtractedValuesURLBuilder();
         String contextPath = prettyContext.getContextPath();
         String path = contextPath + builder.buildURL(mapping) + builder.buildQueryString(mapping);

         // return the script
         return buildScriptInternal(path);

      }
      catch (PrettyException e)
      {
         log.error("Failed to build URL", e);
         return null;
      }
   }

   /**
    * Creates the required script for the supplied URL
    * 
    * @param url The URL
    * @return The script
    */
   private String buildScriptInternal(final String url)
   {
      StringBuilder builder = new StringBuilder();
      builder.append("window.location.href = '");
      builder.append(url);
      builder.append("'; return false;");
      return builder.toString();
   }

   public String getMappingId()
   {
      return mappingId;
   }

   public void setMappingId(final String mappingId)
   {
      this.mappingId = mappingId;
   }

   public String getUrl()
   {
      return url;
   }

   public void setUrl(final String url)
   {
      this.url = url;
   }

}
