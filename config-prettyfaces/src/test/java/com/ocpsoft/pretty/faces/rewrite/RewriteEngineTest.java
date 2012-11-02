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
package com.ocpsoft.pretty.faces.rewrite;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ocpsoft.pretty.faces.config.rewrite.Case;
import com.ocpsoft.pretty.faces.config.rewrite.RewriteRule;
import com.ocpsoft.pretty.faces.config.rewrite.TrailingSlash;
import com.ocpsoft.pretty.faces.rewrite.processor.MockCustomClassProcessor;

public class RewriteEngineTest
{
   String url = "/my/foo/is/COOL";
   RewriteEngine rewriteEngine = new RewriteEngine();

   @Test
   public void testRegex() throws Exception
   {
      RewriteRule c = new RewriteRule();
      c.setMatch("foo");
      c.setSubstitute("bar");

      assertEquals("/my/bar/is/COOL", rewriteEngine.processInbound(null, null, c, url));
   }

   @Test
   public void testTrailingSlash() throws Exception
   {
      RewriteRule c = new RewriteRule();
      c.setTrailingSlash(TrailingSlash.APPEND);
      assertEquals("/my/foo/is/COOL/", rewriteEngine.processInbound(null, null, c, url));
   }

   @Test
   public void testRemoveSingleTrailingSlash() throws Exception
   {
      RewriteRule c = new RewriteRule();
      c.setTrailingSlash(TrailingSlash.APPEND);
      assertEquals("/", rewriteEngine.processInbound(null, null, c, "/"));
   }

   @Test
   public void testToLowerCase() throws Exception
   {
      RewriteRule c = new RewriteRule();
      c.setToCase(Case.LOWERCASE);
      assertEquals("/my/foo/is/cool", rewriteEngine.processInbound(null, null, c, url));
   }

   @Test
   public void testCustomClassProcessor() throws Exception
   {
      RewriteRule c = new RewriteRule();
      c.setProcessor(MockCustomClassProcessor.class.getName());
      assertEquals(MockCustomClassProcessor.RESULT, rewriteEngine.processInbound(null, null, c, url));
   }
}
