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

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.component.UIViewParameter;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewDeclarationLanguage;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.beans.ExtractedValuesURLBuilder;
import com.ocpsoft.pretty.faces.config.mapping.PathParameter;
import com.ocpsoft.pretty.faces.config.mapping.QueryParameter;
import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;
import com.ocpsoft.pretty.faces.util.FacesElUtils;
import com.ocpsoft.pretty.faces.util.URLDuplicatePathCanonicalizer;

/**
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public class PrettyViewHandler extends ViewHandler
{
   protected ViewHandler parent;
   private static FacesElUtils elUtils = new FacesElUtils();
   private final ThreadLocal<Boolean> bookmarkable = new ThreadLocal<Boolean>();

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

   @Override
   public String deriveLogicalViewId(final FacesContext context, final String rawViewId)
   {
      return parent.deriveLogicalViewId(context, rawViewId);
   }

   private void setBookmarkable(final boolean value)
   {
      bookmarkable.set(value);
   }

   public PrettyViewHandler(final ViewHandler viewHandler)
   {
      super();
      parent = viewHandler;
   }

   @Override
   public Locale calculateLocale(final FacesContext facesContext)
   {
      return parent.calculateLocale(facesContext);
   }

   @Override
   public String calculateRenderKitId(final FacesContext facesContext)
   {
      return parent.calculateRenderKitId(facesContext);
   }

   @Override
   public UIViewRoot createView(final FacesContext context, final String viewId)
   {
      // TODO move DynaView intercepter here
      UIViewRoot view = parent.createView(context, viewId);
      // addPrettyViewParameters(context, view);
      return view;
   }

   @Override
   public UIViewRoot restoreView(final FacesContext context, final String viewId)
   {
      // TODO move DynaView intercepter here
      UIViewRoot view = parent.restoreView(context, viewId);
      // addPrettyViewParameters(context, view);
      return view;
   }

   @Override
   public String getActionURL(final FacesContext context, final String viewId)
   {
      /*
       * When this method is called for forms, getBookmarkableURL is NOT called; therefore, we have a way to distinguish
       * the two.
       */
      String result = parent.getActionURL(context, viewId);
      PrettyContext prettyContext = PrettyContext.getCurrentInstance(context);
      if (!isBookmarkable() && prettyContext.isPrettyRequest() && !prettyContext.isInNavigation() && (viewId != null)
               && viewId.equals(context.getViewRoot().getViewId()))
      {
         ExtractedValuesURLBuilder builder = new ExtractedValuesURLBuilder();
         UrlMapping mapping = prettyContext.getCurrentMapping();
         result = prettyContext.getContextPath() + builder.buildURL(mapping) + builder.buildQueryString(mapping);
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

   @Override
   public String getRedirectURL(final FacesContext context, final String viewId,
            final Map<String, List<String>> parameters, final boolean includeViewParams)
   {
      return parent.getRedirectURL(context, viewId, parameters, includeViewParams);
   }

   @Override
   public String getResourceURL(final FacesContext facesContext, final String path)
   {
      return parent.getResourceURL(facesContext, path);
   }

   @Override
   public void renderView(final FacesContext facesContext, final UIViewRoot viewRoot) throws IOException,
            FacesException
   {
      parent.renderView(facesContext, viewRoot);
   }

   @Override
   public void writeState(final FacesContext facesContext) throws IOException
   {
      parent.writeState(facesContext);
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
   public String calculateCharacterEncoding(final FacesContext context)
   {
      return parent.calculateCharacterEncoding(context);
   }

   @Override
   public ViewDeclarationLanguage getViewDeclarationLanguage(final FacesContext context, final String viewId)
   {
      return parent.getViewDeclarationLanguage(context, viewId);
   }

   @Override
   public void initView(final FacesContext context) throws FacesException
   {
      parent.initView(context);
   }

   /*
    * PrettyFaces specific methods
    */

   /**
    * Add PrettyFaces UIViewParameters to the component tree. This is how we do value injection, conversion, and
    * validation.
    */
   private void addPrettyViewParameters(final FacesContext context, final UIViewRoot view)
   {
      PrettyContext prettyContext = PrettyContext.getCurrentInstance(context);
      if (prettyContext.isPrettyRequest())
      {
         UIComponent metadata = view.getFacet(UIViewRoot.METADATA_FACET_NAME);
         if (metadata == null)
         {
            metadata = context.getApplication().createComponent(UIPanel.COMPONENT_TYPE);
            view.getFacets().put(UIViewRoot.METADATA_FACET_NAME, metadata);
         }

         List<UIComponent> children = metadata.getChildren();

         List<PathParameter> pathParameters = prettyContext.getCurrentMapping().getPatternParser().getPathParameters();
         for (PathParameter p : pathParameters)
         {
            UIViewParameter param = (UIViewParameter) context.getApplication().createComponent(
                     UIViewParameter.COMPONENT_TYPE);

            Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
            requestMap.put(p.getName(), p.getValue());
            param.setName(p.getName());

            ValueExpression ve = elUtils.createValueExpression(context, p.getExpression().getELExpression());
            param.setValueExpression("value", ve);
            param.setImmediate(true);
            children.add(param);
         }

         List<QueryParameter> queryParams = prettyContext.getCurrentMapping().getQueryParams();
         for (QueryParameter q : queryParams)
         {
            UIViewParameter param = new UIViewParameter();
            param.setName(q.getName());

            ValueExpression ve = elUtils.createValueExpression(context, q.getExpression().getELExpression());
            param.setValueExpression("value", ve);
            param.setImmediate(true);
            children.add(param);
         }
      }
   }
}