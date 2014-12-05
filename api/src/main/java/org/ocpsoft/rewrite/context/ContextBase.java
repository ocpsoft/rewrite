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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Base {@link Context} abstract class.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public abstract class ContextBase implements Context
{

   private final Map<Object, Object> map = new LinkedHashMap<Object, Object>();

   @Override
   public void clear()
   {
      map.clear();
   }

   @Override
   public Object get(final Object key)
   {
      return map.get(key);
   }

   @Override
   public void put(final Object key, final Object value)
   {
      map.put(key, value);
   }

   @Override
   public boolean containsKey(final Object key)
   {
      return map.containsKey(key);
   }

   @Override
   public String toString()
   {
      return "ContextBase [map=" + map + "]";
   }

}
