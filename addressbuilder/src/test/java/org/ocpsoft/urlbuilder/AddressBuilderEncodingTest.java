package org.ocpsoft.urlbuilder;

import org.junit.Assert;

import org.junit.Test;

public class AddressBuilderEncodingTest
{
   @Test
   public void testCreateEverything()
   {
      Assert.assertEquals("http://example.com:8080/search/table?q=query+string#foo",
               AddressBuilder.create("http://example.com:8080/search/table?q=query+string#foo").toString());
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
      Assert.assertEquals("foo%20bar", AddressBuilder.begin().path("foo%20bar").build().toString());
   }

   @Test
   public void testCreateEncodedSpaceInPath() throws Exception
   {
      Assert.assertEquals("/encoding/foo%20bar", AddressBuilder.create("/encoding/foo%20bar")
               .toString());
   }

   @Test
   public void testCreateEncodedAmpersandInQuery() throws Exception
   {
      Assert.assertEquals("/encoding.html?param=foo%26bar", AddressBuilder.create("/encoding.html?param=foo%26bar")
               .toString());
   }

   @Test
   public void testCreateUnencodedAmpersandInQuery() throws Exception
   {
      Assert.assertEquals("/encoding.html?param=foo&bar", AddressBuilder.create("/encoding.html?param=foo&bar")
               .toString());
   }

   @Test
   public void testParameterEncodingDomainWithQuery()
   {
      Assert.assertEquals("http://a%20b/?q=a+b",
               AddressBuilder.begin()
                        .scheme("http")
                        .domain("{p}")
                        .setEncoded("p", "a b")
                        .queryEncoded("q", "a b")
                        .build()
                        .toString());
   }

   @Test
   public void testParameterEncodingPathWithQuery()
   {
      Assert.assertEquals("http://localhost/a%20b?q=a+b",
               AddressBuilder.begin()
                        .scheme("http")
                        .domain("localhost")
                        .path("/{p}")
                        .setEncoded("p", "a b")
                        .queryEncoded("q", "a b")
                        .build()
                        .toString());
   }

   @Test
   public void testParameterEncodingResult()
   {
      Assert.assertEquals("http://localhost/a%20b?q=a+b",
               AddressBuilder.begin()
                        .scheme("http")
                        .domain("localhost")
                        .path("/{p}")
                        .setEncoded("p", "a b")
                        .queryEncoded("q", "a b")
                        .build()
                        .toString());
   }

   @Test
   public void testParametersWithoutEncoding()
   {
      Assert.assertEquals("http://localhost/a%20b?q=a+b",
               AddressBuilder.begin()
                        .scheme("http")
                        .domain("localhost")
                        .path("/{p}")
                        .set("p", "a%20b")
                        .query("q", "a+b")
                        .build()
                        .toString());
   }

   @Test
   public void testParametersWithoutEncodingResult()
   {
      /*
       * This is actually an erroneous resultant URL because the space ' ' character should be encoded, 
       * but since we are just testing behavior of the builder, this is fine. 
       * Just don't use this as a "good example".
       */
      Assert.assertEquals("http://localhost/a b?q=a b",
               AddressBuilder.begin()
                        .scheme("http")
                        .domain("localhost")
                        .path("/{p}")
                        .set("p", "a b")
                        .query("q", "a b")
                        .build()
                        .toString());
   }

   @Test
   public void testBuildQueryWithAmpersandInName()
   {
      Assert.assertEquals("?q%26q=200",
               AddressBuilder.begin().queryEncoded("q&q", 200).toString());
   }

   @Test
   public void testBuildQueryWithAmpersandInValue()
   {
      Assert.assertEquals("?q=%26200",
               AddressBuilder.begin().queryEncoded("q", "&200").toString());
   }

   @Test
   public void testBuildQueryWithQuestionMarkInName()
   {
      Assert.assertEquals("??q=200",
               AddressBuilder.begin().query("?q=200").toString());
   }

   @Test
   public void testBuildQueryWithQuestionMarkInValue()
   {
      Assert.assertEquals("?q=?200",
               AddressBuilder.begin().query("q", "?200").toString());
   }
}
