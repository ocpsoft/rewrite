/*
 * Copyright 2011 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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
package org.ocpsoft.rewrite.context;

/**
 * An object capable of storing and retrieving values.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface Context
{
   /**
    * Clear the contents of this context and reset to a "like-new" state.
    */
   void clear();

   /**
    * Get the value in the context map defined by the given key. Return <code>null</code> if no such key exists, or if
    * they key maps to a <code>null</code> value.
    */
   Object get(Object key);

   /**
    * Store a key value pair into the context.
    */
   void put(Object key, Object value);

   /**
    * Return <code>true</code> if this context contains an entry with the given key.
    */
   boolean containsKey(Object key);
}
