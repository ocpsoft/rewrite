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
import java.util.Collection;

import jakarta.faces.webapp.FacesServlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

/**
 * This class is used to detect the mapping of the {@link FacesServlet}.
 * This class should not be removed, because 3rd party frameworks like Seam
 * Faces are using it.
 * 
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 * @author Christian Kaltepoth
 */
public class WebXmlParser
{
   private static final Log log = LogFactory.getLog(WebXmlParser.class);

   private static final String FACES_SERVLET = "jakarta.faces.webapp.FacesServlet";

   private String facesMapping = null;

   public void parse(final ServletContext context) throws IOException, SAXException {

      for (ServletRegistration s : context.getServletRegistrations().values()) {
         if (s.getClassName() != null && s.getClassName().equalsIgnoreCase(FACES_SERVLET)) {
            Collection<String> mappings = s.getMappings();
            if (!mappings.isEmpty()) {
               facesMapping = mappings.iterator().next();
               break;
            }
         }
      }

      if (facesMapping == null) {
         log.warn("Faces Servlet (jakarta.faces.webapp.FacesServlet) not found in web context - cannot configure PrettyFaces DynaView");
      }

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
