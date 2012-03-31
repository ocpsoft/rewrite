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
package org.ocpsoft.rewrite.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ocpsoft.common.pattern.WeightedComparator;
import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.common.util.Iterators;
import org.ocpsoft.logging.Logger;

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

      Map<Integer, List<Rule>> priorityMap = new HashMap<Integer, List<Rule>>();
      
      Collections.sort(providers, new WeightedComparator());

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
                        if(rule instanceof RelocatableRule)
                           addListValue(priorityMap, ((RelocatableRule) rule).priority(), rule);
                        else
                           addListValue(priorityMap, provider.priority(), rule);
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

      ConfigurationBuilder result = ConfigurationBuilder.begin();
      
      ArrayList<Integer> sortedKeys = new ArrayList<Integer>(priorityMap.keySet());
      Collections.sort(sortedKeys);
      
      for (Integer integer : sortedKeys) {
         List<Rule> list = priorityMap.get(integer);
         for (Rule rule : list) {
            result.addRule(rule);
         }
      }
      
      return result;
   }
   
   @SuppressWarnings("unchecked")
   public static <K, T> void addListValue(final Map<K, List<T>> map, final K key, final T value)
   {
      if (!map.containsKey(key))
      {
         map.put(key, new ArrayList<T>(Arrays.asList(value)));
      }
      else
      {
         map.get(key).add(value);
      }
   }

}
