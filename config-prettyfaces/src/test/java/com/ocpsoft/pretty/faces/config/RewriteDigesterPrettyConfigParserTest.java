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

import static org.assertj.core.api.Assertions.assertThat;

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
      assertThat(c.getMatch()).isEqualTo("");
      assertThat(c.getSubstitute()).isEqualTo("");
      assertThat(c.getUrl()).isEqualTo("");
      assertThat(c.getRedirect()).isEqualTo(Redirect.PERMANENT);
      assertThat(c.isOutbound()).isEqualTo(true);
      assertThat(c.getToCase()).isEqualTo(Case.IGNORE);
      assertThat(c.getTrailingSlash()).isEqualTo(TrailingSlash.IGNORE);
   }

   @Test
   public void testParseRewriteEntries() throws Exception
   {
      List<RewriteRule> rules = config.getGlobalRewriteRules();
      RewriteRule r = rules.get(0);
      assertThat(r.getMatch()).isEqualTo("^(.*[^/])$");
      assertThat(r.getSubstitute()).isEqualTo("$1/");
      assertThat(r.getRedirect()).isEqualTo(Redirect.CHAIN);
      assertThat(r.isOutbound()).isTrue();
      assertThat(r.getToCase()).isEqualTo(Case.IGNORE);
      assertThat(r.getTrailingSlash()).isEqualTo(TrailingSlash.IGNORE);

      r = rules.get(1);
      assertThat(r.getMatch()).isEqualTo("");
      assertThat(r.getSubstitute()).isEqualTo("");
      assertThat(r.getRedirect()).isEqualTo(Redirect.PERMANENT);
      assertThat(r.isOutbound()).isEqualTo(true);
      assertThat(r.getToCase()).isEqualTo(Case.LOWERCASE);
      assertThat(r.getTrailingSlash()).isEqualTo(TrailingSlash.APPEND);

      r = rules.get(2);
      assertThat(r.getMatch()).isEqualTo("");
      assertThat(r.getSubstitute()).isEqualTo("");
      assertThat(r.getUrl()).isEqualTo("http://www.google.com");
      assertThat(r.getRedirect()).isEqualTo(Redirect.TEMPORARY);
      assertThat(r.isOutbound()).isEqualTo(false);
      assertThat(r.getToCase()).isEqualTo(Case.UPPERCASE);
      assertThat(r.getTrailingSlash()).isEqualTo(TrailingSlash.REMOVE);
   }

   @Test
   public void testParse()
   {
      UrlMapping mapping = config.getMappingById("0");

      assertThat(mapping.getId()).isEqualTo("0");
      assertThat(mapping.getPattern()).isEqualTo("/project/#{pid:viewProjectBean.projectId}/");
      assertThat(mapping.getViewId()).isEqualTo("/faces/viewProject.xhtml");
   }

}
