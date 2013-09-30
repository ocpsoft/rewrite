/*
 * Copyright 2011 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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
package org.ocpsoft.rewrite.servlet.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.ocpsoft.rewrite.servlet.ServletRegistration;

class WebXmlServletRegistrationParser
{

   private final List<ServletRegistration> registrations = new ArrayList<ServletRegistration>();

   private final Stack<String> stack = new Stack<String>();

   private List<ServletEntry> servlets = new ArrayList<ServletEntry>();

   private Map<String, ServletMappingEntry> servletMappings = new LinkedHashMap<String, ServletMappingEntry>();

   private ServletEntry currentServlet;

   private ServletMappingEntry currentServletMapping;

   public void parse(InputStream stream) throws IOException
   {

      try {

         XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
         XMLEventReader reader = xmlInputFactory.createXMLEventReader(stream);

         // parse the complete document
         while (reader.hasNext()) {

            XMLEvent event = reader.nextEvent();

            // this events means we can abort
            if (event.isEndDocument()) {
               break;
            }

            // we need the element name on the stack before processing the event
            if (event.isStartElement()) {
               stack.push(event.asStartElement().getName().getLocalPart());
            }

            // any event processing is done in this method
            handleEvent(event);

            // we can remove the last element from the stack after the event has been processed
            if (event.isEndElement()) {
               stack.pop();
            }

         }

         // each ServletEntry creates one registration
         for (ServletEntry servletEntry : servlets) {

            ServletRegistration reg = new ServletRegistration();
            reg.setClassName(servletEntry.servletClass);

            // we can lookup the mappings from the mappings map
            ServletMappingEntry mapping = servletMappings.get(servletEntry.servletName);
            if (mapping != null) {
               reg.addMappings(mapping.mappings);
            }

            registrations.add(reg);

         }

      }
      catch (XMLStreamException e) {
         throw new IOException(e);
      }

   }

   private void handleEvent(XMLEvent event)
   {

      /*
       * Parse <servlet> entries
       */

      if (isParsePosition("web-app", "servlet")) {
         if (event.isStartElement()) {
            currentServlet = new ServletEntry();
         }
         if (event.isEndElement()) {
            servlets.add(currentServlet);
            currentServlet = null;
         }
      }
      if (event.isCharacters() && isParsePosition("web-app", "servlet", "servlet-name")) {
         currentServlet.servletName = event.asCharacters().getData().trim();
      }
      if (event.isCharacters() && isParsePosition("web-app", "servlet", "servlet-class")) {
         currentServlet.servletClass = event.asCharacters().getData().trim();
      }

      /*
       * Parse <servlet-mapping> entries
       */

      if (isParsePosition("web-app", "servlet-mapping")) {
         if (event.isStartElement()) {
            currentServletMapping = new ServletMappingEntry();
         }
         if (event.isEndElement() && isNotBlank(currentServletMapping.servletName)) {
            String key = currentServletMapping.servletName.trim();
            ServletMappingEntry existingMapping = servletMappings.get(key);
            if (existingMapping != null) {
               existingMapping.mappings.addAll(currentServletMapping.mappings);
            }
            else {
               servletMappings.put(key, currentServletMapping);
            }
            currentServletMapping = null;
         }
      }
      if (event.isCharacters() && isParsePosition("web-app", "servlet-mapping", "servlet-name")) {
         currentServletMapping.servletName = event.asCharacters().getData().trim();
      }
      if (event.isCharacters() && isParsePosition("web-app", "servlet-mapping", "url-pattern")) {
         currentServletMapping.mappings.add(event.asCharacters().getData().trim());
      }

   }

   private static boolean isNotBlank(String s)
   {
      return s != null && s.trim().length() > 0;
   }

   private boolean isParsePosition(String... path)
   {
      if (path.length != stack.size()) {
         return false;
      }
      for (int i = 0; i < stack.size(); i++) {
         if (!path[i].equals(stack.get(i))) {
            return false;
         }
      }
      return true;
   }

   public List<ServletRegistration> getRegistrations()
   {
      return registrations;
   }

   private class ServletEntry
   {
      public String servletName;
      public String servletClass;
   }

   private class ServletMappingEntry
   {
      public String servletName;
      public final List<String> mappings = new ArrayList<String>();
   }

}
