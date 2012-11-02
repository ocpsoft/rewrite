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
package com.ocpsoft.pretty.faces.config.reload;

import javax.servlet.ServletContext;

import com.ocpsoft.pretty.faces.spi.DevelopmentModeDetector;

/**
 * Implementation of {@link DevelopmentModeDetector} that reads the servlet context parameter
 * <code>com.ocpsoft.pretty.DEVELOPMENT</code>. Setting this parameter to <code>true</code> will enable the PrettyFaces
 * development mode. Setting it to <code>false</code> will disable it and prevent all other detectors from enabling it.
 * 
 * @author Christian Kaltepoth
 */
public class DefaultDevelopmentModeDetector implements DevelopmentModeDetector
{

   /**
    * The servlet context parameter
    */
   private static final String CONTEXT_PARAM_DEVELOPMENT = "com.ocpsoft.pretty.DEVELOPMENT";

   public int getPriority()
   {
      return 50;
   }

   public Boolean isDevelopmentMode(ServletContext servletContext)
   {

      String value = servletContext.getInitParameter(CONTEXT_PARAM_DEVELOPMENT);

      // development mode enabled
      if (value != null && "true".equalsIgnoreCase(value.trim())) {
         return true;
      }

      // disable the development mode
      if (value != null && "false".equalsIgnoreCase(value.trim())) {
         return false;
      }

      // we don't know
      return null;

   }

}
