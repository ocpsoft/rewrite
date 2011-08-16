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
package com.ocpsoft.rewrite.servlet.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

public class URLBuilderTest
{
   @Test
   public void testURLBuilderPreservesOriginalURLBuilder() throws Exception
   {
      String value = "/com/ocpsoft/pretty/";
      URLBuilder builder = new URLBuilder(value);
      assertEquals(value, builder.toURL());
   }

   @Test
   public void testPreservesTrailingSlash() throws Exception
   {
      String value = "/com/ocpsoft/pretty/";
      URLBuilder builder = new URLBuilder(value);
      builder.setEncoding("UTF-8");
      assertEquals(value, builder.decode().toURL());
   }

   @Test
   public void testGetURLBuilderReturnsOneSlashWhenBuiltWithEmptyList() throws Exception
   {
      Metadata metadata = new Metadata();
      metadata.setTrailingSlash(true);
      URLBuilder builder = new URLBuilder(new ArrayList<String>(), metadata);

      assertEquals("/", builder.toURL());
      assertEquals("/", builder.decode().toURL());
   }

   @Test
   public void testDecode() throws Exception
   {
      String value = "/\u010d";
      URLBuilder URLBuilder = new URLBuilder(value);
      URLBuilder encoded = URLBuilder.encode();
      assertEquals("/%C4%8D", encoded.toURL());
   }

   @Test
   public void testEncode() throws Exception
   {
      String value = "/\u010d";
      URLBuilder builder = new URLBuilder(value);
      URLBuilder encoded = builder.encode();
      assertEquals("/%C4%8D", encoded.toURL());
      URLBuilder original = encoded.decode();
      assertEquals("/\u010d", original.toURL());
   }

   @Test
   public void testEncodeDecodePreservesSlashes() throws Exception
   {
      String value = "/foo/bar";
      URLBuilder builder = new URLBuilder(value);
      URLBuilder encoded = builder.encode();
      assertEquals("/foo/bar", encoded.toURL());
      URLBuilder original = encoded.decode();
      assertEquals("/foo/bar", original.toURL());
   }

   @Test
   public void testEncodeGermanUmlaut() throws Exception
   {
      String value = "/\u00e4";
      URLBuilder builder = new URLBuilder(value);
      URLBuilder encoded = builder.encode();
      assertEquals("/%C3%A4", encoded.toURL());
      URLBuilder original = encoded.decode();
      assertEquals("/\u00e4", original.toURL());
   }

   @Test
   public void testCommaEncodingAndDecoding() throws Exception
   {
      // the comma is allowed and should not be encoded/decoded
      assertEquals("/a,b", new URLBuilder("/a,b").encode().toURL());
      assertEquals("/a,b", new URLBuilder("/a,b").decode().toURL());
   }

   @Test
   public void testSpaceEncodingAndDecoding() throws Exception
   {
      // encode
      assertEquals("/a%20b", new URLBuilder("/a b").encode().toURL());

      // decode
      assertEquals("/a b", new URLBuilder("/a%20b").decode().toURL());

      // decode plus
      assertEquals("/a b c", new URLBuilder("/a+b+c").decode().toURL());
   }

   @Test
   public void testQuestionMarkEncodingAndDecoding() throws Exception
   {
      // encode
      assertEquals("/a%3Fb", new URLBuilder("/a?b").encode().toURL());

      // decode
      assertEquals("/a?b", new URLBuilder("/a%3Fb").decode().toURL());
   }

}
