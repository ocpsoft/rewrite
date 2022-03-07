/*
 * Copyright 2011 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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
package org.ocpsoft.rewrite.annotation.scan;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PackageFilterTest
{

   @Test
   public void testEmptyFilter()
   {
      assertThat(new PackageFilter(null).isAllowedPackage("org.ocpsoft")).isTrue();
      assertThat(new PackageFilter("").isAllowedPackage("org.ocpsoft")).isTrue();
      assertThat(new PackageFilter("  ").isAllowedPackage("org.ocpsoft")).isTrue();
      assertThat(new PackageFilter(" ,, ").isAllowedPackage("org.ocpsoft")).isTrue();
   }

   @Test
   public void testSinglePackage()
   {

      // test simple filter
      PackageFilter filter = new PackageFilter("org.ocpsoft");
      assertThat(filter.isAllowedPackage("org.ocpsoft.pretty.faces")).isEqualTo(true);
      assertThat(filter.isAllowedPackage("org.ocpsoft")).isEqualTo(true);
      assertThat(filter.isAllowedPackage("com")).isEqualTo(false);
      assertThat(filter.isAllowedPackage("de")).isEqualTo(false);

      // test some danger configuration parameter inputs
      assertThat(new PackageFilter("  org.ocpsoft ").isAllowedPackage("org.ocpsoft.pretty.faces")).isEqualTo(true);
      assertThat(new PackageFilter(" , org.ocpsoft, ").isAllowedPackage("org.ocpsoft.pretty.faces")).isEqualTo(true);

   }

   @Test
   public void testMultiplePackages()
   {

      // test simple filter
      PackageFilter filter = new PackageFilter("org.ocpsoft,de.chkal");
      assertThat(filter.isAllowedPackage("org.ocpsoft.pretty.faces")).isEqualTo(true);
      assertThat(filter.isAllowedPackage("org.ocpsoft")).isEqualTo(true);
      assertThat(filter.isAllowedPackage("de.chkal.bla")).isEqualTo(true);
      assertThat(filter.isAllowedPackage("de.chkal")).isEqualTo(true);
      assertThat(filter.isAllowedPackage("com")).isEqualTo(false);
      assertThat(filter.isAllowedPackage("com.google")).isEqualTo(false);
      assertThat(filter.isAllowedPackage("de")).isEqualTo(false);

      // test some danger configuration parameter inputs
      assertThat(new PackageFilter("  org.ocpsoft , de.chkal ").isAllowedPackage("org.ocpsoft.pretty.faces")).isEqualTo(true);
      assertThat(new PackageFilter(" , org.ocpsoft, de.chkal ,,").isAllowedPackage("de.chkal.bla")).isEqualTo(true);

   }

}
