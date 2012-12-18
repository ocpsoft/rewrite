package org.ocpsoft.urlbuilder.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EncoderTest
{

   @Test
   public void testPath()
   {
      assertEquals("test", Encoder.path("test"));
      assertEquals("hello%20world", Encoder.path("hello world"));
      assertEquals("a%7Cb", Encoder.path("a|b"));
      assertEquals("%5Btest%5D", Encoder.path("[test]"));
      assertEquals("%22a%22", Encoder.path("\"a\""));
      assertEquals("Really%3F", Encoder.path("Really?"));
      assertEquals("foo&bar", Encoder.path("foo&bar"));
   }

   @Test
   public void testQuery()
   {
      assertEquals("test", Encoder.query("test"));
      assertEquals("hello+world", Encoder.query("hello world"));
      assertEquals("a%7Cb", Encoder.query("a|b"));
      assertEquals("%5Btest%5D", Encoder.query("[test]"));
      assertEquals("%22a%22", Encoder.query("\"a\""));
      assertEquals("Really%3F", Encoder.query("Really?"));
   }

}
