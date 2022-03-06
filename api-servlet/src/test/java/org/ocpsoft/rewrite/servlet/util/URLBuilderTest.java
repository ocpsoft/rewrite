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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
      assertThat(builder.toURL()).isEqualTo(value);
   }

   @Test
   public void testPreservesTrailingSlash() throws Exception
   {
      String value = "/com/ocpsoft/pretty/";
      URLBuilder builder = URLBuilder.createFrom(value);
      builder.setEncoding("UTF-8");
      assertThat(builder.decode().toURL()).isEqualTo(value);
   }

   @Test
   // http://code.google.com/p/prettyfaces/issues/detail?id=123
   public void testEmptySegmentsPreserved() throws Exception
   {
      // middle segment is empty
      assertThat(URLBuilder.createFrom("/test//test/").toURL()).isEqualTo("/test//test/");
      assertThat(URLBuilder.createFrom("/test//test/").decode().toURL()).isEqualTo("/test//test/");
      assertThat(URLBuilder.createFrom("/test//test/").encode().toURL()).isEqualTo("/test//test/");

      // last segment is empty
      assertThat(URLBuilder.createFrom("/test//").toURL()).isEqualTo("/test//");
      assertThat(URLBuilder.createFrom("/test//").decode().toURL()).isEqualTo("/test//");
      assertThat(URLBuilder.createFrom("/test//").encode().toURL()).isEqualTo("/test//");

      // first segment is empty
      assertThat(URLBuilder.createFrom("//test/").toURL()).isEqualTo("//test/");
      assertThat(URLBuilder.createFrom("//test/").decode().toURL()).isEqualTo("//test/");
      assertThat(URLBuilder.createFrom("//test/").encode().toURL()).isEqualTo("//test/");

      // only segment is empty
      assertThat(URLBuilder.createFrom("//").toURL()).isEqualTo("//");
      assertThat(URLBuilder.createFrom("//").decode().toURL()).isEqualTo("//");
      assertThat(URLBuilder.createFrom("//").encode().toURL()).isEqualTo("//");

      // multiple segments are empty
      assertThat(URLBuilder.createFrom("///").toURL()).isEqualTo("///");
      assertThat(URLBuilder.createFrom("///").decode().toURL()).isEqualTo("///");
      assertThat(URLBuilder.createFrom("///").encode().toURL()).isEqualTo("///");
      assertThat(URLBuilder.createFrom("////").toURL()).isEqualTo("////");
      assertThat(URLBuilder.createFrom("////").decode().toURL()).isEqualTo("////");
      assertThat(URLBuilder.createFrom("////").encode().toURL()).isEqualTo("////");
      assertThat(URLBuilder.createFrom("/////").toURL()).isEqualTo("/////");
      assertThat(URLBuilder.createFrom("/////").decode().toURL()).isEqualTo("/////");
      assertThat(URLBuilder.createFrom("/////").encode().toURL()).isEqualTo("/////");
   }

   @Test
   public void testSingleSlashPreserved() throws Exception
   {
      assertThat(URLBuilder.createFrom("/").toURL()).isEqualTo("/");
      assertThat(URLBuilder.createFrom("/").encode().toURL()).isEqualTo("/");
      assertThat(URLBuilder.createFrom("/").decode().toURL()).isEqualTo("/");
   }

   @Test
   public void testGetURLBuilderReturnsOneSlashWhenBuiltWithEmptyList() throws Exception
   {
      Metadata metadata = new Metadata();
      metadata.setTrailingSlash(true);
      URLBuilder builder = URLBuilder.createFrom(new ArrayList<String>(), metadata, QueryStringBuilder.createNew());

      assertThat(builder.toURL()).isEqualTo("/");
      assertThat(builder.decode().toURL()).isEqualTo("/");
   }

   @Test
   public void testDecode() throws Exception
   {
      String value = "/\u010d";
      URLBuilder builder = URLBuilder.createNew().appendPathSegments(value);
      URLBuilder encoded = builder.encode();
      assertThat(encoded.toURL()).isEqualTo("/%C4%8D");
   }

   @Test
   public void testEncode() throws Exception
   {
      String value = "/\u010d";
      URLBuilder builder = URLBuilder.createNew().appendPathSegments(value);
      URLBuilder encoded = builder.encode();
      assertThat(encoded.toURL()).isEqualTo("/%C4%8D");
      URLBuilder original = encoded.decode();
      assertThat(original.toURL()).isEqualTo("/\u010d");
   }

   @Test
   public void testEncodeDecodePreservesSlashes() throws Exception
   {
      String value = "/foo/bar";
      URLBuilder builder = URLBuilder.createNew().appendPathSegments(value);
      URLBuilder encoded = builder.encode();
      assertThat(encoded.toURL()).isEqualTo("/foo/bar");
      URLBuilder original = encoded.decode();
      assertThat(original.toURL()).isEqualTo("/foo/bar");
   }

   @Test
   public void testEncodeGermanUmlaut() throws Exception
   {
      String value = "/\u00e4";
      URLBuilder builder = URLBuilder.createNew().appendPathSegments(value);
      URLBuilder encoded = builder.encode();
      assertThat(encoded.toURL()).isEqualTo("/%C3%A4");
      URLBuilder original = encoded.decode();
      assertThat(original.toURL()).isEqualTo("/\u00e4");
   }

   @Test
   public void testCommaEncodingAndDecoding() throws Exception
   {
      // the comma is allowed and should not be encoded/decoded
      assertThat(URLBuilder.createNew().appendPathSegments("/a,b").encode().toURL()).isEqualTo("/a,b");
      assertThat(URLBuilder.createNew().appendPathSegments("/a,b").decode().toURL()).isEqualTo("/a,b");
   }

   @Test
   public void testSpaceEncodingAndDecoding() throws Exception
   {
      // encode
      assertThat(URLBuilder.createNew().appendPathSegments("/a b").encode().toURL()).isEqualTo("/a%20b");

      // decode
      assertThat(URLBuilder.createNew().appendPathSegments("/a%20b").decode().toURL()).isEqualTo("/a b");

      // decode plus does not change anything
      assertThat(URLBuilder.createNew().appendPathSegments("/a+b+c").decode().toURL()).isEqualTo("/a+b+c");
   }

   @Test
   public void testQuestionMarkEncodingAndDecoding() throws Exception
   {
      // encode
      assertThat(URLBuilder.createNew().appendPathSegments("/a?b").encode().toURL()).isEqualTo("/a%3Fb");

      // decode
      assertThat(URLBuilder.createNew().appendPathSegments("/a%3Fb").decode().toURL()).isEqualTo("/a?b");
   }

   @Test
   public void decodingSegmentWithPlusChararcter() {
      // + is valid as a segment. It should not be decoded.
      List<String> segments = URLBuilder.createFrom("foo+bar").decode().getSegments();
      assertThat(segments.size()).isEqualTo(1);
      assertThat(segments.get(0)).isEqualTo("foo+bar");
   }

}
