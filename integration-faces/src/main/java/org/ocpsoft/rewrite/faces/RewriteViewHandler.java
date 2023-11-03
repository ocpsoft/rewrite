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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewDeclarationLanguage;
import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.common.pattern.WeightedComparator;
import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.common.util.Iterators;
import org.ocpsoft.rewrite.faces.spi.FacesActionUrlProvider;
import org.ocpsoft.urlbuilder.Address;
import org.ocpsoft.urlbuilder.AddressBuilder;
import org.ocpsoft.urlbuilder.util.Encoder;

/**
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public class RewriteViewHandler extends ViewHandlerWrapper
{
   protected ViewHandler parent;
   private final ThreadLocal<Boolean> bookmarkable = new ThreadLocal<Boolean>();
   private volatile List<FacesActionUrlProvider> providers;

   /**
    * <b>NOTE:</b> This method should only be used by the getBookmarkableURL and getActionURL methods, for the purposes
    * of rewriting form URLs (which do not include viewParameters.)
    * 
    * @return Bookmarkable state - defaults to false if not previously set;
    */
   private boolean isBookmarkable()
   {
      Boolean result = bookmarkable.get();
      if (result == null)
      {
         result = false;
         bookmarkable.set(result);
      }
      return result;
   }

   private void setBookmarkable(final boolean value)
   {
      bookmarkable.set(value);
   }

   public RewriteViewHandler(final ViewHandler viewHandler)
   {
      super();
      parent = viewHandler;
   }

   @Override
   public String getActionURL(final FacesContext context, final String viewId)
   {
      /*
       * When this method is called for forms, getBookmarkableURL is NOT called; therefore, we have a way to distinguish
       * the two.
       */
      HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
      String result = null;
      if (!isBookmarkable() && !RewriteNavigationHandler.isInNavigation(request)
               && (viewId != null)
               && viewId.equals(context.getViewRoot().getViewId()))
      {
         /*
          * When rendering a form URL, Faces only provides the bare view-id, sans any bookmarkable parameters from the request. 
          * We need to restore those ourselves. 
          */
         for (FacesActionUrlProvider provider : getProviders()) {
            result = provider.getActionURL(context, viewId);
            if (result != null)
               break;
         }

         if (result != null)
         {
            String parentActionURL = parent.getActionURL(context, viewId);
            if (parentActionURL.contains("?"))
            {
               try
               {
                   URI uri = new URI(result);
                   URI action = new URI(parentActionURL);
                   StringBuilder query = new StringBuilder(uri.getQuery());
                   if (query.length() > 0 && action.getQuery() != null) {
                       query.append('&');
                   }
                   query.append(action.getQuery());
                   
                   if (uri.getScheme() == null && uri.getHost() != null) {
                       Address address
                           = AddressBuilder.begin()
                                           .scheme(uri.getScheme())
                                           .domain(uri.getHost())
                                           .port(uri.getPort())
                                           .pathDecoded(uri.getPath())
                                           .queryLiteral(query.toString())
                                           .anchor(uri.getRawFragment())
                                           .buildLiteral();
                       
                       result = address.toString();
                   }
               }
               catch (URISyntaxException e)
               {
                   throw new IllegalArgumentException(
                           "[" + result + "] is not a valid URL fragment. Consider encoding relevant portions of the URL with ["
                                    + Encoder.class
                                    + "], or use the provided builder pattern to specify part encoding.", e);
               }
            }
         }
      }
      if (result == null)
         result = parent.getActionURL(context, viewId);
      return result;
   }

   @SuppressWarnings("unchecked")
   public List<FacesActionUrlProvider> getProviders()
   {
      List<FacesActionUrlProvider> result = providers;

      if (result == null)
      {
        synchronized (this)
        {
          result = providers;
          if (result == null)
          {
            result = Iterators.asList(ServiceLoader.load(FacesActionUrlProvider.class));
            Collections.sort(result, new WeightedComparator());
            providers = result;
          }
        }
      }
      return result;
   }

   @Override
   public String getBookmarkableURL(final FacesContext context, final String viewId,
            final Map<String, List<String>> parameters, final boolean includeViewParams)
   {
      /*
       * When this method is called for <h:link> tags, getActionURL is called as part of the parent call
       */
      setBookmarkable(true);
      String result = parent.getBookmarkableURL(context, viewId, parameters, includeViewParams);
      setBookmarkable(false);
      return result;
   }

   /**
    * Canonicalize the given viewId, then pass that viewId to the next ViewHandler in the chain.
    */
   @Override
   public String deriveViewId(final FacesContext context, final String rawViewId)
   {
      String canonicalViewId = new URLDuplicatePathCanonicalizer().canonicalize(rawViewId);
      return parent.deriveViewId(context, canonicalViewId);
   }

   @Override
   public ViewHandler getWrapped()
   {
      return parent;
   }
}
