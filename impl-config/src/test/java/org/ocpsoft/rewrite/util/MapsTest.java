package org.ocpsoft.rewrite.util;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import junit.framework.Assert;

import org.junit.Test;

public class MapsTest
{

   @Test
   public void testAddArrayValueUninitialized()
   {
      Map<String, String[]> map = new TreeMap<String, String[]>();
      Assert.assertNull(map.get("lincoln"));
      Maps.addArrayValue(map, "lincoln", "baxter");
      Assert.assertEquals("baxter", map.get("lincoln")[0]);
   }

   @Test
   public void testAddArrayValueInitialized()
   {
      Map<String, String[]> map = new TreeMap<String, String[]>();
      Assert.assertNull(map.get("lincoln"));
      Maps.addArrayValue(map, "lincoln", "baxter");
      Maps.addArrayValue(map, "lincoln", "III");
      Assert.assertEquals("baxter", map.get("lincoln")[0]);
      Assert.assertEquals("III", map.get("lincoln")[1]);
   }

   @Test
   public void testAddListValueUninitialized()
   {
      Map<String, List<String>> map = new TreeMap<String, List<String>>();
      Assert.assertNull(map.get("lincoln"));
      Maps.addListValue(map, "lincoln", "baxter");
      Assert.assertEquals("baxter", map.get("lincoln").get(0));
   }

   @Test
   public void testAddListValueInitialized()
   {
      Map<String, List<String>> map = new TreeMap<String, List<String>>();
      Assert.assertNull(map.get("lincoln"));
      Maps.addListValue(map, "lincoln", "baxter");
      Maps.addListValue(map, "lincoln", "III");
      Assert.assertEquals("baxter", map.get("lincoln").get(0));
      Assert.assertEquals("III", map.get("lincoln").get(1));
   }

   @Test
   public void testGetListValueEmpty() throws Exception
   {
      Map<String, List<String>> map = new TreeMap<String, List<String>>();

      Assert.assertEquals(null, Maps.getListValue(map, "lincoln", 0));
   }

   @Test(expected=IndexOutOfBoundsException.class)
   public void testGetListValueOutOfRange() throws Exception
   {
      Map<String, List<String>> map = new TreeMap<String, List<String>>();
      Maps.addListValue(map, "lincoln", "baxter");

      Assert.assertEquals(null, Maps.getListValue(map, "lincoln", 1));
   }

   @Test
   public void testGetListValue() throws Exception
   {
      Map<String, List<String>> map = new TreeMap<String, List<String>>();
      Maps.addListValue(map, "lincoln", "baxter");
      Maps.addListValue(map, "lincoln", "III");

      Assert.assertEquals("baxter", Maps.getListValue(map, "lincoln", 0));
      Assert.assertEquals("III", Maps.getListValue(map, "lincoln", 1));
   }

}
