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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ocpsoft.rewrite.pattern.WeightedComparator;
import com.ocpsoft.rewrite.services.ServiceLoader;
import com.ocpsoft.rewrite.util.Iterators;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ConfigurationLoader
{
   public static Configuration loadConfiguration()
   {
      @SuppressWarnings("unchecked")
      ServiceLoader<ConfigurationProvider> loader = ServiceLoader.load(ConfigurationProvider.class);
      List<ConfigurationProvider> providers = Iterators.asList(loader.iterator());

      Collections.sort(providers, new WeightedComparator());

      List<Configuration> configs = new ArrayList<Configuration>();
      for (ConfigurationProvider provider : providers) {
         configs.add(provider.getConfiguration());
      }

      ConfigurationBuilder result = ConfigurationBuilder.begin();

      for (Configuration configuration : configs) {
         for (Rule rule : configuration.getRules()) {
            result.addRule(rule);
         }
      }

      return result;
   }
}
