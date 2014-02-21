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
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;

import javax.servlet.ServletContext;

import org.xml.sax.SAXException;

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
public class ClassLoaderConfigurationProvider implements ConfigurationProvider
{
   public static final String PRETTY_CONFIG_RESOURCE = "META-INF/pretty-config.xml";
   public static final String CLASSPATH_CONFIG_ENABLED = "com.ocpsoft.pretty.LOAD_CLASSPATH_CONFIG";

   @Override
   public PrettyConfig loadConfiguration(ServletContext context)
   {
      String enabled = context.getInitParameter(CLASSPATH_CONFIG_ENABLED);
      if ((enabled != null) && "false".equalsIgnoreCase(enabled.trim()))
      {
         return null;
      }

      final PrettyConfigBuilder builder = new PrettyConfigBuilder();
      PrettyConfigParser configParser = new DigesterPrettyConfigParser();
      try
      {
         final Enumeration<URL> urls = getClass().getClassLoader().getResources(PRETTY_CONFIG_RESOURCE);
         if (urls != null)
         {
            while (urls.hasMoreElements())
            {
               final URL url = urls.nextElement();
               if (url != null)
               {
                  InputStream is = null;
                  try
                  {
                     is = openStream(url);
                     try
                     {
                        configParser.parse(builder, is);
                     }
                     catch (SAXException e)
                     {
                        throw new PrettyException("Failed to parse PrettyFaces configuration from URL:" + url, e);
                     }
                  }
                  finally
                  {
                     if (is != null)
                     {
                        is.close();
                     }
                  }
               }
            }
         }
      }
      catch (Exception e)
      {
         throw new PrettyException("Could not get references to PrettyFaces ClassLoader-configuration elements.", e);
      }
      return builder.build();
   }

   private InputStream openStream(final URL url) throws IOException
   {
      final URLConnection connection = url.openConnection();
      connection.setUseCaches(false);
      return connection.getInputStream();
   }

}
