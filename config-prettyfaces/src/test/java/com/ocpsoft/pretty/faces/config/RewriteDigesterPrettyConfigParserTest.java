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
package com.ocpsoft.pretty.faces.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;
import com.ocpsoft.pretty.faces.config.rewrite.Case;
import com.ocpsoft.pretty.faces.config.rewrite.Redirect;
import com.ocpsoft.pretty.faces.config.rewrite.RewriteRule;
import com.ocpsoft.pretty.faces.config.rewrite.TrailingSlash;

public class RewriteDigesterPrettyConfigParserTest
{
   private static final String CONFIG_PATH = "rewrite-pretty-config.xml";
   private PrettyConfig config;

   @Before
   public void configure() throws IOException, SAXException
   {
      final PrettyConfigBuilder builder = new PrettyConfigBuilder();
      new DigesterPrettyConfigParser().parse(builder, getClass().getClassLoader().getResourceAsStream(CONFIG_PATH));
      config = builder.build();
   }

   @Test
   public void testDefaultRewriteValues() throws Exception
   {
      RewriteRule c = new RewriteRule();
      assertEquals("", c.getMatch());
      assertEquals("", c.getSubstitute());
      assertEquals("", c.getUrl());
      assertEquals(Redirect.PERMANENT, c.getRedirect());
      assertEquals(true, c.isOutbound());
      assertEquals(Case.IGNORE, c.getToCase());
      assertEquals(TrailingSlash.IGNORE, c.getTrailingSlash());
   }

   @Test
   public void testParseRewriteEntries() throws Exception
   {
      List<RewriteRule> rules = config.getGlobalRewriteRules();
      RewriteRule r = rules.get(0);
      assertEquals("^(.*[^/])$", r.getMatch());
      assertEquals("$1/", r.getSubstitute());
      assertEquals(Redirect.CHAIN, r.getRedirect());
      assertTrue(r.isOutbound());
      assertEquals(Case.IGNORE, r.getToCase());
      assertEquals(TrailingSlash.IGNORE, r.getTrailingSlash());

      r = rules.get(1);
      assertEquals("", r.getMatch());
      assertEquals("", r.getSubstitute());
      assertEquals(Redirect.PERMANENT, r.getRedirect());
      assertEquals(true, r.isOutbound());
      assertEquals(Case.LOWERCASE, r.getToCase());
      assertEquals(TrailingSlash.APPEND, r.getTrailingSlash());

      r = rules.get(2);
      assertEquals("", r.getMatch());
      assertEquals("", r.getSubstitute());
      assertEquals("http://www.google.com", r.getUrl());
      assertEquals(Redirect.TEMPORARY, r.getRedirect());
      assertEquals(false, r.isOutbound());
      assertEquals(Case.UPPERCASE, r.getToCase());
      assertEquals(TrailingSlash.REMOVE, r.getTrailingSlash());
   }

   @Test
   public void testParse()
   {
      UrlMapping mapping = config.getMappingById("0");

      assertEquals("0", mapping.getId());
      assertEquals("/project/#{pid:viewProjectBean.projectId}/", mapping.getPattern());
      assertEquals("/faces/viewProject.xhtml", mapping.getViewId());
   }

}
