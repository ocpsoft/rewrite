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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ocpsoft.common.services.ServiceLoader;

import com.ocpsoft.pretty.faces.config.PrettyConfigurator;
import com.ocpsoft.pretty.faces.spi.DevelopmentModeDetector;

/**
 * This class reloads the PrettyFaces configuration if PrettyFaces runs in development mode.
 * 
 * @author Christian Kaltepoth
 */
public class PrettyConfigReloader
{

   private final Log log = LogFactory.getLog(this.getClass());

   /**
    * The configuration is reloaded after this amount of time
    */
   private final static long CONFIG_RELOAD_DELAY = 2000l;

   /**
    * Keeps track of the last time the configuration was updated
    */
   private long lastUpdate = 0;

   /**
    * The development mode will be lazily detected
    */
   private Boolean developmentMode = null;

   public void reloadIfNecessary(ServletContext servletContext)
   {
      // the development mode detection is started when the first request is received
      if (developmentMode == null) {

         developmentMode = isDevelopmentModeActive(servletContext);

         if (log.isDebugEnabled()) {
            if (developmentMode) {
               log.debug("PrettyFaces development mode detected! Configuration reloading will be enabled.");
            }
            else {
               log.debug("No development mode detected. Configuration reloading gets disabled.");
            }
         }

      }

      // reloading is only done if in development mode
      if (developmentMode != null && developmentMode.booleanValue()) {

         // the point in time the configuration will be reloaded
         long nextUpdate = lastUpdate + CONFIG_RELOAD_DELAY;

         if (System.currentTimeMillis() > nextUpdate)
         {

            if (log.isDebugEnabled()) {
               log.debug("Reloading PrettyFaces configuration...");
            }

            /*
             * first update the 'lastUpdate' so that concurrent requests won't
             * also do an update of the configuration.
             */
            lastUpdate = System.currentTimeMillis();

            // run the configuration procedure again
            PrettyConfigurator configurator = new PrettyConfigurator(servletContext);
            configurator.configure();

         }
      }

   }

   /**
    * Detects the PrettyFaces development mode using the {@link DevelopmentModeDetector} SPI.
    */
   private boolean isDevelopmentModeActive(ServletContext servletContext)
   {

      // create the ServiceLoader for the SPI
      ServiceLoader<DevelopmentModeDetector> serviceLoader = ServiceLoader.loadTypesafe(DevelopmentModeDetector.class);

      // we need a list to be able to sort it
      List<DevelopmentModeDetector> detectors = new ArrayList<DevelopmentModeDetector>();
      for (DevelopmentModeDetector detector : serviceLoader) {
         detectors.add(detector);
      }

      // sort them by priority
      Collections.sort(detectors, new Comparator<DevelopmentModeDetector>() {
         public int compare(DevelopmentModeDetector left, DevelopmentModeDetector right)
         {
            return left.getPriority() - right.getPriority();
         }
      });

      // process the detectors until one returns a result != null
      for (DevelopmentModeDetector detector : detectors) {

         Boolean result = detector.isDevelopmentMode(servletContext);

         if (log.isDebugEnabled()) {
            log.debug("Detector " + detector.getClass().getSimpleName() + " returned: " + result);
         }

         if (result != null) {
            return result.booleanValue();
         }

      }

      // default value
      return false;

   }

}
