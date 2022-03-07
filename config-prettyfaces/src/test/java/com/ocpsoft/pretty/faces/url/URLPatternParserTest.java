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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.ocpsoft.pretty.PrettyException;
import com.ocpsoft.pretty.faces.config.mapping.PathParameter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class URLPatternParserTest
{
   @Test
   public void testSimplestMatch() throws Exception
   {
      URLPatternParser parser = new URLPatternParser("/");
      assertThat(parser.matches(new URL("/"))).isTrue();
      List<PathParameter> parameters = parser.parse(new URL("/"));
      assertThat(parameters.size()).isEqualTo(0);
   }

   @Test
   public void testGetNamedParameterList()
   {
      URLPatternParser namedParamParser = new URLPatternParser("/foo/#{named}/");
      List<PathParameter> params = namedParamParser.parse(new URL("/foo/love/"));
      assertThat(params.size()).isEqualTo(1);

      PathParameter p = params.get(0);
      assertThat(p.getPosition()).isEqualTo(0);
      assertThat(p.getValue()).isEqualTo("love");
      assertThat(p.getName()).isEqualTo("named");
      assertThat(p.getExpression().getELExpression()).isEqualTo("#{named}");
   }

   @Test
   public void testGetInjectedParameterList()
   {
      URLPatternParser namedParamParser = new URLPatternParser("/foo/#{:injected}/");
      List<PathParameter> params = namedParamParser.parse(new URL("/foo/love/"));
      assertThat(params.size()).isEqualTo(1);

      PathParameter p = params.get(0);
      assertThat(p.getPosition()).isEqualTo(0);
      assertThat(p.getValue()).isEqualTo("love");
      assertThat(p.getExpression().getELExpression()).isEqualTo("#{injected}");
   }

   @Test
   public void testGetNamedValuedParameterList()
   {
      URLPatternParser namedValuedParamParser = new URLPatternParser("/foo/#{named:bean.value}/");
      List<PathParameter> params = namedValuedParamParser.parse(new URL("/foo/love/"));
      assertThat(params.size()).isEqualTo(1);

      PathParameter p = params.get(0);
      assertThat(p.getPosition()).isEqualTo(0);
      assertThat(p.getValue()).isEqualTo("love");
      assertThat(p.getName()).isEqualTo("named");
      assertThat(p.getExpression().getELExpression()).isEqualTo("#{bean.value}");
   }

   @Test
   public void testMatches()
   {
      URLPatternParser parser = new URLPatternParser(
               "/project/#{paramsBean.project}/#{paramsBean.iteration}/#{paramsBean.story}");
      assertThat(parser.matches(new URL("/project/starfish1/starfish2/story1"))).isTrue();
      assertThat(parser.matches(new URL("project/starfish1/starfish2/story1"))).isFalse();
      assertThat(parser.matches(new URL("/project/starfish1/starfish2/story1/"))).isFalse();
      assertThat(parser.matches(new URL("project/starfish1/starfish2/story1/test"))).isFalse();
      assertThat(parser.matches(new URL("project/starfish2/story1"))).isFalse();
      assertThat(parser.matches(new URL("project/starfish1/starfish2"))).isFalse();
      assertThat(parser.matches(new URL("project/starfish1/starfish2/"))).isFalse();
   }

   @Test
   public void testMatchesPrefixedSegmentExpressionInjected()
   {
      URLPatternParser parser = new URLPatternParser("/project/#{paramsBean.project}/story-#{paramsBean.story}/");
      assertThat(parser.matches(new URL("/project/starfish/story-1/"))).isTrue();
      assertThat(parser.matches(new URL("/project/starfish/story-1/-"))).isFalse();
      assertThat(parser.matches(new URL("/project/starfish/story-/"))).isFalse();
      assertThat(parser.matches(new URL("project/starfish/story-23/"))).isFalse();
   }

   @Test
   public void testMatchesPrefixedSegmentExpressionNamed()
   {
      URLPatternParser parser = new URLPatternParser("/project/#{paramsBean.project}/story-#{story}/");
      assertThat(parser.matches(new URL("/project/starfish/story-1/"))).isTrue();
   }

   @Test
   public void testMatchesMultipleExpressionNamedSegment()
   {
      URLPatternParser parser = new URLPatternParser("/project/#{paramsBean.project}/story-#{story}-#{comment}");
      assertThat(parser.matches(new URL("/project/starfish/story-1-23"))).isTrue();
      assertThat(parser.matches(new URL("/project/starfish/story-1-23/"))).isFalse();
   }

   @Test
   public void testMatchesPrefixedSegmentExpressionNamedInjected()
   {
      URLPatternParser parser = new URLPatternParser("/project/#{paramsBean.project}/story-#{story:paramsBean.story}/");
      assertThat(parser.matches(new URL("/project/starfish/story-1/"))).isTrue();
   }

   @Test
   public void testGetMappedParameters()
   {
      URLPatternParser parser = new URLPatternParser(
               "/project/#{paramsBean.project}/#{paramsBean.iteration}/#{paramsBean.story}");
      List<PathParameter> params = parser.parse(new URL("/project/starfish1/sprint1/story1"));
      assertThat(params.size()).isEqualTo(3);
      assertThat(params.get(0).getValue()).isEqualTo("starfish1");
      assertThat(params.get(1).getValue()).isEqualTo("sprint1");
      assertThat(params.get(2).getValue()).isEqualTo("story1");
   }

   @Test
   public void testGetMappedParametersDuplicatesAreRepresented()
   {
      URLPatternParser duplicateParamsParser = new URLPatternParser(
               "/project/#{paramsBean.project}/#{paramsBean.project}/#{paramsBean.story}");
      List<PathParameter> params = duplicateParamsParser.parse(new URL("/project/starfish1/sprint1/story1"));
      assertThat(params.size()).isEqualTo(3);
      assertThat(params.get(0).getValue()).isEqualTo("starfish1");
      assertThat(params.get(1).getValue()).isEqualTo("sprint1");
      assertThat(params.get(2).getValue()).isEqualTo("story1");
   }

   @Test
   public void testGetMappedParameterList()
   {
      URLPatternParser parser = new URLPatternParser(
               "/project/#{paramsBean.project}/#{paramsBean.iteration}/#{paramsBean.story}");
      List<PathParameter> params = parser.parse(new URL("/project/starfish1/sprint1/story1"));
      assertThat(params.size()).isEqualTo(3);

      PathParameter p = params.get(0);
      assertThat(p.getPosition()).isEqualTo(0);
      assertThat(p.getValue()).isEqualTo("starfish1");
      assertThat(p.getExpression().getELExpression()).isEqualTo("#{paramsBean.project}");

      PathParameter p1 = params.get(1);
      assertThat(p1.getPosition()).isEqualTo(1);
      assertThat(p1.getValue()).isEqualTo("sprint1");
      assertThat(p1.getExpression().getELExpression()).isEqualTo("#{paramsBean.iteration}");

      PathParameter p2 = params.get(2);
      assertThat(p2.getPosition()).isEqualTo(2);
      assertThat(p2.getValue()).isEqualTo("story1");
      assertThat(p2.getExpression().getELExpression()).isEqualTo("#{paramsBean.story}");
   }

   @Test
   public void testGetMappedUrl()
   {
      URLPatternParser parser = new URLPatternParser(
               "/project/#{paramsBean.project}/#{paramsBean.iteration}/#{paramsBean.story}");
      String mappedUrl = parser.getMappedURL("p1", 22, 55).toURL();
      assertThat(mappedUrl).isEqualTo("/project/p1/22/55");
   }

   @Test
   public void testGetParameterCount()
   {
      URLPatternParser parser = new URLPatternParser(
               "/project/#{paramsBean.project}/#{paramsBean.iteration}/#{paramsBean.story}");
      assertThat(parser.getParameterCount()).isEqualTo(3);
   }

   @Test
   public void testGetMappedUrlWithList()
   {
      URLPatternParser parser = new URLPatternParser(
               "/project/#{paramsBean.project}/#{paramsBean.iteration}/#{paramsBean.story}");
      List<Object> params = new ArrayList<Object>();
      params.add("p1");
      params.add(22);
      params.add(55);
      String mappedUrl = parser.getMappedURL(params).toURL();
      assertThat(mappedUrl).isEqualTo("/project/p1/22/55");
      params.remove(2);
      try
      {
         mappedUrl = parser.getMappedURL(params, 55).toURL();
         assertThat(mappedUrl).isEqualTo("/project/p1/22/55");
         assertThat(false).as("Parameter count is wrong.").isTrue();
      }
      catch (PrettyException pe)
      {
         assertThat(true).isTrue();
      }
      assertThat(parser.getMappedURL(params, 22, 55).toURL()).isNotSameAs("/project/p1/22/55");
   }

   @Test
   public void testGetMappedUrlWithArray()
   {
      URLPatternParser parser = new URLPatternParser(
               "/project/#{paramsBean.project}/#{paramsBean.iteration}/#{paramsBean.story}");
      Object[] params = new Object[3];
      params[0] = "p1";
      params[1] = 22;
      params[2] = 55;
      String mappedUrl = parser.getMappedURL(params).toURL();
      assertThat(mappedUrl).isEqualTo("/project/p1/22/55");
      params[2] = null;
      params[1] = null;
      try
      {
         mappedUrl = parser.getMappedURL(params, 55).toURL();
         assertThat(mappedUrl).isEqualTo("/project/p1/22/55");
         assertThat(false).as("An exception should have been thrown by now").isTrue();
      }
      catch (PrettyException pe)
      {
         assertThat(true).isTrue();
      }
      assertThat(parser.getMappedURL(params, 22, 55).toURL()).isNotSameAs("/project/p1/22/55");
   }

   @Test
   public void testGetMappedUrlWithMap()
   {
      URLPatternParser parser = new URLPatternParser(
               "/project/#{paramsBean.project}/#{paramsBean.iteration}/#{paramsBean.story}");
      Map<String, String> params = new HashMap<String, String>();
      params.put("1", "p1");
      params.put("2", "22");
      params.put("3", "55");
      try
      {
         String mappedUrl = parser.getMappedURL(params).toURL();
         assertThat(mappedUrl).isEqualTo("An exception should have been thrown: Map parameters are not supported at this time.");
      }
      catch (PrettyException pe)
      {
         assertThat(true).isTrue();
      }
      assertThat(parser.getMappedURL(params, 22, 55).toURL()).isNotSameAs("/project/p1/22/55");
   }

   @Test
   public void testGetMappedUrlWithListWithoutParams()
   {
      URLPatternParser noParamsParser = new URLPatternParser("/no/param");
      List<?> params = new ArrayList<Object>();
      String mappedUrl = noParamsParser.getMappedURL(params).toURL();
      assertThat(mappedUrl).as("Empty list failed").isEqualTo("/no/param");
   }

   @Test
   public void testGetMappedUrlNullListWithoutParams()
   {
      URLPatternParser noParamsParser = new URLPatternParser("/no/param");
      List<?> params = null;
      String mappedUrl = noParamsParser.getMappedURL(params).toURL();
      assertThat(mappedUrl).as("Null param failed").isEqualTo("/no/param");
   }

   @Test
   public void testGetMappedUrlWithRegexes()
   {
      URLPatternParser regexParser = new URLPatternParser("/(foo|bar|baz|cat|dog).jsf");
      List<PathParameter> params = regexParser.parse(new URL("/foo.jsf"));
      assertThat(params.isEmpty()).isTrue();
   }
   
   @Test
   public void testBackslashHandling()
   {
      
      // simple path parameter
      URLPatternParser regexParser = new URLPatternParser("/#{string}/");
      
      // parse an URL containing a \ character
      List<PathParameter> params = regexParser.parse(new URL("/\\/"));
      assertThat(params.size()).isEqualTo(1);
      assertThat(params.get(0).getValue()).isEqualTo("\\");
      
      // generate URL
      URL url = regexParser.getMappedURL(new Object[] { "\\" });
      assertThat(url.toURL()).isEqualTo("/\\/");
   }
}
