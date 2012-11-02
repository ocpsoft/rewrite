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
package com.ocpsoft.pretty.faces2.application;

import java.util.Map;
import java.util.Set;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.application.PrettyRedirector;
import com.ocpsoft.pretty.faces.config.PrettyConfig;
import com.ocpsoft.pretty.faces.config.dynaview.DynaviewEngine;
import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;
import com.ocpsoft.pretty.faces.servlet.PrettyFacesWrappedResponse;
import com.ocpsoft.pretty.faces.url.QueryString;
import com.ocpsoft.pretty.faces.url.URL;
import com.ocpsoft.pretty.faces.util.FacesNavigationURLCanonicalizer;

/**
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public class PrettyNavigationHandler extends ConfigurableNavigationHandler
{
   private static final Log log = LogFactory.getLog(PrettyNavigationHandler.class);

   private final ConfigurableNavigationHandler parent;
   private final PrettyRedirector pr = PrettyRedirector.getInstance();
   private final DynaviewEngine dynaview = new DynaviewEngine();

   public PrettyNavigationHandler(final ConfigurableNavigationHandler parent)
   {
      this.parent = parent;
   }

   @Override
   public void handleNavigation(final FacesContext context, final String fromAction, final String outcome)
   {
      log.debug("Navigation requested: fromAction [" + fromAction + "], outcome [" + outcome + "]");
      if (!pr.redirect(context, outcome))
      {
         log.debug("Not a PrettyFaces navigation string - passing control to default nav-handler");
         PrettyContext prettyContext = PrettyContext.getCurrentInstance(context);
         prettyContext.setInNavigation(true);

         String originalViewId = context.getViewRoot().getViewId();
         parent.handleNavigation(context, fromAction, outcome);
         String newViewId = context.getViewRoot().getViewId();

         /*
          * Navigation is complete if the viewId has not changed or the response is complete
          */
         if ((true == context.getResponseComplete()) || originalViewId.equals(newViewId))
         {

            prettyContext.setInNavigation(false);
         }
      }

   }

   @Override
   public NavigationCase getNavigationCase(final FacesContext context, final String fromAction, final String outcome)
   {
      PrettyContext prettyContext = PrettyContext.getCurrentInstance(context);
      PrettyConfig config = prettyContext.getConfig();
      if ((outcome != null) && PrettyContext.PRETTY_PREFIX.equals(outcome))
      {
         String viewId = context.getViewRoot().getViewId();
         NavigationCase navigationCase = parent.getNavigationCase(context, fromAction, viewId);
         return navigationCase;
      }
      else if ((outcome != null) && outcome.startsWith(PrettyContext.PRETTY_PREFIX) && config.isMappingId(outcome))
      {
         /*
          * FIXME this will not work with dynamic view IDs... figure out another solution
          * (<rewrite-view>/faces/views/myview.xhtml</rewrite-view> ? For now. Do not support it.
          */
         UrlMapping mapping = config.getMappingById(outcome);
         String viewId = mapping.getViewId();
         if (mapping.isDynaView())
         {
            viewId = dynaview.calculateDynaviewId(context, mapping);
         }
         viewId = FacesNavigationURLCanonicalizer.normalizeRequestURI(context, viewId);

         URL url = new URL(viewId);
         url.getMetadata().setLeadingSlash(true);
         QueryString qs = QueryString.build("");
         if (viewId.contains("?"))
         {
            qs.addParameters(viewId);
         }
         qs.addParameters("?" + PrettyFacesWrappedResponse.REWRITE_MAPPING_ID_KEY + "=" + mapping.getId());

         viewId = url.toString() + qs.toQueryString();

         NavigationCase navigationCase = parent.getNavigationCase(context, fromAction, viewId);
         return navigationCase;
      }
      else
      {
         NavigationCase navigationCase = parent.getNavigationCase(context, fromAction, outcome);
         return navigationCase;
      }
   }

   @Override
   public Map<String, Set<NavigationCase>> getNavigationCases()
   {
      return parent.getNavigationCases();
   }

   @Override
   public void performNavigation(final String outcome)
   {
      parent.performNavigation(outcome);
   }
}