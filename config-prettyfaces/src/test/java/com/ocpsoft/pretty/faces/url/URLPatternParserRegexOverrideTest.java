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

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.ocpsoft.pretty.faces.config.mapping.PathParameter;

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
      assertEquals(1, params.size());

      PathParameter p = params.get(0);
      assertEquals(0, p.getPosition());
      assertEquals("love", p.getValue());
      assertEquals("named", p.getName());
      assertEquals("#{named}", p.getExpression().getELExpression());
      assertEquals("(?!=admin)[^/]+", p.getRegex());
   }

   @Test
   public void testMultiURLSegmentParsing() throws Exception
   {
      URLPatternParser parser = new URLPatternParser("/foo/#{ /.*/ named}/");
      List<PathParameter> params = parser.parse(new URL("/foo/love/again/"));
      assertEquals(1, params.size());

      PathParameter p = params.get(0);
      assertEquals(0, p.getPosition());
      assertEquals("love/again", p.getValue());
      assertEquals("named", p.getName());
      assertEquals("#{named}", p.getExpression().getELExpression());
      assertEquals(".*", p.getRegex());
   }

   @Test
   public void testLeadingTrailingSlashWithInternalSlashes() throws Exception
   {
      URLPatternParser parser = new URLPatternParser("/#{ /.*/ named}/");
      List<PathParameter> params = parser.parse(new URL("/foo/love/again/"));
      assertEquals(1, params.size());

      PathParameter p = params.get(0);
      assertEquals(0, p.getPosition());
      assertEquals("foo/love/again", p.getValue());
      assertEquals("named", p.getName());
      assertEquals("#{named}", p.getExpression().getELExpression());
      assertEquals(".*", p.getRegex());
   }

   @Test
   public void testMultiURLSegmentParsingInjected() throws Exception
   {
      URLPatternParser parser = new URLPatternParser("/foo/#{ /(\\\\d+/\\\\w+)/ inje.cted}/");
      List<PathParameter> params = parser.parse(new URL("/foo/2010/again/"));
      assertEquals(1, params.size());

      PathParameter p = params.get(0);
      assertEquals(0, p.getPosition());
      assertEquals("2010/again", p.getValue());
      assertEquals("com.ocpsoft.vP_0", p.getName());
      assertEquals("#{inje.cted}", p.getExpression().getELExpression());
      assertEquals("(\\\\d+/\\\\w+)", p.getRegex());
   }

   @Test
   public void testMultiURLSegmentParsingNamedNoTrailingSlash() throws Exception
   {
      URLPatternParser parser = new URLPatternParser("/foo/#{ /(\\\\d+/\\\\w+)/ inje.cted}/and-#{valued}");
      List<PathParameter> params = parser.parse(new URL("/foo/2010/again/and-avalue"));
      assertEquals(2, params.size());

      PathParameter p = params.get(1);
      assertEquals(1, p.getPosition());
      assertEquals("avalue", p.getValue());
      assertEquals("valued", p.getName());
      assertEquals("#{valued}", p.getExpression().getELExpression());
      assertEquals("[^/]+", p.getRegex());
   }

   @Test(expected = IllegalArgumentException.class)
   public void testRegexNamedParseInvalidURL() throws Exception
   {
      URLPatternParser parser = new URLPatternParser("/foo/#{ /(?!=admin)[^/]+/ named}/");
      parser.parse(new URL("/admin/love/"));
   }
}
