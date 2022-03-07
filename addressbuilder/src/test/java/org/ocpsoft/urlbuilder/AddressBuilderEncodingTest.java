package org.ocpsoft.urlbuilder;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AddressBuilderEncodingTest
{
   @Test
   public void testCreateEverything()
   {
      assertThat(AddressBuilder.create("http://example.com:8080/search/table?q=query+string#foo").toString()).isEqualTo("http://example.com:8080/search/table?q=query+string#foo");
   }

   @Test(expected = IllegalArgumentException.class)
   public void testCreateImproperlyEncodedQuery()
   {
      /*
       * Space is not allowed in query strings.
       */
      AddressBuilder.create("http://example.com:8080/search/table?q=query string");
   }

   @Test
   public void testPathEncoded()
   {
      assertThat(AddressBuilder.begin().path("foo%20bar").build().toString()).isEqualTo("foo%20bar");
   }

   @Test
   public void testCreateEncodedSpaceInPath() throws Exception
   {
      assertThat(AddressBuilder.create("/encoding/foo%20bar")
              .toString()).isEqualTo("/encoding/foo%20bar");
   }

   @Test
   public void testCreateEncodedAmpersandInQuery() throws Exception
   {
      assertThat(AddressBuilder.create("/encoding.html?param=foo%26bar")
              .toString()).isEqualTo("/encoding.html?param=foo%26bar");
   }

   @Test
   public void testCreateUnencodedAmpersandInQuery() throws Exception
   {
      assertThat(AddressBuilder.create("/encoding.html?param=foo&bar")
              .toString()).isEqualTo("/encoding.html?param=foo&bar");
   }

   @Test
   public void testParameterEncodingDomainWithQuery()
   {
      assertThat(AddressBuilder.begin()
              .scheme("http")
              .domain("{p}")
              .setEncoded("p", "a b")
              .queryEncoded("q", "a b")
              .build()
              .toString()).isEqualTo("http://a%20b/?q=a+b");
   }

   @Test
   public void testParameterEncodingPathWithQuery()
   {
      assertThat(AddressBuilder.begin()
              .scheme("http")
              .domain("localhost")
              .path("/{p}")
              .setEncoded("p", "a b")
              .queryEncoded("q", "a b")
              .build()
              .toString()).isEqualTo("http://localhost/a%20b?q=a+b");
   }

   @Test
   public void testParameterEncodingResult()
   {
      assertThat(AddressBuilder.begin()
              .scheme("http")
              .domain("localhost")
              .path("/{p}")
              .setEncoded("p", "a b")
              .queryEncoded("q", "a b")
              .build()
              .toString()).isEqualTo("http://localhost/a%20b?q=a+b");
   }

   @Test
   public void testParametersWithoutEncoding()
   {
      assertThat(AddressBuilder.begin()
              .scheme("http")
              .domain("localhost")
              .path("/{p}")
              .set("p", "a%20b")
              .query("q", "a+b")
              .build()
              .toString()).isEqualTo("http://localhost/a%20b?q=a+b");
   }

   @Test
   public void testParametersWithoutEncodingResult()
   {
      /*
       * This is actually an erroneous resultant URL because the space ' ' character should be encoded, 
       * but since we are just testing behavior of the builder, this is fine. 
       * Just don't use this as a "good example".
       */
      assertThat(AddressBuilder.begin()
              .scheme("http")
              .domain("localhost")
              .path("/{p}")
              .set("p", "a b")
              .query("q", "a b")
              .build()
              .toString()).isEqualTo("http://localhost/a b?q=a b");
   }

   @Test
   public void testBuildQueryWithAmpersandInName()
   {
      assertThat(AddressBuilder.begin().queryEncoded("q&q", 200).toString()).isEqualTo("?q%26q=200");
   }

   @Test
   public void testBuildQueryWithAmpersandInValue()
   {
      assertThat(AddressBuilder.begin().queryEncoded("q", "&200").toString()).isEqualTo("?q=%26200");
   }

   @Test
   public void testBuildQueryWithQuestionMarkInName()
   {
      assertThat(AddressBuilder.begin().query("?q=200").toString()).isEqualTo("??q=200");
   }

   @Test
   public void testBuildQueryWithQuestionMarkInValue()
   {
      assertThat(AddressBuilder.begin().query("q", "?200").toString()).isEqualTo("?q=?200");
   }
}
