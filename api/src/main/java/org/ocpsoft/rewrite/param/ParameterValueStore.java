package org.ocpsoft.rewrite.param;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class ParameterValueStore implements Iterable<Entry<Parameter<?>, String>>
{
   Map<Parameter<?>, String> map = new HashMap<Parameter<?>, String>();

   public boolean submit(Parameter<?> param, String value)
   {
      boolean result = false;
      String stored = map.get(param);

      if (stored == value || (stored != null && stored.equals(value)))
      {
         result = true;
      }
      else if (stored == null)
      {
         // FIXME handle constraints and transforms
         map.put(param, value);
         result = true;
      }

      return result;
   }

   public String get(Parameter<?> parameter)
   {
      return map.get(parameter);
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
}
