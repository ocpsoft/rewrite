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
package org.ocpsoft.rewrite.servlet.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/*
 * FIXME:  Remove references to deprecated code.
 * Leaving test class while the code is still usable.  
 * Adding task as a better method of tracking.  
 */
@Deprecated
public class URLBuilderTest
{
   @Test(expected = IllegalArgumentException.class)
   public void testNullURL() throws Exception
   {
      URLBuilder.createFrom(null);
   }

   @Test
   public void testURLBuilderPreservesOriginalURLBuilder() throws Exception
   {
      String value = "/com/ocpsoft/pretty/";
      URLBuilder builder = URLBuilder.createFrom(value);
      assertEquals(value, builder.toURL());
   }

   @Test
   public void testPreservesTrailingSlash() throws Exception
   {
      String value = "/com/ocpsoft/pretty/";
      URLBuilder builder = URLBuilder.createFrom(value);
      builder.setEncoding("UTF-8");
      assertEquals(value, builder.decode().toURL());
   }

   @Test
   // http://code.google.com/p/prettyfaces/issues/detail?id=123
   public void testEmptySegmentsPreserved() throws Exception
   {
      // middle segment is empty
      assertEquals("/test//test/", URLBuilder.createFrom("/test//test/").toURL());
      assertEquals("/test//test/", URLBuilder.createFrom("/test//test/").decode().toURL());
      assertEquals("/test//test/", URLBuilder.createFrom("/test//test/").encode().toURL());

      // last segment is empty
      assertEquals("/test//", URLBuilder.createFrom("/test//").toURL());
      assertEquals("/test//", URLBuilder.createFrom("/test//").decode().toURL());
      assertEquals("/test//", URLBuilder.createFrom("/test//").encode().toURL());

      // first segment is empty
      assertEquals("//test/", URLBuilder.createFrom("//test/").toURL());
      assertEquals("//test/", URLBuilder.createFrom("//test/").decode().toURL());
      assertEquals("//test/", URLBuilder.createFrom("//test/").encode().toURL());

      // only segment is empty
      assertEquals("//", URLBuilder.createFrom("//").toURL());
      assertEquals("//", URLBuilder.createFrom("//").decode().toURL());
      assertEquals("//", URLBuilder.createFrom("//").encode().toURL());

      // multiple segments are empty
      assertEquals("///", URLBuilder.createFrom("///").toURL());
      assertEquals("///", URLBuilder.createFrom("///").decode().toURL());
      assertEquals("///", URLBuilder.createFrom("///").encode().toURL());
      assertEquals("////", URLBuilder.createFrom("////").toURL());
      assertEquals("////", URLBuilder.createFrom("////").decode().toURL());
      assertEquals("////", URLBuilder.createFrom("////").encode().toURL());
      assertEquals("/////", URLBuilder.createFrom("/////").toURL());
      assertEquals("/////", URLBuilder.createFrom("/////").decode().toURL());
      assertEquals("/////", URLBuilder.createFrom("/////").encode().toURL());
   }

   @Test
   public void testSingleSlashPreserved() throws Exception
   {
      assertEquals("/", URLBuilder.createFrom("/").toURL());
      assertEquals("/", URLBuilder.createFrom("/").encode().toURL());
      assertEquals("/", URLBuilder.createFrom("/").decode().toURL());
   }

   @Test
   public void testGetURLBuilderReturnsOneSlashWhenBuiltWithEmptyList() throws Exception
   {
      Metadata metadata = new Metadata();
      metadata.setTrailingSlash(true);
      URLBuilder builder = URLBuilder.createFrom(new ArrayList<String>(), metadata, QueryStringBuilder.createNew());

      assertEquals("/", builder.toURL());
      assertEquals("/", builder.decode().toURL());
   }

   @Test
   public void testDecode() throws Exception
   {
      String value = "/\u010d";
      URLBuilder builder = URLBuilder.createNew().appendPathSegments(value);
      URLBuilder encoded = builder.encode();
      assertEquals("/%C4%8D", encoded.toURL());
   }

   @Test
   public void testEncode() throws Exception
   {
      String value = "/\u010d";
      URLBuilder builder = URLBuilder.createNew().appendPathSegments(value);
      URLBuilder encoded = builder.encode();
      assertEquals("/%C4%8D", encoded.toURL());
      URLBuilder original = encoded.decode();
      assertEquals("/\u010d", original.toURL());
   }

   @Test
   public void testEncodeDecodePreservesSlashes() throws Exception
   {
      String value = "/foo/bar";
      URLBuilder builder = URLBuilder.createNew().appendPathSegments(value);
      URLBuilder encoded = builder.encode();
      assertEquals("/foo/bar", encoded.toURL());
      URLBuilder original = encoded.decode();
      assertEquals("/foo/bar", original.toURL());
   }

   @Test
   public void testEncodeGermanUmlaut() throws Exception
   {
      String value = "/\u00e4";
      URLBuilder builder = URLBuilder.createNew().appendPathSegments(value);
      URLBuilder encoded = builder.encode();
      assertEquals("/%C3%A4", encoded.toURL());
      URLBuilder original = encoded.decode();
      assertEquals("/\u00e4", original.toURL());
   }

   @Test
   public void testCommaEncodingAndDecoding() throws Exception
   {
      // the comma is allowed and should not be encoded/decoded
      assertEquals("/a,b", URLBuilder.createNew().appendPathSegments("/a,b").encode().toURL());
      assertEquals("/a,b", URLBuilder.createNew().appendPathSegments("/a,b").decode().toURL());
   }

   @Test
   public void testSpaceEncodingAndDecoding() throws Exception
   {
      // encode
      assertEquals("/a%20b", URLBuilder.createNew().appendPathSegments("/a b").encode().toURL());

      // decode
      assertEquals("/a b", URLBuilder.createNew().appendPathSegments("/a%20b").decode().toURL());

      // decode plus does not change anything
      assertEquals("/a+b+c", URLBuilder.createNew().appendPathSegments("/a+b+c").decode().toURL());
   }

   @Test
   public void testQuestionMarkEncodingAndDecoding() throws Exception
   {
      // encode
      assertEquals("/a%3Fb", URLBuilder.createNew().appendPathSegments("/a?b").encode().toURL());

      // decode
      assertEquals("/a?b", URLBuilder.createNew().appendPathSegments("/a%3Fb").decode().toURL());
   }

   @Test
   public void decodingSegmentWithPlusChararcter() {
      // + is valid as a segment. It should not be decoded.
      List<String> segments = URLBuilder.createFrom("foo+bar").decode().getSegments();
      assertEquals(1, segments.size());
      assertEquals("foo+bar", segments.get(0));
   }

}
