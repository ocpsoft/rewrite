package org.ocpsoft.urlbuilder;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

public class AddressBuilderTest
{

   @Test
   public void testBuildEverything()
   {
      Assert.assertEquals("http://example.com:8080/search/table?q=query+string#foo",

               AddressBuilder.begin()
                        .scheme("http")
                        .domain("example.com")
                        .port(8080)
                        .path("/{s}/{t}")
                        .set("s", "search")
                        .set("t", "table")
                        .queryEncoded("q", "query string")
                        .anchor("foo")
                        .build()
                        .toString());
   }

   @Test
   public void testBuildEverythingResult()
   {
      Assert.assertEquals("http://example.com:8080/search/table?q=query+string#foo",

               AddressBuilder.begin()
                        .scheme("http")
                        .domain("example.com")
                        .port(8080)
                        .path("/{s}/{t}")
                        .set("s", "search")
                        .set("t", "table")
                        .queryEncoded("q", "query string")
                        .anchor("foo")
                        .build()
                        .toString());
   }

   @Test
   public void testBuildQuery()
   {
      Assert.assertEquals("?q=200",
               AddressBuilder.begin().query("q", 200).toString());
   }

   @Test
   public void testBuildQueryResult()
   {
      Assert.assertEquals("?q=200",
               AddressBuilder.begin().query("q", 200).build().toString());
   }

   @Test
   public void testBuildQueryMultipleNames()
   {
      Assert.assertEquals("?q=query&e=string",
               AddressBuilder.begin().query("q", "query").query("e", "string").toString());
   }

   @Test
   public void testBuildQueryMultipleNamesResult()
   {
      Assert.assertEquals("?q=query&e=string",
               AddressBuilder.begin().query("q", "query").query("e", "string").build().toString());
   }

   @Test
   public void testBuildQueryMultipleValues()
   {
      Assert.assertEquals("?q=10&q=20",
               AddressBuilder.begin().query("q", 10, 20).toString());
   }

   @Test
   public void testBuildQueryMultipleValuesResult()
   {
      Assert.assertEquals("?q=10&q=20",
               AddressBuilder.begin().query("q", 10, 20).build().toString());
   }

   @Test
   public void testBuildQueryLiteral()
   {
      Assert.assertEquals("?q=200",
               AddressBuilder.begin().queryLiteral("q=200").toString());
   }

   @Test
   public void testBuildQueryLiteralResult()
   {
      Assert.assertEquals("?q=200",
               AddressBuilder.begin().queryLiteral("q=200").build().toString());
   }

   @Test
   public void testBuildQueryLiteralMultipleNames()
   {
      Assert.assertEquals("?q=query&e=string",
               AddressBuilder.begin().queryLiteral("q=query&e=string").toString());
   }

   @Test
   public void testBuildQueryLiteralMultipleNamesResult()
   {
      Assert.assertEquals("?q=query&e=string",
               AddressBuilder.begin().queryLiteral("q=query&e=string").build().toString());
   }

   @Test
   public void testBuildQueryLiteralMultipleValues()
   {
      Assert.assertEquals("?q=10&q=20",
               AddressBuilder.begin().queryLiteral("q=10&q=20").toString());
   }

   @Test
   public void testBuildQueryLiteralMultipleValuesResult()
   {
      Assert.assertEquals("?q=10&q=20",
               AddressBuilder.begin().queryLiteral("q=10&q=20").build().toString());
   }

   @Test
   public void testBuildPathSimple()
   {
      Assert.assertEquals("/store/23",
               AddressBuilder.begin().path("/store/23").toString());
   }

   @Test
   public void testBuildPathSimpleResult()
   {
      Assert.assertEquals("/store/23",
               AddressBuilder.begin().path("/store/23").build().toString());
   }

   @Test
   public void testBuildPathWithOneParameter()
   {
      Assert.assertEquals("/store/23",
               AddressBuilder.begin().path("/store/{item}").set("item", 23).build().toString());
   }

   @Test
   public void testBuildPathWithOneParameterResult()
   {
      Assert.assertEquals("/store/23",
               AddressBuilder.begin().path("/store/{item}").set("item", 23).build().toString());
   }

   @Test
   public void testBuildPathWithParameters()
   {
      Assert.assertEquals("/store/23/buy",
               AddressBuilder.begin().path("/store/{item}/{action}").set("item", 23).set("action", "buy").build()
                        .toString());
   }

   @Test
   public void testBuildPathWithParametersResult()
   {
      Assert.assertEquals("/store/23/buy",
               AddressBuilder.begin().path("/store/{item}/{action}").set("item", 23).set("action", "buy").build()
                        .toString());
   }

   @Test
   public void testBuildHostAndPath()
   {
      Assert.assertEquals("//ocpsoft.org/store/23/buy",
               AddressBuilder.begin()
                        .domain("ocpsoft.org")
                        .path("/store/{item}/{action}").set("item", 23).set("action", "buy").build().toString());
   }

