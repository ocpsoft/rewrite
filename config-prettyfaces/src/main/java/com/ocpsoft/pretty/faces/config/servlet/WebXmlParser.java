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

package com.ocpsoft.pretty.faces.config.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import com.ocpsoft.pretty.faces.config.PrettyConfigParser;
import com.ocpsoft.pretty.faces.util.EmptyEntityResolver;

/**
 * Digester-based implementation of {@link PrettyConfigParser}.
 * 
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public class WebXmlParser
{
   private static final Log log = LogFactory.getLog(WebXmlParser.class);

   private static final String FACES_SERVLET = "javax.faces.webapp.FacesServlet";
   private static final String WEB_XML_PATH = "/WEB-INF/web.xml";
   public static final String CONFIG_FORCE_PARSING = "com.ocpsoft.pretty.DISABLE_SERVLET_3.0_SUPPORT";

   String facesMapping = null;

   public void parse(final ServletContext context) throws IOException, SAXException
   {
      /*
       * forceParsing must come first to avoid bug with WebLogic not fully implementing the servlet spec.
       */
      if (!forceParsing(context) && context.getMajorVersion() >= 3)
      {
         Map<String, ? extends ServletRegistration> servlets = context.getServletRegistrations();
         if (servlets != null)
         {
            for (ServletRegistration s : servlets.values())
            {
               if (s.getClassName().equalsIgnoreCase(FACES_SERVLET))
               {
                  Collection<String> mappings = s.getMappings();
                  if (!mappings.isEmpty())
                  {
                     facesMapping = mappings.iterator().next();
                     break;
                  }
               }
            }
         }

         if (facesMapping == null)
         {
            log.warn("Faces Servlet (javax.faces.webapp.FacesServlet) not found in web context - cannot configure PrettyFaces DynaView");
         }
      }
      else
      {
         InputStream in = context.getResourceAsStream(WEB_XML_PATH);
         if (in == null)
         {
            log.warn("No " + WEB_XML_PATH + " found - cannot configure PrettyFaces DynaView");
         }

         WebXml webXml = new WebXml();
         if (in != null)
         {
            Digester digester = getConfiguredDigester();
            digester.push(webXml);
            digester.parse(in);
            processConfig(webXml);
         }
      }

      log.trace("Completed parsing web.xml");
   }

   private boolean forceParsing(ServletContext context)
   {
      String forceParsing = context.getInitParameter(CONFIG_FORCE_PARSING);
      if ((forceParsing != null) && forceParsing.trim().equalsIgnoreCase("true"))
      {
         log.debug("Servlet 3.0 API has been disabled! PrettyFaces will parse web.xml manually.");
         return true;
      }
      return false;
   }

   private void processConfig(final WebXml webXml)
   {
      ServletDefinition facesServlet = null;
      if (webXml != null)
      {
         Iterator<ServletDefinition> si = webXml.getServlets().iterator();
         while ((facesServlet == null) && si.hasNext())
         {
            ServletDefinition servlet = si.next();
            if (FACES_SERVLET.equals(servlet.getServletClass()))
            {
               facesServlet = servlet;
               Iterator<ServletMapping> mi = webXml.getServletMappings().iterator();
               while ((facesMapping == null) && mi.hasNext())
               {
                  ServletMapping mapping = mi.next();
                  if (facesServlet.getServletName().equals(mapping.getServletName()))
                  {
                     facesMapping = mapping.getUrlPattern().trim();
                  }
               }
            }
         }
      }
   }

   private Digester getConfiguredDigester()
   {
      final Digester digester = new Digester();

      /*
       * We use the context class loader to resolve classes. This fixes
       * ClassNotFoundExceptions on Geronimo.
       */
      digester.setUseContextClassLoader(true);

      // prevent downloading of DTDs
      digester.setEntityResolver(new EmptyEntityResolver());

      digester.addObjectCreate("web-app/servlet", ServletDefinition.class);
      digester.addCallMethod("web-app/servlet/servlet-name", "setServletName", 0);
      digester.addCallMethod("web-app/servlet/servlet-class", "setServletClass", 0);
      digester.addSetNext("web-app/servlet", "addServlet");

      digester.addObjectCreate("web-app/servlet-mapping", ServletMapping.class);
      digester.addCallMethod("web-app/servlet-mapping/servlet-name", "setServletName", 0);
      digester.addCallMethod("web-app/servlet-mapping/url-pattern", "setUrlPattern", 0);
      digester.addSetNext("web-app/servlet-mapping", "addServletMapping");

      return digester;
   }

   public boolean isFacesPresent()
   {
      return facesMapping != null;
   }

   public String getFacesMapping()
   {
      if (isFacesPresent())
      {
         return facesMapping;
      }
      return "";
   }
}
