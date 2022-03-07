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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author haaawk@gmail.com
 */

public class StringUtilsTest
{

   @Test
   public void testHasLeadingSlash()
   {
      assertThat(StringUtils.hasLeadingSlash("/")).isTrue();
      assertThat(StringUtils.hasLeadingSlash("/test")).isTrue();
      assertThat(StringUtils.hasLeadingSlash("//")).isTrue();
      assertThat(StringUtils.hasLeadingSlash("//test")).isTrue();
      assertThat(StringUtils.hasLeadingSlash("/test/")).isTrue();
      assertThat(StringUtils.hasLeadingSlash(null)).isFalse();
      assertThat(StringUtils.hasLeadingSlash("")).isFalse();
      assertThat(StringUtils.hasLeadingSlash("test")).isFalse();
      assertThat(StringUtils.hasLeadingSlash("test/")).isFalse();
      assertThat(StringUtils.hasLeadingSlash("test//")).isFalse();
   }

   @Test
   public void testHasTrailingSlash()
   {
      assertThat(StringUtils.hasTrailingSlash("/")).isTrue();
      assertThat(StringUtils.hasTrailingSlash("test/")).isTrue();
      assertThat(StringUtils.hasTrailingSlash("//")).isTrue();
      assertThat(StringUtils.hasTrailingSlash("test//")).isTrue();
      assertThat(StringUtils.hasTrailingSlash("/test/")).isTrue();
      assertThat(StringUtils.hasTrailingSlash(null)).isFalse();
      assertThat(StringUtils.hasTrailingSlash("")).isFalse();
      assertThat(StringUtils.hasTrailingSlash("test")).isFalse();
      assertThat(StringUtils.hasTrailingSlash("/test")).isFalse();
      assertThat(StringUtils.hasTrailingSlash("//test")).isFalse();
   }

   @Test
   public void testSplitBySlash()
   {
      assertThat(StringUtils.splitBySlash(null)).isEqualTo(new String[0]);
      assertThat(StringUtils.splitBySlash("")).isEqualTo(new String[0]);

      assertThat(StringUtils.splitBySlash("test")).isEqualTo(new String[]{"test"});
      assertThat(StringUtils.splitBySlash("a/b")).isEqualTo(new String[]{"a", "b"});
      assertThat(StringUtils.splitBySlash("a/b/c")).isEqualTo(new String[]{"a", "b", "c"});

      assertThat(StringUtils.splitBySlash("/test")).isEqualTo(new String[]{"", "test"});
      assertThat(StringUtils.splitBySlash("test/")).isEqualTo(new String[]{"test", ""});
      assertThat(StringUtils.splitBySlash("/test/")).isEqualTo(new String[]{"", "test", ""});

      assertThat(StringUtils.splitBySlash("/")).isEqualTo(new String[]{"", ""});
      assertThat(StringUtils.splitBySlash("//")).isEqualTo(new String[]{"", "", ""});
      assertThat(StringUtils.splitBySlash("//test")).isEqualTo(new String[]{"", "", "test"});
      assertThat(StringUtils.splitBySlash("test//")).isEqualTo(new String[]{"test", "", ""});
      assertThat(StringUtils.splitBySlash("//test//")).isEqualTo(new String[]{"", "", "test", "", ""});

      assertThat(StringUtils.splitBySlash("/a/b")).isEqualTo(new String[]{"", "a", "b"});
      assertThat(StringUtils.splitBySlash("a//b")).isEqualTo(new String[]{"a", "", "b"});
      assertThat(StringUtils.splitBySlash("a/b/")).isEqualTo(new String[]{"a", "b", ""});
      assertThat(StringUtils.splitBySlash("/a//b/")).isEqualTo(new String[]{"", "a", "", "b", ""});

      assertThat(StringUtils.splitBySlash("/a/b/c")).isEqualTo(new String[]{"", "a", "b", "c"});
      assertThat(StringUtils.splitBySlash("a//b/c")).isEqualTo(new String[]{"a", "", "b", "c"});
      assertThat(StringUtils.splitBySlash("a/b//c")).isEqualTo(new String[]{"a", "b", "", "c"});
      assertThat(StringUtils.splitBySlash("a/b/c/")).isEqualTo(new String[]{"a", "b", "c", ""});
      assertThat(StringUtils.splitBySlash("/a//b//c/")).isEqualTo(new String[]{"", "a", "", "b", "", "c", ""});

   }

   @Test
   public void testCountSlashes()
   {
      assertThat(StringUtils.countSlashes("")).isEqualTo(0);
      assertThat(StringUtils.countSlashes("abcde")).isEqualTo(0);
      assertThat(StringUtils.countSlashes("/")).isEqualTo(1);
      assertThat(StringUtils.countSlashes("/test")).isEqualTo(1);
      assertThat(StringUtils.countSlashes("test/")).isEqualTo(1);
      assertThat(StringUtils.countSlashes("test/test")).isEqualTo(1);
      assertThat(StringUtils.countSlashes("//")).isEqualTo(2);
      assertThat(StringUtils.countSlashes("//test")).isEqualTo(2);
      assertThat(StringUtils.countSlashes("test//")).isEqualTo(2);
      assertThat(StringUtils.countSlashes("/test/")).isEqualTo(2);
      assertThat(StringUtils.countSlashes("/test/test/")).isEqualTo(3);
   }

}