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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PackageFilterTest
{

   @Test
   public void testEmptyFilter()
   {
      assertTrue(new PackageFilter(null).isAllowedPackage("org.ocpsoft"));
      assertTrue(new PackageFilter("").isAllowedPackage("org.ocpsoft"));
      assertTrue(new PackageFilter("  ").isAllowedPackage("org.ocpsoft"));
      assertTrue(new PackageFilter(" ,, ").isAllowedPackage("org.ocpsoft"));
   }

   @Test
   public void testSinglePackage()
   {

      // test simple filter
      PackageFilter filter = new PackageFilter("org.ocpsoft");
      assertEquals(true, filter.isAllowedPackage("org.ocpsoft.pretty.faces"));
      assertEquals(true, filter.isAllowedPackage("org.ocpsoft"));
      assertEquals(false, filter.isAllowedPackage("com"));
      assertEquals(false, filter.isAllowedPackage("de"));

      // test some danger configuration parameter inputs
      assertEquals(true, new PackageFilter("  org.ocpsoft ").isAllowedPackage("org.ocpsoft.pretty.faces"));
      assertEquals(true, new PackageFilter(" , org.ocpsoft, ").isAllowedPackage("org.ocpsoft.pretty.faces"));

   }

   @Test
   public void testMultiplePackages()
   {

      // test simple filter
      PackageFilter filter = new PackageFilter("org.ocpsoft,de.chkal");
      assertEquals(true, filter.isAllowedPackage("org.ocpsoft.pretty.faces"));
      assertEquals(true, filter.isAllowedPackage("org.ocpsoft"));
      assertEquals(true, filter.isAllowedPackage("de.chkal.bla"));
      assertEquals(true, filter.isAllowedPackage("de.chkal"));
      assertEquals(false, filter.isAllowedPackage("com"));
      assertEquals(false, filter.isAllowedPackage("com.google"));
      assertEquals(false, filter.isAllowedPackage("de"));

      // test some danger configuration parameter inputs
      assertEquals(true, new PackageFilter("  org.ocpsoft , de.chkal ").isAllowedPackage("org.ocpsoft.pretty.faces"));
      assertEquals(true, new PackageFilter(" , org.ocpsoft, de.chkal ,,").isAllowedPackage("de.chkal.bla"));

   }

}
