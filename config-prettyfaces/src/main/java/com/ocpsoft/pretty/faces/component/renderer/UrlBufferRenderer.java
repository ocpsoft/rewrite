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

package com.ocpsoft.pretty.faces.component.renderer;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.PrettyException;
import com.ocpsoft.pretty.faces.component.UrlBuffer;
import com.ocpsoft.pretty.faces.config.PrettyConfig;
import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;
import com.ocpsoft.pretty.faces.util.PrettyURLBuilder;

/**
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public class UrlBufferRenderer extends Renderer
{
   public static final String RENDERER_TYPE = "com.ocpsoft.pretty.Url";
   private final PrettyURLBuilder urlBuilder = new PrettyURLBuilder();

   @Override
   public void encodeBegin(final FacesContext context, final UIComponent component) throws IOException
   {
      super.encodeBegin(context, component);
      if (!component.isRendered())
      {
         return;
      }

      UrlBuffer urlBuffer = (UrlBuffer) component;
      String var = (String) urlBuffer.getAttributes().get("var");

      String mappingId = (String) component.getAttributes().get("mappingId");
      if (mappingId == null)
      {
         throw new PrettyException("Mapping id was null when attempting to build URL for component: "
                  + component.toString() + " <" + component.getClientId(context) + ">");
      }
      
      String relative = (String) urlBuffer.getAttributes().get("relative");

      PrettyContext prettyContext = PrettyContext.getCurrentInstance(context);
      PrettyConfig prettyConfig = prettyContext.getConfig();
      UrlMapping urlMapping = prettyConfig.getMappingById(mappingId);

      String prettyHref = urlBuilder.build(urlMapping, true, urlBuilder.extractParameters(component));
      String contextPath = context.getExternalContext().getRequestContextPath();
      String href = (relative == null || "false".equals(relative)) ? contextPath + prettyHref : prettyHref;

      context.getExternalContext().getRequestMap().put(var, href);
   }
}