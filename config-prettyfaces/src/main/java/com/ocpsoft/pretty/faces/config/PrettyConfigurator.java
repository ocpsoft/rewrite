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
package com.ocpsoft.pretty.faces.config;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ocpsoft.common.services.ServiceLoader;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.PrettyException;
import com.ocpsoft.pretty.faces.config.dynaview.DynaviewEngine;
import com.ocpsoft.pretty.faces.config.servlet.WebXmlParser;
import com.ocpsoft.pretty.faces.config.spi.ParentingPostProcessor;
import com.ocpsoft.pretty.faces.config.spi.ValidatingPostProcessor;
import com.ocpsoft.pretty.faces.spi.ConfigurationPostProcessor;
import com.ocpsoft.pretty.faces.spi.ConfigurationProvider;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com>Lincoln Baxter, III</a>
 * @author Aleksei Valikov
 */
public class PrettyConfigurator
{

   private static final Log log = LogFactory.getLog(PrettyConfigurator.class);

   private final ServletContext servletContext;

   private final WebXmlParser webXmlParser = new WebXmlParser();
   private final DynaviewEngine dynaview = new DynaviewEngine();

   private PrettyConfig config;

   public PrettyConfigurator(final ServletContext servletContext)
   {
      this.servletContext = servletContext;
   }

   public void configure()
   {
      try
      {
         final PrettyConfigBuilder builder = new PrettyConfigBuilder();

         ServiceLoader<?> configLoader = ServiceLoader.load(ConfigurationProvider.class);
         for (Object p : configLoader)
         {
            builder.addFromConfig(((ConfigurationProvider) p).loadConfiguration(servletContext));
         }

         config = builder.build();
         config.setDynaviewId(getFacesDynaViewId());

         /*
          * Do the built-in post-processing manually to ensure ordering
          */
         ConfigurationPostProcessor parenting = new ParentingPostProcessor();

         config = parenting.processConfiguration(servletContext, config);

         ServiceLoader<?> postProcessors = ServiceLoader.load(ConfigurationPostProcessor.class);
         for (Object p : postProcessors)
         {
            config = ((ConfigurationPostProcessor) p).processConfiguration(servletContext, config);
         }

         ConfigurationPostProcessor validating = new ValidatingPostProcessor();
         config = validating.processConfiguration(servletContext, config);

         log.trace("Setting config into ServletContext");
         servletContext.setAttribute(PrettyContext.CONFIG_KEY, config);
      }
      catch (Exception e)
      {
         throw new PrettyException("Failed to load configuration.", e);
      }
   }

   private String getFacesDynaViewId()
   {
      try
      {
         webXmlParser.parse(servletContext);
         return dynaview.buildDynaViewId(webXmlParser.getFacesMapping());
      }
      catch (Exception e)
      {
         throw new PrettyException("Could not retrieve DynaViewId.", e);
      }
   }

   public PrettyConfig getConfig()
   {
      return config;
   }

}
