package org.ocpsoft.rewrite.param;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * {@link Parameter} store which retains the order, bindings, and names of parameters contained within.
 */
public class ParameterStore<T extends ParameterBuilder<T, ?>> implements Map<String, T>
{
   private final Map<String, T> parameters = new LinkedHashMap<String, T>();

   public T where(final String param, T deflt)
   {
      T parameter = null;
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

   @Override
   public void clear()
   {
      parameters.clear();
   }

   @Override
   public boolean containsKey(Object key)
   {
      return parameters.get(key) != null;
   }

   @Override
   public boolean containsValue(Object value)
   {
      return parameters.containsValue(value);
   }

   @Override
   public Set<java.util.Map.Entry<String, T>> entrySet()
   {
      return parameters.entrySet();
   }

   @Override
   public T get(Object key)
   {
      if (!parameters.containsKey(key))
         throw new IllegalArgumentException("No such parameter [" + key + "] exists.");
      return parameters.get(key);
   }

   @Override
   public boolean isEmpty()
   {
      return parameters.isEmpty();
   }

   @Override
   public Set<String> keySet()
   {
      return parameters.keySet();
   }

   @Override
   public T put(String key, T value)
   {
      return parameters.put(key, value);
   }

   @Override
   public void putAll(Map<? extends String, ? extends T> map)
   {
      parameters.putAll(map);
   }

   @Override
   public T remove(Object key)
   {
      return parameters.remove(key);
   }

   @Override
   public int size()
   {
      return parameters.size();
   }

   @Override
   public Collection<T> values()
   {
      return parameters.values();
   }
}
