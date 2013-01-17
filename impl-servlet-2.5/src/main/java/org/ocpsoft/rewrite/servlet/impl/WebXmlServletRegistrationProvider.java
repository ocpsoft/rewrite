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
import java.util.List;

import javax.servlet.ServletContext;

import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.servlet.ServletRegistration;
import org.ocpsoft.rewrite.servlet.spi.ServletRegistrationProvider;

/**
 * Implementation of {@link ServletRegistrationProvider} that parses <code>web.xml</code> to look for Servlets.
 * 
 * @author Christian Kaltepoth
 */
public class WebXmlServletRegistrationProvider implements ServletRegistrationProvider
{

   private final Logger log = Logger.getLogger(WebXmlServletRegistrationProvider.class);

   @Override
   public int priority()
   {
      return 20;
   }

   @Override
   public List<ServletRegistration> getServletRegistrations(ServletContext context)
   {

      InputStream webXmlStream = context.getResourceAsStream("/WEB-INF/web.xml");

      if (webXmlStream != null) {

         try {

            WebXmlServletRegistrationParser parser = new WebXmlServletRegistrationParser();
            parser.parse(webXmlStream);
            return parser.getRegistrations();

         }
         catch (IOException e) {
            log.warn("Failed to parse web.xml to look for servlet registations", e);
         }

      }

      return null;

   }

}
