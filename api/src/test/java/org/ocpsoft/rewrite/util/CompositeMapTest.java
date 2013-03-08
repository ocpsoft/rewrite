package org.ocpsoft.rewrite.util;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

public class CompositeMapTest
{

   @Test
   public void testEntrySet()
   {
      CompositeMap<String, String> map = new CompositeMap<String, String>();
      HashMap<String, String> delegate = new HashMap<String, String>();
      delegate.put("foo", "bar");
      map.addDelegate(delegate);

      Set<Entry<String, String>> entries = map.entrySet();
      Assert.assertNotNull(entries);
      for (Entry<String, String> entry : entries) {
         Assert.assertEquals("foo", entry.getKey());
         Assert.assertEquals("bar", entry.getValue());
      }
   }

}
