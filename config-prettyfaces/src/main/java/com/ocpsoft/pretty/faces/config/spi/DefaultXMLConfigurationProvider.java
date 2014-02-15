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

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ocpsoft.pretty.PrettyException;
import com.ocpsoft.pretty.faces.config.DigesterPrettyConfigParser;
import com.ocpsoft.pretty.faces.config.PrettyConfig;
import com.ocpsoft.pretty.faces.config.PrettyConfigBuilder;
import com.ocpsoft.pretty.faces.config.PrettyConfigParser;
import com.ocpsoft.pretty.faces.spi.ConfigurationProvider;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class DefaultXMLConfigurationProvider implements ConfigurationProvider
{
   private static final Log log = LogFactory.getLog(DefaultXMLConfigurationProvider.class);

   public static final String DEFAULT_PRETTY_FACES_CONFIG = "/WEB-INF/pretty-config.xml";

   @Override
   public PrettyConfig loadConfiguration(ServletContext servletContext)
   {
      final PrettyConfigBuilder builder = new PrettyConfigBuilder();
      PrettyConfigParser configParser = new DigesterPrettyConfigParser();
      final InputStream is = servletContext.getResourceAsStream(DEFAULT_PRETTY_FACES_CONFIG);
      if (is != null)
      {
         log.trace("Reading config [" + DEFAULT_PRETTY_FACES_CONFIG + "].");

         try
         {
            configParser.parse(builder, is);
         }
         catch (Exception e)
         {
            throw new PrettyException("Failed to parse PrettyFaces configuration from " + DEFAULT_PRETTY_FACES_CONFIG,
                     e);
         }
         finally
         {
            try
            {
               is.close();
            }
            catch (IOException ignored)
            {}
         }
      }

      return builder.build();
   }
}
