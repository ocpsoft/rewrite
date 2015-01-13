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
package org.ocpsoft.rewrite.param;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.common.util.Assert;
import org.ocpsoft.common.util.Iterators;
import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.spi.GlobalParameterProvider;
import org.ocpsoft.rewrite.util.ServiceLogger;

/**
 * Default implementation of {@link ParameterValueStore}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class DefaultParameterValueStore implements ParameterValueStore, Iterable<Entry<Parameter<?>, String>>
{
   Map<Parameter<?>, String> map = new LinkedHashMap<Parameter<?>, String>();
   private static List<GlobalParameterProvider> providers;
   private static final Logger log = Logger.getLogger(DefaultParameterValueStore.class);

   /**
    * Create a new, empty {@link DefaultParameterValueStore} instance.
    */
   @SuppressWarnings("unchecked")
   public DefaultParameterValueStore()
   {
      if (providers == null)
      {
         providers = Iterators.asList(ServiceLoader.load(GlobalParameterProvider.class));
         ServiceLogger.logLoadedServices(log, GlobalParameterProvider.class, providers);
      }
   }

   /**
    * Create a new {@link DefaultParameterValueStore} instance, copying all {@link Parameter} and value pairs from the
    * given instance.
    */
   public DefaultParameterValueStore(DefaultParameterValueStore instance)
   {
      for (Entry<Parameter<?>, String> entry : instance)
      {
         map.put(entry.getKey(), entry.getValue());
      }
   }

   @Override
   public String retrieve(Parameter<?> parameter)
   {
      String value = map.get(parameter);
      return value;
   }

   @Override
   public boolean submit(Rewrite event, EvaluationContext context, Parameter<?> param, String value)
   {
      Assert.notNull(event, "Rewrite event must not be null.");
      Assert.notNull(context, "EvaluationContext must not be null.");
      Assert.notNull(param, "Parameter must not be null.");

      boolean result = false;
      boolean supportsSubmission = supportsSubmission(event, context, param, value);
      if (!supportsSubmission)
      {
         result = true;
      }
      else if (supportsSubmission && isValid(event, context, param, value))
      {
         // FIXME Transposition processing will break multi-conditional matching
         for (Transposition<String> transposition : param.getTranspositions())
         {
            value = transposition.transpose(event, context, value);
         }
         map.put(param, value);
         result = true;
      }

      return result;
   }

   private boolean supportsSubmission(Rewrite event, EvaluationContext context, Parameter<?> param, String value)
   {
      boolean result = true;
      for (GlobalParameterProvider provider : providers)
      {
         Set<Parameter<?>> params = provider.getParameters();
         if (params != null)
         {
            for (Parameter<?> parameter : params)
            {
               if (parameter != null && parameter.getName() != null && parameter.getName().equals(param.getName()))
               {
                  result = provider.supportsSubmission(event, context, parameter);
                  break;
               }
            }
         }
      }

      return result;
   }

   @Override
   public boolean isValid(Rewrite event, EvaluationContext context, Parameter<?> param, String value)
   {
      Assert.notNull(event, "Rewrite event must not be null.");
      Assert.notNull(context, "EvaluationContext must not be null.");
      Assert.notNull(param, "Parameter must not be null.");

      String stored = map.get(param);
      boolean result = false;
      if (_doParameterProviderValidation(event, context, param, value))
      {
         result = true;
      }
      else if (stored == value || (stored != null && stored.equals(value)))
      {
         result = true;
      }
      else if (stored == null)
      {
         result = true;
         for (Constraint<String> constraint : param.getConstraints())
         {
            if (!constraint.isSatisfiedBy(event, context, value))
            {
               result = false;
            }
         }
      }

      return result;
   }

   private boolean _doParameterProviderValidation(Rewrite event, EvaluationContext context, Parameter<?> param,
            String value)
   {
      boolean result = false;
      for (GlobalParameterProvider provider : providers)
      {
         Set<Parameter<?>> params = provider.getParameters();
         if (params != null)
         {
            for (Parameter<?> parameter : params)
            {
               if (parameter != null && parameter.getName() != null && parameter.getName().equals(param.getName()))
               {
                  result = provider.isValid(event, context, param, value);
                  break;
               }
            }
         }
      }
      return result;
   }

   @Override
   public Iterator<Entry<Parameter<?>, String>> iterator()
   {
      return map.entrySet().iterator();
   }

   @Override
   public String toString()
   {
      return map.keySet().toString();
   }

   /**
    * Retrieve the current {@link ParameterValueStore} from the given {@link EvaluationContext} instance.
    * 
    * @throws IllegalStateException If the {@link ParameterValueStore} could not be located.
    */
   public static ParameterValueStore getInstance(EvaluationContext context) throws IllegalStateException
   {
      ParameterValueStore valueStore = (ParameterValueStore) context.get(ParameterValueStore.class);
      if (valueStore == null)
      {
         throw new IllegalStateException("Could not retrieve " + ParameterValueStore.class.getName() + " from "
                  + EvaluationContext.class.getName() + ". Has the " + EvaluationContext.class.getSimpleName()
                  + " been set up properly?");
      }
      return valueStore;
   }
}
