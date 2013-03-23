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
import java.util.Map;
import java.util.Map.Entry;

/**
 * {@link Parameter} store which retains the order, bindings, and names of parameters contained within.
 */
public class DefaultParameterStore implements ParameterStore
{
   private final Map<String, ConfigurableParameter<?>> parameters = new LinkedHashMap<String, ConfigurableParameter<?>>();

   public ConfigurableParameter<?> where(final String param, ConfigurableParameter<?> deflt)
   {
      ConfigurableParameter<?> parameter = null;
      if (parameters.get(param) != null)
      {
         parameter = parameters.get(param);
      }
      else
      {
         parameter = deflt;
         parameters.put(param, parameter);
      }
      return parameter;
   }

   public ConfigurableParameter<?> get(String key)
   {
      if (!parameters.containsKey(key))
         throw new IllegalArgumentException("No such parameter [" + key + "] exists.");
      return parameters.get(key);
   }

   public boolean isEmpty()
   {
      return parameters.isEmpty();
   }

   public ConfigurableParameter<?> put(String key, ConfigurableParameter<?> value)
   {
      return parameters.put(key, value);
   }

   public int size()
   {
      return parameters.size();
   }

   @Override
   public Iterator<Entry<String, ConfigurableParameter<?>>> iterator()
   {
      return parameters.entrySet().iterator();
   }

   public boolean contains(String name)
   {
      return parameters.containsKey(name);
   }

   @Override
   public String toString()
   {
      return parameters.keySet().toString();
   }
}
