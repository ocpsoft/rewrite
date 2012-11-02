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
package com.ocpsoft.pretty.faces2.config.reload;

import javax.faces.application.ProjectStage;
import javax.servlet.ServletContext;

import org.ocpsoft.logging.Logger;

import com.ocpsoft.pretty.faces.spi.DevelopmentModeDetector;
import com.ocpsoft.pretty.faces.util.FacesFactory;

/**
 * Implementation of {@link DevelopmentModeDetector} that checks for the JSF project stage the application runs in. If
 * the project stage isn't set to <code>Production</code>, the PrettyFaces development mode will be enabled.
 * 
 * @author Christian Kaltepoth
 */
public class JSF2DevelopmentModeDetector implements DevelopmentModeDetector
{
   Logger log = Logger.getLogger(JSF2DevelopmentModeDetector.class);

   public int getPriority()
   {
      return 100;
   }

   public Boolean isDevelopmentMode(ServletContext servletContext)
   {
      try {
         return !ProjectStage.Production.equals(FacesFactory.getApplication().getProjectStage());
      }
      catch (Exception e) {
         log.warn("Could not determine project stage due to underlying exception.", e);
         return false;
      }
   }

}