   @Test
   public void testBuildHostAndQuery()
   {
      Assert.assertEquals("//ocpsoft.org/?buy=23",
               AddressBuilder.begin()
                        .domain("ocpsoft.org")
                        .query("buy", "23").build().toString());
   }

   @Test
   public void testBuildHostAndPathResult()
   {
      Assert.assertEquals("//ocpsoft.org/store/23/buy",
               AddressBuilder.begin().domain("ocpsoft.org")
                        .path("/store/{item}/{action}").set("item", 23).set("action", "buy").build().toString());
   }

   @Test
   public void testProtocolAndPort()
   {
      Assert.assertEquals("file::80",
               AddressBuilder.begin().scheme("file").port(80).toString());
   }

   @Test
   public void testProtocolAndPortResult()
   {
      Assert.assertEquals("file::80",
               AddressBuilder.begin().scheme("file").port(80).build().toString());
   }

   @Test
   public void testFromStringWithFullUrl()
   {
      Address address = AddressBuilder.create("http://www.google.com:80/search?q=foobar");
      assertEquals("http", address.getScheme());
      assertEquals("www.google.com", address.getDomain());
      assertEquals(Integer.valueOf(80), address.getPort());
      assertEquals("/search", address.getPath());
      assertEquals("q=foobar", address.getQuery());
      assertEquals("/search?q=foobar", address.getPathAndQuery());
   }

   @Test
   public void testFromStringWithoutPort()
   {
      Address address = AddressBuilder.create("http://www.google.com/search?q=foobar");
      assertEquals("http", address.getScheme());
      assertEquals("www.google.com", address.getDomain());
      assertEquals(null, address.getPort());
      assertEquals("/search", address.getPath());
      assertEquals("q=foobar", address.getQuery());
      assertEquals("/search?q=foobar", address.getPathAndQuery());
   }

   @Test
   public void testFromStringOnlyWithPathAndQuery()
   {
      Address address = AddressBuilder.create("/search?q=foobar");
      assertEquals(null, address.getScheme());
      assertEquals(null, address.getDomain());
      assertEquals(null, address.getPort());
      assertEquals("/search", address.getPath());
      assertEquals("q=foobar", address.getQuery());
      assertEquals("/search?q=foobar", address.getPathAndQuery());
   }

   @Test
   public void testFromStringOnlyWithPathAndQuery2()
   {
      Address address = AddressBuilder.create("search?q=foobar");
      assertEquals(null, address.getScheme());
      assertEquals(null, address.getDomain());
      assertEquals(null, address.getPort());
      assertEquals("search", address.getPath());
      assertEquals("q=foobar", address.getQuery());
      assertEquals("search?q=foobar", address.getPathAndQuery());
   }

   @Test
   public void testCreateSchemalessUrl()
   {

      Address address = AddressBuilder.begin()
               .scheme(null)
               .domain("example.com")
               .path("/test.txt")
               .build();

      assertEquals("//example.com/test.txt", address.toString());

   }

   @Test
   public void testBuildSchemeSpecificPart()
   {
      Assert.assertEquals("mailto:contact@ocpsoft.org?subject=Howdy Lincoln!",
               AddressBuilder.begin()
                        .scheme("mailto")
                        .schemeSpecificPart("contact@ocpsoft.org?subject=Howdy Lincoln!")
                        .toString());
   }

   @Test
   public void testBuildSchemeSpecificPartResult()
   {
      Assert.assertEquals("mailto:contact@ocpsoft.org?subject=Howdy Lincoln!",
               AddressBuilder.begin()
                        .scheme("mailto")
                        .schemeSpecificPart("contact@ocpsoft.org?subject=Howdy Lincoln!")
                        .build().toString());
   }

   @Test
   public void testAnchorOnly()
   {
      assertEquals("#foobar", AddressBuilder.create("#foobar").toString());
   }

   @Test
   public void testEmptyAnchorOnly()
   {
      assertEquals("#", AddressBuilder.create("#").toString());
   }

   @Test
   public void shouldCreateAddressFromUrlWithCurlyBrace()
   {
      Address address = AddressBuilder.create("http://localhost/somepath/%7Bsomething%7D");
      assertEquals("/somepath/%7Bsomething%7D", address.getPath());
      assertEquals("http://localhost/somepath/%7Bsomething%7D", address.toString());
   }

   @Test
   public void testQueryWithNull() {
      assertEquals("",
              AddressBuilder.begin().query(null).build().toString());
   }

   @Test
   public void testQueryDecodedWithNull() {
      assertEquals("",
              AddressBuilder.begin().queryDecoded(null).build().toString());
   }

   @Test
   public void testQueryEncodedWithNull() {
      assertEquals("",
              AddressBuilder.begin().queryEncoded(null).build().toString());
   }

   @Test
   public void testSetEncodedWithNull() {
      assertEquals("//localhost",
              AddressBuilder.begin().domain("localhost").setEncoded(null).build().toString());
   }

   @Test
   public void testSetDecodedWithNull() {
      assertEquals("//localhost",
              AddressBuilder.begin().domain("localhost").setDecoded(null).build().toString());
   }
}
