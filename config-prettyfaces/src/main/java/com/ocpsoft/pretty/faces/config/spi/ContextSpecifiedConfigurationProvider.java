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

package com.ocpsoft.pretty.faces.config.spi;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.PrettyException;
import com.ocpsoft.pretty.faces.config.DigesterPrettyConfigParser;
import com.ocpsoft.pretty.faces.config.PrettyConfig;
import com.ocpsoft.pretty.faces.config.PrettyConfigBuilder;
import com.ocpsoft.pretty.faces.config.PrettyConfigParser;
import com.ocpsoft.pretty.faces.spi.ConfigurationProvider;

/**
 * Loads configuration files specified in web.xml init parameter {@link PrettyContext#CONFIG_KEY}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ContextSpecifiedConfigurationProvider implements ConfigurationProvider
{
   private static final Log log = LogFactory.getLog(ContextSpecifiedConfigurationProvider.class);

   @Override
   public PrettyConfig loadConfiguration(ServletContext servletContext)
   {
      final PrettyConfigBuilder builder = new PrettyConfigBuilder();
      PrettyConfigParser configParser = new DigesterPrettyConfigParser();
      final List<String> configFilesList = getConfigFilesList(servletContext);
      for (final String configFilePath : configFilesList)
      {
         final InputStream is = servletContext.getResourceAsStream(configFilePath);
         if (is == null)
         {
            log.error("Pretty Faces config resource [" + configFilePath + "] not found.");
            continue;
         }

         log.trace("Reading config [" + configFilePath + "].");

         try
         {
            configParser.parse(builder, is);
         }
         catch (Exception e)
         {
            throw new PrettyException("Failed to parse PrettyFaces configuration from " + configFilePath, e);
         }
         finally
         {
            try
            {
               is.close();
            }
            catch (IOException ignored)
            {

            }
         }
      }

      return builder.build();
   }

   private List<String> getConfigFilesList(ServletContext context)
   {
      final String configFiles = context.getInitParameter(PrettyContext.CONFIG_KEY);
      final List<String> configFilesList = new ArrayList<String>();
      if (configFiles != null)
      {
         final StringTokenizer st = new StringTokenizer(configFiles, ",", false);
         while (st.hasMoreTokens())
         {
            final String systemId = st.nextToken().trim();

            if (DefaultXMLConfigurationProvider.DEFAULT_PRETTY_FACES_CONFIG.equals(systemId))
            {
               log.warn("The file [" + DefaultXMLConfigurationProvider.DEFAULT_PRETTY_FACES_CONFIG
                        + "] has been specified in the ["
                        + PrettyContext.CONFIG_KEY + "] context parameter of "
                        + "the deployment descriptor; this is unecessary and will be ignored.");
            }
            else
            {
               configFilesList.add(systemId);
            }
         }
      }
      return configFilesList;
   }
}
