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
package com.ocpsoft.rewrite.servlet.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class Maps
{
   @SuppressWarnings("unchecked")
   public static <K, T> void addListValue(final Map<K, List<T>> map, final K key, final T value)
   {
      if (!map.containsKey(key))
      {
         map.put(key, new ArrayList<T>(Arrays.asList(value)));
      }
      else
      {
         map.get(key).add(value);
      }
   }

   public static <T> void addArrayValue(final Map<T, String[]> map, final T key, final String value)
   {
      if (!map.containsKey(key))
      {
         map.put(key, Arrays.asList(value).toArray(new String[] {}));
      }
      else
      {
         String[] values = map.get(key);
         List<String> list = Arrays.asList(values);
         list.add(value);
         map.put(key, list.toArray(new String[] {}));
      }
   }

   public static <T> T popListValue(final Map<String, List<T>> values, final String name)
   {
      List<T> list = values.get(name);
      if ((list != null) && !list.isEmpty())
      {
         return list.remove(0);
      }
      return null;
   }
}
