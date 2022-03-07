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

import org.junit.Test;

import com.ocpsoft.pretty.faces.config.rewrite.Case;
import com.ocpsoft.pretty.faces.config.rewrite.RewriteRule;
import com.ocpsoft.pretty.faces.config.rewrite.TrailingSlash;
import com.ocpsoft.pretty.faces.rewrite.processor.MockCustomClassProcessor;

import static org.assertj.core.api.Assertions.assertThat;

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

      assertThat(rewriteEngine.processInbound(null, null, c, url)).isEqualTo("/my/bar/is/COOL");
   }

   @Test
   public void testTrailingSlash() throws Exception
   {
      RewriteRule c = new RewriteRule();
      c.setTrailingSlash(TrailingSlash.APPEND);
      assertThat(rewriteEngine.processInbound(null, null, c, url)).isEqualTo("/my/foo/is/COOL/");
   }

   @Test
   public void testRemoveSingleTrailingSlash() throws Exception
   {
      RewriteRule c = new RewriteRule();
      c.setTrailingSlash(TrailingSlash.APPEND);
      assertThat(rewriteEngine.processInbound(null, null, c, "/")).isEqualTo("/");
   }

   @Test
   public void testToLowerCase() throws Exception
   {
      RewriteRule c = new RewriteRule();
      c.setToCase(Case.LOWERCASE);
      assertThat(rewriteEngine.processInbound(null, null, c, url)).isEqualTo("/my/foo/is/cool");
   }

   @Test
   public void testCustomClassProcessor() throws Exception
   {
      RewriteRule c = new RewriteRule();
      c.setProcessor(MockCustomClassProcessor.class.getName());
      assertThat(rewriteEngine.processInbound(null, null, c, url)).isEqualTo(MockCustomClassProcessor.RESULT);
   }
}
