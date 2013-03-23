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

import java.util.Map.Entry;

/**
 * {@link Parameter} store which retains the order, bindings, and names of parameters contained within.
 */
public interface ParameterStore extends Iterable<Entry<String, ConfigurableParameter<?>>>
{
   /**
    * Get the {@link Parameter} with the given name.
    * 
    * @throws IllegalArgumentException if the {@link Parameter} with the given name does not exist.
    */
   public ConfigurableParameter<?> get(String name) throws IllegalArgumentException;

   /**
    * Retrieve the {@link Parameter} with the given name, otherwise use the default, if supplied.
    * 
    * @throws IllegalArgumentException if the {@link Parameter} with the given name does not exist and no default was
    *            supplied.
    */
   public ConfigurableParameter<?> get(final String name, ConfigurableParameter<?> deflt);

   /**
    * Return <code>true</code> if this {@link ParameterStore} is empty, otherwise return <code>false</code>.
    */
   public boolean isEmpty();

   /**
    * Return the number of {@link Parameter} instances in this {@link ParameterStore}.
    */
   public int size();

   /**
    * Return <code>true</code> if this {@link ParameterStore} contains a {@link Parameter} with the given name,
    * otherwise return <code>false</code>.
    */
   public boolean contains(String name);
}
