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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ocpsoft.common.pattern.WeightedComparator;
import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.common.util.Iterators;
import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.bind.Evaluation;
import org.ocpsoft.rewrite.context.Context;
import org.ocpsoft.rewrite.param.ConfigurableParameter;
import org.ocpsoft.rewrite.param.DefaultParameter;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.ParameterizedRule;
import org.ocpsoft.rewrite.spi.ConfigurationCacheProvider;
import org.ocpsoft.rewrite.util.Visitor;

/**
 * Responsible for loading all {@link ConfigurationProvider} instances, and building a single unified
 * {@link Configuration} based on {@link ConfigurationProvider#priority()}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ConfigurationLoader
{
   public static Logger log = Logger.getLogger(ConfigurationLoader.class);
   private final List<ConfigurationCacheProvider<?>> caches;
   private final List<ConfigurationProvider<?>> providers;

   @SuppressWarnings({ "unchecked" })
   public ConfigurationLoader(Object context)
   {
      caches = Iterators.asList(ServiceLoader.load(ConfigurationCacheProvider.class));
      Collections.sort(caches, new WeightedComparator());

      providers = Iterators.asList(ServiceLoader.load(ConfigurationProvider.class));
      Collections.sort(providers, new WeightedComparator());
   }

   /**
    * Get a new {@link ConfigurationLoader} instance.
    */
   public static ConfigurationLoader create(final Object context)
   {
      return new ConfigurationLoader(context);
   }

   /**
    * Load all {@link ConfigurationProvider} instances, sort by {@link ConfigurationProvider#priority()}, and return a
    * unified, composite {@link Configuration} object.
    */
   public Configuration loadConfiguration(Object context)
   {
      if (caches.isEmpty())
      {
         return build(context);
      }
      return buildCached(context);
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   private Configuration buildCached(Object context)
   {
      Configuration result = null;
      /*
       * Do not force synchronization if a configuration is primed.
       */
      for (ConfigurationCacheProvider cache : caches) {
         Configuration cachedConfig = cache.getConfiguration(context);
         if (cachedConfig != null)
         {
            result = cachedConfig;
            break;
         }
      }

      if (result == null)
      {
         synchronized (this) {

            /*
             * Double check in order to ensure that a configuration wasn't built after our first cache check.
             */
            for (ConfigurationCacheProvider cache : caches) {
               Configuration cachedConfig = cache.getConfiguration(context);
               if (cachedConfig != null)
               {
                  result = cachedConfig;
                  break;
               }
            }

            if (result == null)
            {
               result = build(context);

               for (ConfigurationCacheProvider cache : caches) {
                  cache.setConfiguration(context, result);
               }
            }
         }
      }

      return result;
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   private Configuration build(Object context)
   {

      Map<Integer, List<Rule>> priorityMap = new LinkedHashMap<Integer, List<Rule>>();
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
                        if (rule instanceof RelocatableRule && ((RelocatableRule) rule).isRelocated())
                           addListValue(priorityMap, ((RelocatableRule) rule).priority(), rule);
                        else
                           addListValue(priorityMap, provider.priority(), rule);
                     }
                     else {
                        log.debug("Ignoring null Rule from ConfigurationProvider ["
                                 + provider.getClass().getName()
                                 + "]");
                     }
                  }
               }
               else {
                  log.debug("Ignoring null List<Rule> from ConfigurationProvider ["
                           + provider.getClass().getName()
                           + "]");
               }
            }
            else {
               log.debug("Ignoring null Configuration from ConfigurationProvider ["
                        + provider.getClass().getName()
                        + "].");
            }
         }
      }

      ConfigurationBuilder result = ConfigurationBuilder.begin();
      ArrayList<Integer> sortedKeys = new ArrayList<Integer>(priorityMap.keySet());
      Collections.sort(sortedKeys);

      for (Integer integer : sortedKeys) {
         List<Rule> list = priorityMap.get(integer);
         for (final Rule rule : list) {
            result.addRule(rule);

            try {
               if (rule instanceof ParameterizedRule) {
                  ParameterizedCallback callback = new ParameterizedCallback() {
                     @Override
                     public void call(Parameterized parameterized)
                     {
                        Set<String> names = parameterized.getRequiredParameterNames();
                        ParameterStore store = ((ParameterizedRule) rule).getParameterStore();

                        if (names != null)
                           for (String name : names) {
                              Parameter<?> parameter = store.get(name, new DefaultParameter(name));
                              if (parameter instanceof ConfigurableParameter<?>)
                                 ((ConfigurableParameter<?>) parameter).bindsTo(Evaluation.property(name));
                           }

                        parameterized.setParameterStore(store);
                     }
                  };

                  Visitor<Condition> conditionVisitor = new ParameterizedConditionVisitor(callback);
                  new ConditionVisit(rule).accept(conditionVisitor);

                  Visitor<Operation> operationVisitor = new ParameterizedOperationVisitor(callback);
                  new OperationVisit(rule).accept(operationVisitor);
               }
            }
            catch (RuntimeException e) {
               String message = "Error encountered while visiting rule: " + rule;

               if (rule instanceof Context)
               {
                  message += " defined at " + ((Context) rule).get(RuleMetadata.PROVIDER_LOCATION) + "\n";
               }
               log.error(message);
               throw e;
            }
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
