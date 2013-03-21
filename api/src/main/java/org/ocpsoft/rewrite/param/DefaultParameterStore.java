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
   private final Map<String, Parameter<?>> parameters = new LinkedHashMap<String, Parameter<?>>();

   public Parameter<?> where(final String param, Parameter<?> deflt)
   {
      Parameter<?> parameter = null;
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

   public Parameter<?> get(String key)
   {
      if (!parameters.containsKey(key))
         throw new IllegalArgumentException("No such parameter [" + key + "] exists.");
      return parameters.get(key);
   }

   public boolean isEmpty()
   {
      return parameters.isEmpty();
   }

   public Parameter<?> put(String key, Parameter<?> value)
   {
      return parameters.put(key, value);
   }

   public int size()
   {
      return parameters.size();
   }

   @Override
   public Iterator<Entry<String, Parameter<?>>> iterator()
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
