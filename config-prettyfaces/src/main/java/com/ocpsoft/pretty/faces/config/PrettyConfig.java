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

package com.ocpsoft.pretty.faces.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;
import com.ocpsoft.pretty.faces.config.rewrite.RewriteRule;
import com.ocpsoft.pretty.faces.url.URL;

public class PrettyConfig
{
   public static final String CONFIG_REQUEST_KEY = "pretty_CONFIG_REQUEST_KEY";
   private List<UrlMapping> mappings = new ArrayList<UrlMapping>();
   private List<RewriteRule> globalRewriteRules = new ArrayList<RewriteRule>();
   private String dynaviewId = "";
   private final Map<String, UrlMapping> cachedMappings = new LinkedHashMap<String, UrlMapping>();
   private boolean useEncodeUrlForRedirects = false;

   /**
    * Creates an empty {@link PrettyConfig} object
    */
   public PrettyConfig()
   {
      // nothing
   }

   /**
    * Set the current DynaView ID. This is used when calculating dynamic viewIds specified in pretty-config.xml (Do not
    * change unless you know what you are doing - this maps directly to your Faces Servlet mapping and is discovered
    * automatically when the rewrite system starts up.
    */
   public void setDynaviewId(final String facesDynaViewId)
   {
      this.dynaviewId = facesDynaViewId;
   }

   /**
    * Get the current DynaView ID. This is the viewId to which {@link PrettyFilter} will issue a servlet forward when
    * the developer has requested a dynamic view-id in a url-mapping.
    */
   public String getDynaviewId()
   {
      return dynaviewId;
   }

   /**
    * Return the currently configured List of {@link RewriteRule} as an unmodifiable collection.
    */
   public List<RewriteRule> getGlobalRewriteRules()
   {
      return globalRewriteRules;
   }

   /**
    * Set the current list of {@link RewriteRule} objects.
    */
   public void setGlobalRewriteRules(final List<RewriteRule> rules)
   {
      globalRewriteRules = Collections.unmodifiableList(rules);
   }

   /**
    * Get the currently configured list of {@link UrlMapping} as an unmodifiable List
    */
   public List<UrlMapping> getMappings()
   {
      return Collections.unmodifiableList(mappings);
   }

   /**
    * Set the currently configured list of {@link UrlMapping}
    */
   public void setMappings(final List<UrlMapping> mappings)
   {
      this.mappings = Collections.unmodifiableList(mappings);
   }

   /**
    * Search through all currently configured {@link UrlMapping} objects for the first one that matches the given URL.
    * 
    * @return the first appropriate {@link UrlMapping} for a given URL.
    */
   public UrlMapping getMappingForUrl(final URL url)
   {
      final String mappingKey = url.toURL();
      if (cachedMappings.containsKey(mappingKey)) {
         return cachedMappings.get(mappingKey);
      }
      for (UrlMapping mapping : getMappings())
      {
         if (mapping.matches(url))
         {
            if (!mapping.getPatternParser().isElPattern()) {
               cachedMappings.put(mappingKey, mapping);
            }
            return mapping;
         }
      }
      return null;
   }

   /**
    * Discover if the given id is a {@link UrlMapping} id specified in the current configuration.
    * 
    * @return True if the id is found, false if not.
    */
   public boolean isMappingId(final String id)
   {
      UrlMapping mapping = getMappingById(id);
      return mapping instanceof UrlMapping;
   }

   /**
    * Discover if the given URL is mapped by any {@link UrlMapping} specified in the current configuration.
    * 
    * @return True if the URL is mapped, false if not.
    */
   public boolean isURLMapped(final URL url)
   {
      UrlMapping mapping = getMappingForUrl(url);
      return mapping != null;
   }

   /**
    * Discover if the given ViewId is mapped by any {@link UrlMapping} specified in the current configuration.
    * <p>
    * <b>Note:</b>This will not match if a #{dynamicView.id} method is configured.
    * 
    * @return True if the ViewId is mapped, false if not.
    */
   public boolean isViewMapped(String viewId)
   {
      if (viewId != null)
      {
         viewId = viewId.trim();
         for (UrlMapping mapping : mappings)
         {
            if (viewId.equals(mapping.getViewId())
                     || (viewId.startsWith("/") && viewId.substring(1).equals(mapping.getViewId())))
            {
               return true;
            }
         }
      }
      return false;
   }

   /**
    * Get the {@link UrlMapping} corresponding with the given mapping id.
    * 
    * @param id Mapping id
    * @return PrettyUrlMapping Corresponding mapping
    */
   public UrlMapping getMappingById(String id)
   {
      if (id != null)
      {
         if (id.startsWith(PrettyContext.PRETTY_PREFIX))
         {
            id = id.substring(PrettyContext.PRETTY_PREFIX.length());
         }
         for (UrlMapping mapping : getMappings())
         {
            if (mapping.getId().equals(id))
            {
               return mapping;
            }
         }
      }
      return null;
   }

   public boolean isUseEncodeUrlForRedirects()
   {
      return useEncodeUrlForRedirects;
   }

   public void setUseEncodeUrlForRedirects(boolean useEncodeUrlForRedirects)
   {
      this.useEncodeUrlForRedirects = useEncodeUrlForRedirects;
   }

   @Override
   public String toString()
   {
      return "PrettyConfig [mappings=" + mappings + ", globalRewriteRules=" + globalRewriteRules + "]";
   }
}
