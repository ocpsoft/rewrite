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
package org.ocpsoft.rewrite.faces.navigate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * MultiMap for storing multiple values for each parameter
 * 
 * @author Christian Kaltepoth
 */
class ParameterMap
{

   private final Map<String, List<String>> map;

   public ParameterMap()
   {
      map = new LinkedHashMap<String, List<String>>();
   }

   public ParameterMap(ParameterMap source)
   {
      map = new LinkedHashMap<String, List<String>>(source.map);
   }

   public void put(String key, String value)
   {

      List<String> values = map.get(key);
      if (values == null)
      {
         values = new ArrayList<String>();
         map.put(key, values);
      }

      values.add(value);

   }

   public ParameterMap copy()
   {
      return new ParameterMap(this);
   }

   public Set<Entry<String, List<String>>> entrySet()
   {
      return map.entrySet();
   }

}
