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
package com.ocpsoft.pretty.faces.url;

import java.util.List;

import org.junit.Test;

import com.ocpsoft.pretty.faces.config.mapping.PathParameter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class URLPatternParserRegexOverrideTest
{

   @Test
   public void testRegexNamedParser() throws Exception
   {
      URLPatternParser parser = new URLPatternParser("/foo/#{ /(?!=admin)[^/]+/ named}/");
      List<PathParameter> params = parser.parse(new URL("/foo/love/"));
      assertThat(params.size()).isEqualTo(1);

      PathParameter p = params.get(0);
      assertThat(p.getPosition()).isEqualTo(0);
      assertThat(p.getValue()).isEqualTo("love");
      assertThat(p.getName()).isEqualTo("named");
      assertThat(p.getExpression().getELExpression()).isEqualTo("#{named}");
      assertThat(p.getRegex()).isEqualTo("(?!=admin)[^/]+");
   }

   @Test
   public void testMultiURLSegmentParsing() throws Exception
   {
      URLPatternParser parser = new URLPatternParser("/foo/#{ /.*/ named}/");
      List<PathParameter> params = parser.parse(new URL("/foo/love/again/"));
      assertThat(params.size()).isEqualTo(1);

      PathParameter p = params.get(0);
      assertThat(p.getPosition()).isEqualTo(0);
      assertThat(p.getValue()).isEqualTo("love/again");
      assertThat(p.getName()).isEqualTo("named");
      assertThat(p.getExpression().getELExpression()).isEqualTo("#{named}");
      assertThat(p.getRegex()).isEqualTo(".*");
   }

   @Test
   public void testLeadingTrailingSlashWithInternalSlashes() throws Exception
   {
      URLPatternParser parser = new URLPatternParser("/#{ /.*/ named}/");
      List<PathParameter> params = parser.parse(new URL("/foo/love/again/"));
      assertThat(params.size()).isEqualTo(1);

      PathParameter p = params.get(0);
      assertThat(p.getPosition()).isEqualTo(0);
      assertThat(p.getValue()).isEqualTo("foo/love/again");
      assertThat(p.getName()).isEqualTo("named");
      assertThat(p.getExpression().getELExpression()).isEqualTo("#{named}");
      assertThat(p.getRegex()).isEqualTo(".*");
   }

   @Test
   public void testMultiURLSegmentParsingInjected() throws Exception
   {
      URLPatternParser parser = new URLPatternParser("/foo/#{ /(\\\\d+/\\\\w+)/ inje.cted}/");
      List<PathParameter> params = parser.parse(new URL("/foo/2010/again/"));
      assertThat(params.size()).isEqualTo(1);

      PathParameter p = params.get(0);
      assertThat(p.getPosition()).isEqualTo(0);
      assertThat(p.getValue()).isEqualTo("2010/again");
      assertThat(p.getName()).isEqualTo("com.ocpsoft.vP_0");
      assertThat(p.getExpression().getELExpression()).isEqualTo("#{inje.cted}");
      assertThat(p.getRegex()).isEqualTo("(\\\\d+/\\\\w+)");
   }

   @Test
   public void testMultiURLSegmentParsingNamedNoTrailingSlash() throws Exception
   {
      URLPatternParser parser = new URLPatternParser("/foo/#{ /(\\\\d+/\\\\w+)/ inje.cted}/and-#{valued}");
      List<PathParameter> params = parser.parse(new URL("/foo/2010/again/and-avalue"));
      assertThat(params.size()).isEqualTo(2);

      PathParameter p = params.get(1);
      assertThat(p.getPosition()).isEqualTo(1);
      assertThat(p.getValue()).isEqualTo("avalue");
      assertThat(p.getName()).isEqualTo("valued");
      assertThat(p.getExpression().getELExpression()).isEqualTo("#{valued}");
      assertThat(p.getRegex()).isEqualTo("[^/]+");
   }

   @Test(expected = IllegalArgumentException.class)
   public void testRegexNamedParseInvalidURL() throws Exception
   {
      URLPatternParser parser = new URLPatternParser("/foo/#{ /(?!=admin)[^/]+/ named}/");
      parser.parse(new URL("/admin/love/"));
   }
}
