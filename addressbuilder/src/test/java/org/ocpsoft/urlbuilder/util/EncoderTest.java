package org.ocpsoft.urlbuilder.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EncoderTest
{

   @Test
   public void testPath()
   {
      assertThat(Encoder.path("test")).isEqualTo("test");
      assertThat(Encoder.path("hello world")).isEqualTo("hello%20world");
      assertThat(Encoder.path("a|b")).isEqualTo("a%7Cb");
      assertThat(Encoder.path("[test]")).isEqualTo("%5Btest%5D");
      assertThat(Encoder.path("\"a\"")).isEqualTo("%22a%22");
      assertThat(Encoder.path("Really?")).isEqualTo("Really%3F");
      assertThat(Encoder.path("foo&bar")).isEqualTo("foo&bar");
   }

   @Test
   public void testQuery()
   {
      assertThat(Encoder.query("test")).isEqualTo("test");
      assertThat(Encoder.query("hello world")).isEqualTo("hello+world");
      assertThat(Encoder.query("a|b")).isEqualTo("a%7Cb");
      assertThat(Encoder.query("[test]")).isEqualTo("%5Btest%5D");
      assertThat(Encoder.query("\"a\"")).isEqualTo("%22a%22");
      assertThat(Encoder.query("Really?")).isEqualTo("Really%3F");
   }

}
