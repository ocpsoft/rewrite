package org.ocpsoft.urlbuilder;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AddressBuilderTest
{

   @Test
   public void testBuildEverything()
   {
      assertThat(AddressBuilder.begin()
              .scheme("http")
              .domain("example.com")
              .port(8080)
              .path("/{s}/{t}")
              .set("s", "search")
              .set("t", "table")
              .queryEncoded("q", "query string")
              .anchor("foo")
              .build()
              .toString()).isEqualTo("http://example.com:8080/search/table?q=query+string#foo");
   }

   @Test
   public void testBuildEverythingResult()
   {
      assertThat(AddressBuilder.begin()
              .scheme("http")
              .domain("example.com")
              .port(8080)
              .path("/{s}/{t}")
              .set("s", "search")
              .set("t", "table")
              .queryEncoded("q", "query string")
              .anchor("foo")
              .build()
              .toString()).isEqualTo("http://example.com:8080/search/table?q=query+string#foo");
   }

   @Test
   public void testBuildQuery()
   {
      assertThat(AddressBuilder.begin().query("q", 200).toString()).isEqualTo("?q=200");
   }

   @Test
   public void testBuildQueryResult()
   {
      assertThat(AddressBuilder.begin().query("q", 200).build().toString()).isEqualTo("?q=200");
   }

   @Test
   public void testBuildQueryMultipleNames()
   {
      assertThat(AddressBuilder.begin().query("q", "query").query("e", "string").toString()).isEqualTo("?q=query&e=string");
   }

   @Test
   public void testBuildQueryMultipleNamesResult()
   {
      assertThat(AddressBuilder.begin().query("q", "query").query("e", "string").build().toString()).isEqualTo("?q=query&e=string");
   }

   @Test
   public void testBuildQueryMultipleValues()
   {
      assertThat(AddressBuilder.begin().query("q", 10, 20).toString()).isEqualTo("?q=10&q=20");
   }

   @Test
   public void testBuildQueryMultipleValuesResult()
   {
      assertThat(AddressBuilder.begin().query("q", 10, 20).build().toString()).isEqualTo("?q=10&q=20");
   }

   @Test
   public void testBuildQueryLiteral()
   {
      assertThat(AddressBuilder.begin().queryLiteral("q=200").toString()).isEqualTo("?q=200");
   }

   @Test
   public void testBuildQueryLiteralResult()
   {
      assertThat(AddressBuilder.begin().queryLiteral("q=200").build().toString()).isEqualTo("?q=200");
   }

   @Test
   public void testBuildQueryLiteralMultipleNames()
   {
      assertThat(AddressBuilder.begin().queryLiteral("q=query&e=string").toString()).isEqualTo("?q=query&e=string");
   }

   @Test
   public void testBuildQueryLiteralMultipleNamesResult()
   {
      assertThat(AddressBuilder.begin().queryLiteral("q=query&e=string").build().toString()).isEqualTo("?q=query&e=string");
   }

   @Test
   public void testBuildQueryLiteralMultipleValues()
   {
      assertThat(AddressBuilder.begin().queryLiteral("q=10&q=20").toString()).isEqualTo("?q=10&q=20");
   }

   @Test
   public void testBuildQueryLiteralMultipleValuesResult()
   {
      assertThat(AddressBuilder.begin().queryLiteral("q=10&q=20").build().toString()).isEqualTo("?q=10&q=20");
   }

   @Test
   public void testBuildPathSimple()
   {
      assertThat(AddressBuilder.begin().path("/store/23").toString()).isEqualTo("/store/23");
   }

   @Test
   public void testBuildPathSimpleResult()
   {
      assertThat(AddressBuilder.begin().path("/store/23").build().toString()).isEqualTo("/store/23");
   }

   @Test
   public void testBuildPathWithOneParameter()
   {
      assertThat(AddressBuilder.begin().path("/store/{item}").set("item", 23).build().toString()).isEqualTo("/store/23");
   }

   @Test
   public void testBuildPathWithOneParameterResult()
   {
      assertThat(AddressBuilder.begin().path("/store/{item}").set("item", 23).build().toString()).isEqualTo("/store/23");
   }

   @Test
   public void testBuildPathWithParameters()
   {
      assertThat(AddressBuilder.begin().path("/store/{item}/{action}").set("item", 23).set("action", "buy").build()
              .toString()).isEqualTo("/store/23/buy");
   }

   @Test
   public void testBuildPathWithParametersResult()
   {
      assertThat(AddressBuilder.begin().path("/store/{item}/{action}").set("item", 23).set("action", "buy").build()
              .toString()).isEqualTo("/store/23/buy");
   }

   @Test
   public void testBuildHostAndPath()
   {
      assertThat(AddressBuilder.begin()
              .domain("ocpsoft.org")
              .path("/store/{item}/{action}").set("item", 23).set("action", "buy").build().toString()).isEqualTo("//ocpsoft.org/store/23/buy");
   }

   @Test
   public void testBuildHostAndQuery()
   {
      assertThat(AddressBuilder.begin()
              .domain("ocpsoft.org")
              .query("buy", "23").build().toString()).isEqualTo("//ocpsoft.org/?buy=23");
   }

   @Test
   public void testBuildHostAndPathResult()
   {
      assertThat(AddressBuilder.begin().domain("ocpsoft.org")
              .path("/store/{item}/{action}").set("item", 23).set("action", "buy").build().toString()).isEqualTo("//ocpsoft.org/store/23/buy");
   }

   @Test
   public void testProtocolAndPort()
   {
      assertThat(AddressBuilder.begin().scheme("file").port(80).toString()).isEqualTo("file::80");
   }

   @Test
   public void testProtocolAndPortResult()
   {
      assertThat(AddressBuilder.begin().scheme("file").port(80).build().toString()).isEqualTo("file::80");
   }

   @Test
   public void testFromStringWithFullUrl()
   {
      Address address = AddressBuilder.create("http://www.google.com:80/search?q=foobar");
      assertThat(address.getScheme()).isEqualTo("http");
      assertThat(address.getDomain()).isEqualTo("www.google.com");
      assertThat(address.getPort()).isEqualTo(Integer.valueOf(80));
      assertThat(address.getPath()).isEqualTo("/search");
      assertThat(address.getQuery()).isEqualTo("q=foobar");
      assertThat(address.getPathAndQuery()).isEqualTo("/search?q=foobar");
   }

   @Test
   public void testFromStringWithoutPort()
   {
      Address address = AddressBuilder.create("http://www.google.com/search?q=foobar");
      assertThat(address.getScheme()).isEqualTo("http");
      assertThat(address.getDomain()).isEqualTo("www.google.com");
      assertThat(address.getPort()).isEqualTo(null);
      assertThat(address.getPath()).isEqualTo("/search");
      assertThat(address.getQuery()).isEqualTo("q=foobar");
      assertThat(address.getPathAndQuery()).isEqualTo("/search?q=foobar");
   }

   @Test
   public void testFromStringOnlyWithPathAndQuery()
   {
      Address address = AddressBuilder.create("/search?q=foobar");
      assertThat(address.getScheme()).isEqualTo(null);
      assertThat(address.getDomain()).isEqualTo(null);
      assertThat(address.getPort()).isEqualTo(null);
      assertThat(address.getPath()).isEqualTo("/search");
      assertThat(address.getQuery()).isEqualTo("q=foobar");
      assertThat(address.getPathAndQuery()).isEqualTo("/search?q=foobar");
   }

   @Test
   public void testFromStringOnlyWithPathAndQuery2()
   {
      Address address = AddressBuilder.create("search?q=foobar");
      assertThat(address.getScheme()).isEqualTo(null);
      assertThat(address.getDomain()).isEqualTo(null);
      assertThat(address.getPort()).isEqualTo(null);
      assertThat(address.getPath()).isEqualTo("search");
      assertThat(address.getQuery()).isEqualTo("q=foobar");
      assertThat(address.getPathAndQuery()).isEqualTo("search?q=foobar");
   }

   @Test
   public void testCreateSchemalessUrl()
   {

      Address address = AddressBuilder.begin()
               .scheme(null)
               .domain("example.com")
               .path("/test.txt")
               .build();

      assertThat(address.toString()).isEqualTo("//example.com/test.txt");

   }

   @Test
   public void testBuildSchemeSpecificPart()
   {
      assertThat(AddressBuilder.begin()
              .scheme("mailto")
              .schemeSpecificPart("contact@ocpsoft.org?subject=Howdy Lincoln!")
              .toString()).isEqualTo("mailto:contact@ocpsoft.org?subject=Howdy Lincoln!");
   }

   @Test
   public void testBuildSchemeSpecificPartResult()
   {
      assertThat(AddressBuilder.begin()
              .scheme("mailto")
              .schemeSpecificPart("contact@ocpsoft.org?subject=Howdy Lincoln!")
              .build().toString()).isEqualTo("mailto:contact@ocpsoft.org?subject=Howdy Lincoln!");
   }

   @Test
   public void testAnchorOnly()
   {
      assertThat(AddressBuilder.create("#foobar").toString()).isEqualTo("#foobar");
   }

   @Test
   public void testEmptyAnchorOnly()
   {
      assertThat(AddressBuilder.create("#").toString()).isEqualTo("#");
   }

   @Test
   public void shouldCreateAddressFromUrlWithCurlyBrace()
   {
      Address address = AddressBuilder.create("http://localhost/somepath/%7Bsomething%7D");
      assertThat(address.getPath()).isEqualTo("/somepath/%7Bsomething%7D");
      assertThat(address.toString()).isEqualTo("http://localhost/somepath/%7Bsomething%7D");
   }

   @Test
   public void testQueryWithNull() {
      assertThat(AddressBuilder.begin().query(null).build().toString()).isEqualTo("");
   }

   @Test
   public void testQueryDecodedWithNull() {
      assertThat(AddressBuilder.begin().queryDecoded(null).build().toString()).isEqualTo("");
   }

   @Test
   public void testQueryEncodedWithNull() {
      assertThat(AddressBuilder.begin().queryEncoded(null).build().toString()).isEqualTo("");
   }

   @Test
   public void testSetEncodedWithNull() {
      assertThat(AddressBuilder.begin().domain("localhost").setEncoded(null).build().toString()).isEqualTo("//localhost");
   }

   @Test
   public void testSetDecodedWithNull() {
      assertThat(AddressBuilder.begin().domain("localhost").setDecoded(null).build().toString()).isEqualTo("//localhost");
   }
}
