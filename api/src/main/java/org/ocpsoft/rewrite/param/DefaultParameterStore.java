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

import org.ocpsoft.common.util.Assert;

/**
 * {@link Parameter} store which retains the order, bindings, and names of parameters contained within.
 */
public class DefaultParameterStore implements ParameterStore
{
   private final Map<String, ConfigurableParameter<?>> parameters = new LinkedHashMap<String, ConfigurableParameter<?>>();

   public DefaultParameterStore()
   {
      get("*", new DefaultParameter("*")).constrainedBy(new RegexConstraint(".*"));
   }

   public ConfigurableParameter<?> get(final String name, ConfigurableParameter<?> deflt)
   {
      ConfigurableParameter<?> parameter = null;
      if (parameters.get(name) != null)
      {
         parameter = parameters.get(name);
      }
      else
      {
         parameter = deflt;
         parameters.put(name, parameter);
      }

      if (parameter == null)
         throw new IllegalArgumentException("No such parameter [" + name + "] exists.");

      return parameter;
   }

   public ConfigurableParameter<?> get(String name)
   {
      if (!parameters.containsKey(name))
         throw new IllegalArgumentException("No such parameter [" + name + "] exists.");
      return parameters.get(name);
   }

   public boolean isEmpty()
   {
      return parameters.isEmpty();
   }

   public ConfigurableParameter<?> store(ConfigurableParameter<?> value)
   {
      Assert.notNull(value, "Parameter to store must not be null.");
      return parameters.put(value.getName(), value);
   }

   public int size()
   {
      return parameters.size() - 1;
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
