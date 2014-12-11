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
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.PrettyException;
import com.ocpsoft.pretty.faces.component.Link;
import com.ocpsoft.pretty.faces.config.PrettyConfig;
import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;
import com.ocpsoft.pretty.faces.util.PrettyURLBuilder;

/**
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public class LinkRenderer extends Renderer
{
   public static final String RENDERER_TYPE = "javax.faces.Link";
   protected final PrettyURLBuilder urlBuilder = new PrettyURLBuilder();

   @Override
   public void encodeBegin(final FacesContext context, final UIComponent component) throws IOException
   {
      super.encodeBegin(context, component);
      if (!component.isRendered())
      {
         return;
      }

      Link link = (Link) component;
      ResponseWriter writer = context.getResponseWriter();

      if (link.isDisabled())
      {
         writer.startElement("span", link);
      }
      else
      {
         writer.startElement("a", link);
      }

      String mappingId = (String) component.getAttributes().get("mappingId");
      if (mappingId == null)
      {
         throw new PrettyException("Mapping id was null when attempting to build URL for component: "
                  + component.toString() + " <" + component.getClientId(context) + ">");
      }

      try
      {

         PrettyContext prettyContext = PrettyContext.getCurrentInstance(context);
         PrettyConfig prettyConfig = prettyContext.getConfig();
         UrlMapping urlMapping = prettyConfig.getMappingById(mappingId);

         String href = buildUrl(context, component, urlMapping);

         if ((link.getAnchor() != null) && link.getAnchor().length() > 0)
         {
            href += "#" + link.getAnchor();
         }

         writer.writeURIAttribute("href", context.getExternalContext().encodeResourceURL(href), "href");

      }
      catch (PrettyException e)
      {
         throw new PrettyException("Failed to build URL for mapping '" + mappingId
               + "' while rendering <pretty:link> component: " + link.getClientId(context), e);
      }

      writeAttr(writer, "id", link.getClientId(context));
      writeAttr(writer, "accesskey", link.getAccesskey());
      writeAttr(writer, "charset", link.getCharset());
      writeAttr(writer, "coords", link.getCoords());
      writeAttr(writer, "dir", link.getDir());
      writeAttr(writer, "hreflang", link.getHreflang());
      writeAttr(writer, "lang", link.getLang());
      writeAttr(writer, "onblur", link.getOnblur());
      writeAttr(writer, "onclick", link.getOnclick());
      writeAttr(writer, "ondblclick", link.getOndblclick());
      writeAttr(writer, "onfocus", link.getOnfocus());
      writeAttr(writer, "onkeydown", link.getOnkeydown());
      writeAttr(writer, "onkeypress", link.getOnkeypress());
      writeAttr(writer, "onkeyup", link.getOnkeyup());
      writeAttr(writer, "onmousedown", link.getOnmousedown());
      writeAttr(writer, "onmousemove", link.getOnmousemove());
      writeAttr(writer, "onmouseout", link.getOnmouseout());
      writeAttr(writer, "onmouseover", link.getOnmouseover());
      writeAttr(writer, "onmouseup", link.getOnmouseup());
      writeAttr(writer, "rel", link.getRel());
      writeAttr(writer, "rev", link.getRev());
      writeAttr(writer, "shape", link.getShape());
      writeAttr(writer, "style", link.getStyle());
      writeAttr(writer, "class", link.getStyleClass());
      writeAttr(writer, "tabindex", link.getTabindex());
      writeAttr(writer, "target", link.getTarget());
      writeAttr(writer, "title", link.getTitle());
      writeAttr(writer, "type", link.getType());

   }

   protected String buildUrl(FacesContext context, UIComponent component, UrlMapping urlMapping) {
      return context.getExternalContext().getRequestContextPath()
                 + urlBuilder.build(urlMapping, true, urlBuilder.extractParameters(component));
   }

   private void writeAttr(final ResponseWriter writer, final String name, final String value) throws IOException
   {
      if (value != null)
      {
         writer.writeAttribute(name, value, name);
      }
   }

   @Override
   public void encodeEnd(final FacesContext context, final UIComponent component) throws IOException
   {
      Link link = (Link) component;
      super.encodeEnd(context, link);

      if (!link.isRendered())
      {
         return;
      }

      ResponseWriter writer = context.getResponseWriter();

      if (link.isDisabled())
      {
         writer.endElement("span");
      }
      else
      {
         writer.endElement("a");
      }
   }
}