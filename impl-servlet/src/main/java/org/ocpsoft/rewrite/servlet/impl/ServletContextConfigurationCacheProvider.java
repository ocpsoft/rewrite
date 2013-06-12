/*
 * Copyright 2013 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationCacheProvider;
import org.ocpsoft.rewrite.spi.ConfigurationCacheProvider;

/**
 * Default implementation of {@link ConfigurationCacheProvider} that uses the {@link ServletContext} as an
 * application-scoped storage medium. By default, the {@link Configuration} will be loaded once upon application
 * startup, and never again.
 * <p>
 * To disable the default caching mechanism, and enable {@link Configuration} reloading on each request, add the
 * following servlet context init parameter to <code>web.xml</code>:
 * 
 * <pre>
 * &lt;context-param&gt;
 *   &lt;param-name&gt;org.ocpsoft.rewrite.config.CONFIG_RELOADING&lt;/param-name&gt;
 *   &lt;param-value&gt;true&lt;/param-value&gt;
 * &lt;/context-param&gt;
 * </pre>
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ServletContextConfigurationCacheProvider extends HttpConfigurationCacheProvider
{
   private static final String KEY = ServletContextConfigurationCacheProvider.class.getName() + "_cachedConfig";
   private static final String RELOAD_CONFIGURATION = "org.ocpsoft.rewrite.config.CONFIG_RELOADING";

   @Override
   public Configuration getConfiguration(ServletContext context)
   {
      String reload = context.getInitParameter(RELOAD_CONFIGURATION);
      if (reload != null && "true".equalsIgnoreCase(reload.trim())) {
         return null;
      }
      return (Configuration) context.getAttribute(KEY);
   }

   @Override
   public void setConfiguration(ServletContext context, Configuration configuration)
   {
      context.setAttribute(KEY, configuration);
   }

   @Override
   public int priority()
   {
      return 0;
   }
}
