package org.ocpsoft.rewrite.servlet.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.ocpsoft.rewrite.param.ParameterBuilder;

public class ParameterStore<T extends ParameterBuilder<T, ?>>
{
   private final Map<String, T> parameters = new HashMap<String, T>();

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
         this.parameters.put(param, parameter);
      }
      return parameter;
   }

   /**
    * Return an unmodifiable {@link Map} of the current parameters.
    */
   public Map<String, T> getParameters()
   {
      return Collections.unmodifiableMap(parameters);
   }
}
