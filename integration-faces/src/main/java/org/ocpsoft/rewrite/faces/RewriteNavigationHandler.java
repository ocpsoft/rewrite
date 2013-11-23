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
package org.ocpsoft.rewrite.faces;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.rewrite.exception.RewriteException;
import org.ocpsoft.urlbuilder.AddressBuilder;

/**
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public class RewriteNavigationHandler extends ConfigurableNavigationHandler
{
   private static final String REWRITE_PREFIX = "rewrite:";
   public static final String REDIRECT_PREFIX = "rewrite-redirect:";
   private static final String IN_NAVIGATION = RewriteNavigationHandler.class.getName() + "_inNavigation";
   private final ConfigurableNavigationHandler parent;

   public RewriteNavigationHandler(final ConfigurableNavigationHandler parent)
   {
      this.parent = parent;
   }

   @Override
   public void handleNavigation(final FacesContext context, final String fromAction, final String outcome)
   {
      if (!redirect(context, outcome))
      {
         setInNavigation((HttpServletRequest) context.getExternalContext().getRequest(), true);

         /*
          * the view root may be null if the navigation occurs before RESTORE_VIEW
          * 
          * https://github.com/ocpsoft/rewrite/issues/142
          */
         String originalViewId = null;
         if(context.getViewRoot() != null)
         {
           originalViewId = context.getViewRoot().getViewId();
         }
         
         parent.handleNavigation(context, fromAction, outcome);

         /*
          * the view root may be null as a result of a ViewExpiredException
          * 
          * https://github.com/ocpsoft/rewrite/issues/103
          */
         String newViewId = null;
         if (context.getViewRoot() != null)
         {
            newViewId = context.getViewRoot().getViewId();
         }

         
         /*
          * Navigation is complete if the viewId has not changed or the response is complete
          */
         if ((true == context.getResponseComplete()) || (originalViewId == newViewId) || (originalViewId != null && originalViewId.equals(newViewId)))
         {
            setInNavigation((HttpServletRequest) context.getExternalContext().getRequest(), false);
         }
      }
   }

   private void setInNavigation(final HttpServletRequest request, final boolean inNavigation)
   {
      request.setAttribute(IN_NAVIGATION, inNavigation);
   }

   public static boolean isInNavigation(final HttpServletRequest request)
   {
      Boolean inNavigation = (Boolean) request.getAttribute(IN_NAVIGATION);
      return inNavigation == null ? false : inNavigation;
   }

   @Override
   public NavigationCase getNavigationCase(final FacesContext context, final String fromAction, final String outcome)
   {
      // TODO integrate rewrite with navigation (See PrettyNavigationHandler)
      if (REWRITE_PREFIX.equals(outcome))
      {
         String viewId = context.getViewRoot().getViewId();
         NavigationCase navigationCase = parent.getNavigationCase(context, fromAction, viewId);
         return navigationCase;
      }
      else if (outcome != null && outcome.startsWith(REDIRECT_PREFIX)) {
         String url = outcome.substring(REDIRECT_PREFIX.length());
         String viewId = AddressBuilder.create(url).getPath();
         return parent.getNavigationCase(context, fromAction, viewId);
      }
      else
      {
         NavigationCase navigationCase = parent.getNavigationCase(context, fromAction, outcome);
         return navigationCase;
      }
   }

   private boolean redirect(final FacesContext context, final String outcome)
   {
      ExternalContext externalContext = context.getExternalContext();

      if (REWRITE_PREFIX.equals(outcome))
      {
         String target = FacesRewriteLifecycleListener.getOriginalRequestURL((HttpServletRequest) externalContext
                  .getRequest());
         String redirectUrl = externalContext.encodeRedirectURL(target, null);
         try {
            externalContext.redirect(redirectUrl);
            return true;
         }
         catch (IOException e) {
            throw new RewriteException("Could not redirect to [" + redirectUrl + "]", e);
         }
      }

      // outcomes created by Navigate for redirects
      else if (outcome != null && outcome.startsWith(REDIRECT_PREFIX)) {

         // strip the prefix to get the context-relative URL
         String relativeUrl = outcome.substring(REDIRECT_PREFIX.length());

         // add the context path
         String absoluteUrl = prependContextPath(externalContext, relativeUrl);

         // rewrite the URL
         String rewrittenUrl = externalContext.encodeActionURL(absoluteUrl);

         // send the redirect
         try {
            externalContext.redirect(rewrittenUrl);
            return true;
         }
         catch (IOException e) {
            throw new RewriteException("Could not redirect to [" + rewrittenUrl + "]", e);
         }

      }

      return false;
   }

   /**
    * Adds the context path to the given context-relative URL.
    */
   private String prependContextPath(ExternalContext externalContext, String url)
   {
      String contextPath = externalContext.getRequestContextPath();
      if ("/".equals(contextPath) || (url.startsWith(contextPath))) {
         return url;
      }
      return contextPath + url;
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