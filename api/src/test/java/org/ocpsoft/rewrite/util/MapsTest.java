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
package org.ocpsoft.rewrite.util;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MapsTest
{

   @Test
   public void testAddArrayValueUninitialized()
   {
      Map<String, String[]> map = new TreeMap<String, String[]>();
      assertThat(map.get("lincoln")).isNull();
      Maps.addArrayValue(map, "lincoln", "baxter");
      assertThat(map.get("lincoln")[0]).isEqualTo("baxter");
   }

   @Test
   public void testAddArrayValueInitialized()
   {
      Map<String, String[]> map = new TreeMap<String, String[]>();
      assertThat(map.get("lincoln")).isNull();
      Maps.addArrayValue(map, "lincoln", "baxter");
      Maps.addArrayValue(map, "lincoln", "III");
      assertThat(map.get("lincoln")[0]).isEqualTo("baxter");
      assertThat(map.get("lincoln")[1]).isEqualTo("III");
   }

   @Test
   public void testAddListValueUninitialized()
   {
      Map<String, List<String>> map = new TreeMap<String, List<String>>();
      assertThat(map.get("lincoln")).isNull();
      Maps.addListValue(map, "lincoln", "baxter");
      assertThat(map.get("lincoln").get(0)).isEqualTo("baxter");
   }

   @Test
   public void testAddListValueInitialized()
   {
      Map<String, List<String>> map = new TreeMap<String, List<String>>();
      assertThat(map.get("lincoln")).isNull();
      Maps.addListValue(map, "lincoln", "baxter");
      Maps.addListValue(map, "lincoln", "III");
      assertThat(map.get("lincoln").get(0)).isEqualTo("baxter");
      assertThat(map.get("lincoln").get(1)).isEqualTo("III");
   }

   @Test
   public void testGetListValueEmpty() throws Exception
   {
      Map<String, List<String>> map = new TreeMap<String, List<String>>();

      assertThat(Maps.getListValue(map, "lincoln", 0)).isEqualTo(null);
   }

   @Test(expected=IndexOutOfBoundsException.class)
   public void testGetListValueOutOfRange() throws Exception
   {
      Map<String, List<String>> map = new TreeMap<String, List<String>>();
      Maps.addListValue(map, "lincoln", "baxter");

      assertThat(Maps.getListValue(map, "lincoln", 1)).isEqualTo(null);
   }

   @Test
   public void testGetListValue() throws Exception
   {
      Map<String, List<String>> map = new TreeMap<String, List<String>>();
      Maps.addListValue(map, "lincoln", "baxter");
      Maps.addListValue(map, "lincoln", "III");

      assertThat(Maps.getListValue(map, "lincoln", 0)).isEqualTo("baxter");
      assertThat(Maps.getListValue(map, "lincoln", 1)).isEqualTo("III");
   }

}
