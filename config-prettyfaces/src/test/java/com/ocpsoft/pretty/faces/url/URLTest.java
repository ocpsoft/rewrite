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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

public class URLTest
{
   @Test
   public void testURLWithEmptySegmentsConstruction() throws Exception
   {
      final String URL_WITH_TRAILING_EMPTY_SEGMENT = "/test//";
      final String URL_WITH_LEADING_EMPTY_SEGMENT = "//test/";
      final String URL_WITH_LEADING_AND_TRAILING_EMPTY_SEGMENTS = "//test//";
      final String URL_WITH_INNER_EMPTY_SEGMENTS = "/test//test2/";
      
      assertEquals(URL_WITH_TRAILING_EMPTY_SEGMENT, 
           new URL(URL_WITH_TRAILING_EMPTY_SEGMENT).decode().toURL());
      assertEquals(URL_WITH_LEADING_EMPTY_SEGMENT, 
           new URL(URL_WITH_LEADING_EMPTY_SEGMENT).decode().toURL());
      assertEquals(URL_WITH_LEADING_AND_TRAILING_EMPTY_SEGMENTS, 
           new URL(URL_WITH_LEADING_AND_TRAILING_EMPTY_SEGMENTS).decode().toURL());
      assertEquals(URL_WITH_INNER_EMPTY_SEGMENTS, 
            new URL(URL_WITH_INNER_EMPTY_SEGMENTS).decode().toURL());
   }
   
   @Test
   public void testURLPreservesOriginalURL() throws Exception
   {
      String value = "/com/ocpsoft/pretty/";
      URL url = new URL(value);
      assertEquals(value, url.toURL());
   }

   @Test
   public void testPreservesTrailingSlash() throws Exception
   {
      String value = "/com/ocpsoft/pretty/";
      URL url = new URL(value);
      url.setEncoding("UTF-8");
      assertEquals(value, url.decode().toURL());
   }

   @Test
   public void testGetURLReturnsOneSlashWhenBuiltWithEmptyList() throws Exception
   {
      Metadata metadata = new Metadata();
      metadata.setTrailingSlash(true);
      URL url = new URL(new ArrayList<String>(), metadata);

      assertEquals("/", url.toURL());
      assertEquals("/", url.decode().toURL());
   }

   @Test
   public void testDecode() throws Exception
   {
      String value = "/\u010d";
      URL url = new URL(value);
      URL encoded = url.encode();
      assertEquals("/%C4%8D", encoded.toURL());
   }

   @Test
   public void testEncode() throws Exception
   {
      String value = "/\u010d";
      URL url = new URL(value);
      URL encoded = url.encode();
      assertEquals("/%C4%8D", encoded.toURL());
      URL original = encoded.decode();
      assertEquals("/\u010d", original.toURL());
   }

   @Test
   public void testEncodeDecodePreservesSlashes() throws Exception
   {
      String value = "/foo/bar";
      URL url = new URL(value);
      URL encoded = url.encode();
      assertEquals("/foo/bar", encoded.toURL());
      URL original = encoded.decode();
      assertEquals("/foo/bar", original.toURL());
   }
   
   @Test
   public void testEncodeGermanUmlaut() throws Exception
   {
      String value = "/\u00e4";
      URL url = new URL(value);
      URL encoded = url.encode();
      assertEquals("/%C3%A4", encoded.toURL());
      URL original = encoded.decode();
      assertEquals("/\u00e4", original.toURL());
   }
   
   @Test
   public void testCommaEncodingAndDecoding() throws Exception
   {
      // the comma is allowed and should not be encoded/decoded
      assertEquals("/a,b", new URL("/a,b").encode().toURL());
      assertEquals("/a,b", new URL("/a,b").decode().toURL());
   }

   @Test
   public void testSpaceEncodingAndDecoding() throws Exception
   {
      // encode
      assertEquals("/a%20b", new URL("/a b").encode().toURL());
      
      // decode
      assertEquals("/a b", new URL("/a%20b").decode().toURL());
      
      // decode of not-encoded character
      assertEquals("/a b", new URL("/a b").decode().toURL());
      
   }

   @Test
   public void testQuestionMarkEncodingAndDecoding() throws Exception
   {
     // encode
     assertEquals("/a%3Fb", new URL("/a?b").encode().toURL());

     // decode
     assertEquals("/a?b", new URL("/a%3Fb").decode().toURL());
     
   }
   
   @Test
   public void testQuoteCharacterEncodingAndDecoding() throws Exception
   {
      
      // encode
      assertEquals("/a%22b", new URL("/a\"b").encode().toURL());
      
      // decode
      assertEquals("/a\"b", new URL("/a%22b").decode().toURL());
      
      // decode of not-encoded character
      assertEquals("/a\"b", new URL("/a\"b").decode().toURL());
      
   }

   @Test
   public void testSquareBracketEncodingAndDecoding() throws Exception
   {

      // encode
      assertEquals("/%5Ba%5D", new URL("/[a]").encode().toURL());

      // decode
      assertEquals("/[a]", new URL("/%5Ba%5D").decode().toURL());

      // decode of not-encoded character
      assertEquals("/[a]", new URL("/[a]").decode().toURL());

   }

   @Test
   public void testLessGreaterThanEncodingAndDecoding() throws Exception
   {

      // encode
      assertEquals("/%3Ca%3E", new URL("/<a>").encode().toURL());

      // decode
      assertEquals("/<a>", new URL("/%3Ca%3E").decode().toURL());

      // decode of not-encoded character
      assertEquals("/<a>", new URL("/<a>").decode().toURL());

   }

   @Test
   public void testPipeEncodingAndDecoding() throws Exception
   {

      // encode
      assertEquals("/%7C", new URL("/|").encode().toURL());

      // decode
      assertEquals("/|", new URL("/%7C").decode().toURL());

      // decode of not-encoded character
      assertEquals("/|", new URL("/|").decode().toURL());

   }
   
   
}
