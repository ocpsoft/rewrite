/*
 * Copyright 2011 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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
package com.ocpsoft.rewrite.config;

import java.util.Collections;
import java.util.List;

import com.ocpsoft.common.pattern.WeightedComparator;
import com.ocpsoft.common.services.ServiceLoader;
import com.ocpsoft.common.util.Iterators;
import com.ocpsoft.logging.Logger;

/**
 * Responsible for loading all {@link ConfigurationProvider} instances, and building a single unified
 * {@link Configuration} based on {@link ConfigurationProvider#priority()}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ConfigurationLoader
{
   public static Logger log = Logger.getLogger(ConfigurationLoader.class);

   /**
    * Load all {@link ConfigurationProvider} instances, sort by {@link ConfigurationProvider#priority()}, and return a
    * unified, composited {@link Configuration} object.
    */
   @SuppressWarnings({ "rawtypes", "unchecked" })
   public static Configuration loadConfiguration(final Object context)
   {
      ServiceLoader<ConfigurationProvider> loader = ServiceLoader.load(ConfigurationProvider.class);
      List<ConfigurationProvider> providers = Iterators.asList(loader.iterator());

      Collections.sort(providers, new WeightedComparator());

      ConfigurationBuilder result = ConfigurationBuilder.begin();

      for (ConfigurationProvider provider : providers) {
         if (provider.handles(context))
         {
            Configuration configuration = provider.getConfiguration(context);

            if (configuration != null)
            {
               List<Rule> rules = configuration.getRules();
               if (rules != null)
               {
                  for (Rule rule : rules) {
                     if (rule != null)
                     {
                        result.addRule(rule);
                     }
                     else {
                        log.debug("Ignoring null Rule from ConfigurationProvider [" + provider.getClass().getName()
                                 + "]");
                     }
                  }
               }
               else {
                  log.debug("Ignoring null List<Rule> from ConfigurationProvider [" + provider.getClass().getName()
                           + "]");
               }
            }
            else {
               log.debug("Ignoring null Configuration from ConfigurationProvider [" + provider.getClass().getName()
                        + "].");
            }
         }
      }

      return result;
   }
}
