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
package com.ocpsoft.pretty.faces.spi;

import javax.servlet.ServletContext;

/**
 * This SPI is used by PrettyFaces to detect whether it should run in development mode.
 * 
 * @author Christian Kaltepoth
 * 
 */
public interface DevelopmentModeDetector
{

   /**
    * Detectors with a higher priority value will run after detectors with a lower priority value.
    */
   int getPriority();

   /**
    * This method must return <code>true</code> if it wants PrettyFaces to run in development mode. It must return
    * <code>false</code> if it doesn't want PrettyFaces to run in development mode. If the method doesn't want to have
    * any infuence on the development mode detection, it should return <code>null</code>.
    * 
    * @param servletContext The {@link ServletContext}
    * @return The detector's result
    */
   Boolean isDevelopmentMode(ServletContext servletContext);

}
