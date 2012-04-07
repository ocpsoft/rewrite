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
package org.ocpsoft.rewrite.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
         List<String> list = new ArrayList<String>(Arrays.asList(values));
         list.add(value);
         map.put(key, list.toArray(new String[] {}));
      }
   }

   public static <T> T getListValue(final Map<String, List<T>> values, final String name, final int index)
   {
      List<T> list = values.get(name);
      if ((list != null) && !list.isEmpty())
      {
         T item = list.get(index);
         return item;
      }
      return null;
   }

   public static Map<String, String[]> toArrayMap(final Map<String, List<String>> parameterMap)
   {
      Map<String, String[]> result = new LinkedHashMap<String, String[]>();

      for (Entry<String, List<String>> entry : parameterMap.entrySet())
      {
         for (String value : entry.getValue())
         {
            addArrayValue(result, entry.getKey(), value);
         }
      }
      return result;
   }
}
