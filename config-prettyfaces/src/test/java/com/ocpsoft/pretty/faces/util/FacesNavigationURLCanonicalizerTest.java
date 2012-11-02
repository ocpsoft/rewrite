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
package com.ocpsoft.pretty.faces.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FacesNavigationURLCanonicalizerTest
{

   /**
    * don't fail for null values
    */
   @Test
   public void testNormalizeRequestUriWithNullArguments()
   {
      assertEquals(null, FacesNavigationURLCanonicalizer.normalizeRequestURI(null, null, null));
   }

   /**
    * Test extension mapping: *.jsf
    */
   @Test
   public void testNormalizeRequestUriWithExtensionMapping()
   {
      assertEquals("/page2.jsf", FacesNavigationURLCanonicalizer.normalizeRequestURI("/page1.jsf", null, "/page2.jsf"));
   }

   /**
    * Test path mapping: /faces/*
    */
   @Test
   public void testNormalizeRequestUriWithPathMapping()
   {
      assertEquals("/page2.xhtml", FacesNavigationURLCanonicalizer.normalizeRequestURI("/faces", "/page1.xhtml", "/faces/page2.xhtml"));
   }

}
