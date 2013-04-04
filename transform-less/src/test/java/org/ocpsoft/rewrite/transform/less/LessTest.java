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
package org.ocpsoft.rewrite.transform.less;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.ocpsoft.rewrite.transform.less.Less;

public class LessTest
{

   @Test
   public void testCalculation()
   {
      String input = ".class { width: 1 + 1 }";
      String output = Less.compiler().transform(null, input);
      assertSameCSS(".class { width: 2; }", output);
   }

   @Test
   public void testVariables()
   {
      String input = "@nice-blue: #5B83AD;\n.myblue{ color: @nice-blue; }";
      String output = Less.compiler().transform(null, input);
      assertSameCSS(".myblue { color: #5B83AD; }", output);
   }

   @Test
   public void testMixins()
   {
      String input = ".bordered { border: 1px solid red; }\n.navigation { .bordered }\n";
      String output = Less.compiler().transform(null, input);
      assertSameCSS(".bordered{ border: 1px solid red; }\n.navigation{ border: 1px solid red; }\n", output);
   }

   private static void assertSameCSS(String expected, String actual)
   {
      assertEquals(normalize(expected), normalize(actual));
   }

   private static String normalize(String s)
   {
      return s.toLowerCase().replaceAll("\\s+", "");
   }

}
