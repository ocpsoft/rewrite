/*
 * Copyright 2010 Lincoln Baxter, III
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
package com.ocpsoft.pretty.faces.url;

import java.util.ArrayList;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class URLTest
{
   @Test
   public void testURLWithEmptySegmentsConstruction() throws Exception
   {
      final String URL_WITH_TRAILING_EMPTY_SEGMENT = "/test//";
      final String URL_WITH_LEADING_EMPTY_SEGMENT = "//test/";
      final String URL_WITH_LEADING_AND_TRAILING_EMPTY_SEGMENTS = "//test//";
      final String URL_WITH_INNER_EMPTY_SEGMENTS = "/test//test2/";
      
      assertThat(new URL(URL_WITH_TRAILING_EMPTY_SEGMENT).decode().toURL()).isEqualTo(URL_WITH_TRAILING_EMPTY_SEGMENT);
      assertThat(new URL(URL_WITH_LEADING_EMPTY_SEGMENT).decode().toURL()).isEqualTo(URL_WITH_LEADING_EMPTY_SEGMENT);
      assertThat(new URL(URL_WITH_LEADING_AND_TRAILING_EMPTY_SEGMENTS).decode().toURL()).isEqualTo(URL_WITH_LEADING_AND_TRAILING_EMPTY_SEGMENTS);
      assertThat(new URL(URL_WITH_INNER_EMPTY_SEGMENTS).decode().toURL()).isEqualTo(URL_WITH_INNER_EMPTY_SEGMENTS);
   }
   
   @Test
   public void testURLPreservesOriginalURL() throws Exception
   {
      String value = "/com/ocpsoft/pretty/";
      URL url = new URL(value);
      assertThat(url.toURL()).isEqualTo(value);
   }

   @Test
   public void testPreservesTrailingSlash() throws Exception
   {
      String value = "/com/ocpsoft/pretty/";
      URL url = new URL(value);
      url.setEncoding("UTF-8");
      assertThat(url.decode().toURL()).isEqualTo(value);
   }

   @Test
   public void testGetURLReturnsOneSlashWhenBuiltWithEmptyList() throws Exception
   {
      Metadata metadata = new Metadata();
      metadata.setTrailingSlash(true);
      URL url = new URL(new ArrayList<String>(), metadata);

      assertThat(url.toURL()).isEqualTo("/");
      assertThat(url.decode().toURL()).isEqualTo("/");
   }

   @Test
   public void testDecode() throws Exception
   {
      String value = "/\u010d";
      URL url = new URL(value);
      URL encoded = url.encode();
      assertThat(encoded.toURL()).isEqualTo("/%C4%8D");
   }

   @Test
   public void testEncode() throws Exception
   {
      String value = "/\u010d";
      URL url = new URL(value);
      URL encoded = url.encode();
      assertThat(encoded.toURL()).isEqualTo("/%C4%8D");
      URL original = encoded.decode();
      assertThat(original.toURL()).isEqualTo("/\u010d");
   }

   @Test
   public void testEncodeDecodePreservesSlashes() throws Exception
   {
      String value = "/foo/bar";
      URL url = new URL(value);
      URL encoded = url.encode();
      assertThat(encoded.toURL()).isEqualTo("/foo/bar");
      URL original = encoded.decode();
      assertThat(original.toURL()).isEqualTo("/foo/bar");
   }
   
   @Test
   public void testEncodeGermanUmlaut() throws Exception
   {
      String value = "/\u00e4";
      URL url = new URL(value);
      URL encoded = url.encode();
      assertThat(encoded.toURL()).isEqualTo("/%C3%A4");
      URL original = encoded.decode();
      assertThat(original.toURL()).isEqualTo("/\u00e4");
   }
   
   @Test
   public void testCommaEncodingAndDecoding() throws Exception
   {
      // the comma is allowed and should not be encoded/decoded
      assertThat(new URL("/a,b").encode().toURL()).isEqualTo("/a,b");
      assertThat(new URL("/a,b").decode().toURL()).isEqualTo("/a,b");
   }

   @Test
   public void testSpaceEncodingAndDecoding() throws Exception
   {
      // encode
      assertThat(new URL("/a b").encode().toURL()).isEqualTo("/a%20b");
      
      // decode
      assertThat(new URL("/a%20b").decode().toURL()).isEqualTo("/a b");
      
      // decode of not-encoded character
      assertThat(new URL("/a b").decode().toURL()).isEqualTo("/a b");
      
   }

   @Test
   public void testQuestionMarkEncodingAndDecoding() throws Exception
   {
     // encode
     assertThat(new URL("/a?b").encode().toURL()).isEqualTo("/a%3Fb");

     // decode
     assertThat(new URL("/a%3Fb").decode().toURL()).isEqualTo("/a?b");
     
   }
   
   @Test
   public void testQuoteCharacterEncodingAndDecoding() throws Exception
   {
      
      // encode
      assertThat(new URL("/a\"b").encode().toURL()).isEqualTo("/a%22b");
      
      // decode
      assertThat(new URL("/a%22b").decode().toURL()).isEqualTo("/a\"b");
      
      // decode of not-encoded character
      assertThat(new URL("/a\"b").decode().toURL()).isEqualTo("/a\"b");
      
   }

   @Test
   public void testSquareBracketEncodingAndDecoding() throws Exception
   {

      // encode
      assertThat(new URL("/[a]").encode().toURL()).isEqualTo("/%5Ba%5D");

      // decode
      assertThat(new URL("/%5Ba%5D").decode().toURL()).isEqualTo("/[a]");

      // decode of not-encoded character
      assertThat(new URL("/[a]").decode().toURL()).isEqualTo("/[a]");

   }

   @Test
   public void testLessGreaterThanEncodingAndDecoding() throws Exception
   {

      // encode
      assertThat(new URL("/<a>").encode().toURL()).isEqualTo("/%3Ca%3E");

      // decode
      assertThat(new URL("/%3Ca%3E").decode().toURL()).isEqualTo("/<a>");

      // decode of not-encoded character
      assertThat(new URL("/<a>").decode().toURL()).isEqualTo("/<a>");

   }

   @Test
   public void testPipeEncodingAndDecoding() throws Exception
   {

      // encode
      assertThat(new URL("/|").encode().toURL()).isEqualTo("/%7C");

      // decode
      assertThat(new URL("/%7C").decode().toURL()).isEqualTo("/|");

      // decode of not-encoded character
      assertThat(new URL("/|").decode().toURL()).isEqualTo("/|");

   }
   
   
}
