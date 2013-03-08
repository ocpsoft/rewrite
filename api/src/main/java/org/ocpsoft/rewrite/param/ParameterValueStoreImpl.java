package org.ocpsoft.rewrite.param;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

public class ParameterValueStoreImpl implements ParameterValueStore, Iterable<Entry<Parameter<?>, String>>
{
   Map<Parameter<?>, String> map = new HashMap<Parameter<?>, String>();

   @Override
   public String retrieve(Parameter<?> parameter)
   {
      return map.get(parameter);
   }

   @Override
   public boolean submit(Rewrite event, EvaluationContext context, Parameter<?> param, String value)
   {
      boolean result = false;
      String stored = map.get(param);

      if (stored == value || (stored != null && stored.equals(value)))
      {
         result = true;
      }
      else if (stored == null)
      {
         result = true;
         for (Constraint<String> constraint : param.getConstraints()) {
            if (!constraint.isSatisfiedBy(event, context, value))
            {
               result = false;
            }
         }

         if (result)
         {
            for (Transform<String> transform : param.getTransforms()) {
               value = transform.transform(event, context, value);
            }
            map.put(param, value);
            result = true;
         }
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
