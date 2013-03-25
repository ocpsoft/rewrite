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
package org.ocpsoft.rewrite.transform.markup;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MarkdownTest
{

   @Test
   public void testBoldText()
   {

      String markdown = "This is **bold**!";
      String html = Markdown.partialDocument().transform(markdown);

      assertEquals("<p>This is <strong>bold</strong>!</p>", html);

   }

   @Test
   public void testHeaders()
   {

      String markdown = "# Header\n\n##Section\n\nSome text!";
      String html = Markdown.partialDocument().transform(markdown);

      assertEquals("<h1 id='header'>Header</h1><h2 id='section'>Section</h2><p>Some text!</p>", normalize(html));

   }

   @Test
   public void testBlockquote()
   {

      String markdown = "> Some quote";
      String html = Markdown.partialDocument().transform(markdown);

      assertEquals("<blockquote><p>Some quote</p></blockquote>", normalize(html));

   }

   @Test
   public void testLists()
   {

      String markdown = "* One\n* Two";
      String html = Markdown.partialDocument().transform(markdown);

      assertEquals("<ul><li>One</li><li>Two</li></ul>", normalize(html));

   }

   @Test
   public void testCode()
   {

      String markdown = "    private int n = 0;";
      String html = Markdown.partialDocument().transform(markdown);

      assertEquals("<pre><code>private int n = 0;</code></pre>", normalize(html));

   }

   @Test
   public void testFullHtmlDocument()
   {

      String markdown = "some text";
      String html = Markdown.fullDocument().transform(markdown);

      assertTrue("DOCTYPE is missing", html.contains("<!DOCTYPE html"));
      assertTrue("html tag is missing", html.contains("<html"));
      assertTrue("body tag is missing", html.contains("<body>"));
      assertTrue("Expected text missing", html.contains("<p>some text</p>"));

   }

   @Test
   public void shouldRenderTitleCorrectly()
   {

      String textile = "some text";
      String html = Markdown.fullDocument().withTitle("My Title").transform(textile);

      assertThat(html, containsString("<title>My Title</title>"));

   }

   @Test
   public void shouldAddStylesheetCorrectly()
   {

      String textile = "some text";
      String html = Markdown.fullDocument().addStylesheet("http://localhost/style.css").transform(textile);

      assertThat(html, containsString("http://localhost/style.css"));

   }

   private static String normalize(String s)
   {
      return s.replaceAll("\n", "").replaceAll("[\t ]+", " ").trim();
   }

}
