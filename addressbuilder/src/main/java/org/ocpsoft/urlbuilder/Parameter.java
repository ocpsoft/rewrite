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
package org.ocpsoft.urlbuilder;

import java.util.Arrays;
import java.util.List;

/**
 * Immutable internal state object used in {@link AddressBuilder} to perform parameterization of {@link String} based
 * values.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
class Parameter
{
   private final CharSequence name;
   private final List<Object> values;

   private Parameter(CharSequence name, List<Object> values)
   {
      this.name = name;
      this.values = values;
   }

   public static Parameter create(CharSequence name, List<Object> values)
   {
      return new Parameter(name, values);
   }

   public static Parameter create(CharSequence name, Object... values)
   {
      return new Parameter(name, Arrays.asList(values));
   }

   public CharSequence getName()
   {
      return name;
   }

   public boolean hasValues()
   {
      return !values.isEmpty();
   }

   public int getValueCount()
   {
      return values.size();
   }

   public String getValue(int index)
   {
      Object value = values.get(index);
      return value == null ? null : value.toString();
   }

   public List<Object> getValues()
   {
      return values;
   }

}
