/*
 * Copyright 2010 Lincoln Baxter, StringUtils.countSlashes(III Licensed under
 * the Apache License, StringUtils.countSlashes(Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may obtain a
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless
 * required by applicable law or agreed to in writing,
 * StringUtils.countSlashes(software distributed under the License is
 * distributed on an "AS IS" BASIS, StringUtils.countSlashes(WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, StringUtils.countSlashes(either express or
 * implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ocpsoft.pretty.faces.util;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

/**
 * @author haaawk@gmail.com
 */

public class StringUtilsTest
{

   @Test
   public void testHasLeadingSlash()
   {
      assertTrue(StringUtils.hasLeadingSlash("/"));
      assertTrue(StringUtils.hasLeadingSlash("/test"));
      assertTrue(StringUtils.hasLeadingSlash("//"));
      assertTrue(StringUtils.hasLeadingSlash("//test"));
      assertTrue(StringUtils.hasLeadingSlash("/test/"));
      assertFalse(StringUtils.hasLeadingSlash(null));
      assertFalse(StringUtils.hasLeadingSlash(""));
      assertFalse(StringUtils.hasLeadingSlash("test"));
      assertFalse(StringUtils.hasLeadingSlash("test/"));
      assertFalse(StringUtils.hasLeadingSlash("test//"));
   }

   @Test
   public void testHasTrailingSlash()
   {
      assertTrue(StringUtils.hasTrailingSlash("/"));
      assertTrue(StringUtils.hasTrailingSlash("test/"));
      assertTrue(StringUtils.hasTrailingSlash("//"));
      assertTrue(StringUtils.hasTrailingSlash("test//"));
      assertTrue(StringUtils.hasTrailingSlash("/test/"));
      assertFalse(StringUtils.hasTrailingSlash(null));
      assertFalse(StringUtils.hasTrailingSlash(""));
      assertFalse(StringUtils.hasTrailingSlash("test"));
      assertFalse(StringUtils.hasTrailingSlash("/test"));
      assertFalse(StringUtils.hasTrailingSlash("//test"));
   }

   @Test
   public void testSplitBySlash()
   {
      assertArrayEquals(new String[0], StringUtils.splitBySlash(null));
      assertArrayEquals(new String[0], StringUtils.splitBySlash(""));

      assertArrayEquals(new String[] { "test" }, StringUtils.splitBySlash("test"));
      assertArrayEquals(new String[] { "a", "b" }, StringUtils.splitBySlash("a/b"));
      assertArrayEquals(new String[] { "a", "b", "c" }, StringUtils.splitBySlash("a/b/c"));

      assertArrayEquals(new String[] { "", "test" }, StringUtils.splitBySlash("/test"));
      assertArrayEquals(new String[] { "test", "" }, StringUtils.splitBySlash("test/"));
      assertArrayEquals(new String[] { "", "test", "" }, StringUtils.splitBySlash("/test/"));

      assertArrayEquals(new String[] { "", "" }, StringUtils.splitBySlash("/"));
      assertArrayEquals(new String[] { "", "", "" }, StringUtils.splitBySlash("//"));
      assertArrayEquals(new String[] { "", "", "test" }, StringUtils.splitBySlash("//test"));
      assertArrayEquals(new String[] { "test", "", "" }, StringUtils.splitBySlash("test//"));
      assertArrayEquals(new String[] { "", "", "test", "", "" }, StringUtils.splitBySlash("//test//"));

      assertArrayEquals(new String[] { "", "a", "b" }, StringUtils.splitBySlash("/a/b"));
      assertArrayEquals(new String[] { "a", "", "b" }, StringUtils.splitBySlash("a//b"));
      assertArrayEquals(new String[] { "a", "b", "" }, StringUtils.splitBySlash("a/b/"));
      assertArrayEquals(new String[] { "", "a", "", "b", "" }, StringUtils.splitBySlash("/a//b/"));

      assertArrayEquals(new String[] { "", "a", "b", "c" }, StringUtils.splitBySlash("/a/b/c"));
      assertArrayEquals(new String[] { "a", "", "b", "c" }, StringUtils.splitBySlash("a//b/c"));
      assertArrayEquals(new String[] { "a", "b", "", "c" }, StringUtils.splitBySlash("a/b//c"));
      assertArrayEquals(new String[] { "a", "b", "c", "" }, StringUtils.splitBySlash("a/b/c/"));
      assertArrayEquals(new String[] { "", "a", "", "b", "", "c", "" }, StringUtils.splitBySlash("/a//b//c/"));

   }

   @Test
   public void testCountSlashes()
   {
      assertEquals(0, StringUtils.countSlashes(""));
      assertEquals(0, StringUtils.countSlashes("abcde"));
      assertEquals(1, StringUtils.countSlashes("/"));
      assertEquals(1, StringUtils.countSlashes("/test"));
      assertEquals(1, StringUtils.countSlashes("test/"));
      assertEquals(1, StringUtils.countSlashes("test/test"));
      assertEquals(2, StringUtils.countSlashes("//"));
      assertEquals(2, StringUtils.countSlashes("//test"));
      assertEquals(2, StringUtils.countSlashes("test//"));
      assertEquals(2, StringUtils.countSlashes("/test/"));
      assertEquals(3, StringUtils.countSlashes("/test/test/"));
   }

}