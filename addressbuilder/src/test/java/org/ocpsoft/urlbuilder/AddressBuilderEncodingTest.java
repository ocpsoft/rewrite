package org.ocpsoft.urlbuilder;

import junit.framework.Assert;

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
      Assert.assertEquals("foo%20bar", AddressBuilder.begin().pathEncoded("foo%20bar").build().toString());
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
   public void testParameterEncoding()
   {
      Assert.assertEquals("http://localhost/a%20b?q=a+b",
               AddressBuilder.begin()
                        .protocol("http")
                        .host("localhost")
                        .path("/{p}")
                        .set("p", "a b")
                        .query("q", "a b")
                        .toString());
   }

   @Test
   public void testParameterEncodingResult()
   {
      Assert.assertEquals("http://localhost/a%20b?q=a+b",
               AddressBuilder.begin()
                        .protocol("http")
                        .host("localhost")
                        .path("/{p}")
                        .set("p", "a b")
                        .query("q", "a b")
                        .build()
                        .toString());
   }

   @Test
   public void testParametersWithoutEncoding()
   {
      Assert.assertEquals("http://localhost/a%20b?q=a+b",
               AddressBuilder.begin()
                        .protocol("http")
                        .host("localhost")
                        .path("/{p}")
                        .setEncoded("p", "a%20b")
                        .queryEncoded("q", "a+b")
                        .toString());
   }

   @Test
   public void testParametersWithoutEncodingResult()
   {
      Assert.assertEquals("http://localhost/a%20b?q=a+b",
               AddressBuilder.begin()
                        .protocol("http")
                        .host("localhost")
                        .path("/{p}")
                        .setEncoded("p", "a%20b")
                        .queryEncoded("q", "a+b")
                        .build()
                        .toString());
   }

   @Test
   public void testBuildQueryWithAmpersandInName()
   {
      Assert.assertEquals("?q%26q=200",
               AddressBuilder.begin().query("q&q", 200).toString());
   }

   @Test
   public void testBuildQueryWithAmpersandInValue()
   {
      Assert.assertEquals("?q=%26200",
               AddressBuilder.begin().query("q", "&200").toString());
   }

   @Test
   public void testBuildQueryWithQuestionMarkInName()
   {
      Assert.assertEquals("??q=200",
               AddressBuilder.begin().queryEncoded("?q=200").toString());
   }

   @Test
   public void testBuildQueryWithQuestionMarkInValue()
   {
      Assert.assertEquals("?q=?200",
               AddressBuilder.begin().queryEncoded("q", "?200").toString());
   }
}
