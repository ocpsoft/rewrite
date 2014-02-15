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

import javax.servlet.ServletContext;

import com.ocpsoft.pretty.faces.config.PrettyConfig;
import com.ocpsoft.pretty.faces.spi.ConfigurationPostProcessor;

/**
 * Set various flags from the ServletContext into the configuration.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ContextParamsPostProcessor implements ConfigurationPostProcessor
{
   public static final String USE_ENCODE_URL_FOR_REDIRECTS = "com.ocpsoft.pretty.USE_ENCODE_URL_FOR_REDIRECTS";

   @Override
   public PrettyConfig processConfiguration(ServletContext context, PrettyConfig config)
   {
      String useEncodeURL = context.getInitParameter(USE_ENCODE_URL_FOR_REDIRECTS);
      if ((useEncodeURL != null) && "true".equalsIgnoreCase(useEncodeURL.trim()))
      {
         config.setUseEncodeUrlForRedirects(true);
      }

      return config;
   }
}
