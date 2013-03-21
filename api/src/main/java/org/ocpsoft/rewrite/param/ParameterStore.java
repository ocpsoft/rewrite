package org.ocpsoft.rewrite.param;

import java.util.Map.Entry;

/**
 * {@link Parameter} store which retains the order, bindings, and names of parameters contained within.
 */
public interface ParameterStore extends Iterable<Entry<String, Parameter<?>>>
{
   public Parameter<?> where(final String param, Parameter<?> deflt);

   public Parameter<?> get(String key);

   public boolean isEmpty();

   public Parameter<?> put(String key, Parameter<?> value);

   public int size();

   public boolean contains(String name);
}
