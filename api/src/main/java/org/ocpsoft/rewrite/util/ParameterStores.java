package org.ocpsoft.rewrite.util;

import java.util.Set;

import org.ocpsoft.rewrite.param.DefaultParameter;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;

/**
 * Utility methods for interactive with {@link ParameterStore} instances.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ParameterStores
{
   /**
    * Initialize the {@link Parameterized} instance with the given {@link ParameterStore}, also record required
    * parameter names in the {@link ParameterStore} and initialize with a new {@link Parameter} instance.
    */
   public static void initialize(ParameterStore store, Parameterized parameterized)
   {
      Set<String> names = parameterized.getRequiredParameterNames();
      for (String name : names) {
         if (!store.contains(name))
            store.put(name, new DefaultParameter(name));
      }

      parameterized.setParameterStore(store);
   }
}
