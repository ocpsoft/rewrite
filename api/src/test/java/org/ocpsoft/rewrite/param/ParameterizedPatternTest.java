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
package org.ocpsoft.rewrite.param;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.ocpsoft.rewrite.MockEvaluationContext;
import org.ocpsoft.rewrite.MockRewrite;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.util.ParseTools.CaptureType;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ParameterizedPatternTest
{
   private MockRewrite event;
   private EvaluationContext context;

   public static void initialize(ParameterStore store, Parameterized parameterized)
   {
      Set<String> names = parameterized.getRequiredParameterNames();
      for (String name : names)
      {
         store.get(name, new DefaultParameter(name));
      }

      parameterized.setParameterStore(store);
   }

   @Before
   public void before()
   {
      event = new MockRewrite();
      context = new MockEvaluationContext();
      context.put(ParameterStore.class, new DefaultParameterStore());
      context.put(ParameterValueStore.class, new DefaultParameterValueStore());
   }

   @Test
   public void testComplexMatchingWithMutipleAdjacentParameters() throws Exception
   {
      String url = "http://domain.com:8080/context/application/pathy?foo=bar&baz=bazaar";

      ParameterizedPatternParser path = new RegexParameterizedPatternParser(
               "{prefix}/application/{seg}{suffix}");

      ParameterStore store = DefaultParameterStore.getInstance(context);
      initialize(store, path);

      ((ConfigurableParameter<?>) store.get("prefix")).constrainedBy(new RegexConstraint(".*"));
      ((ConfigurableParameter<?>) store.get("seg")).constrainedBy(new RegexConstraint("[^/]+"));
      ((ConfigurableParameter<?>) store.get("suffix")).constrainedBy(new RegexConstraint("\\?.*"));

      initialize(store, path);

      assertThat(path.parse(url).matches()).isTrue();

      String[] expected = new String[] { "http://domain.com:8080/context", "pathy", "?foo=bar&baz=bazaar" };
      Map<Parameter<?>, String> parsed = path.parse(url).getParameters(context);

      int index = 0;
      for (Entry<Parameter<?>, String> entry : parsed.entrySet())
      {
         String value = entry.getValue();
         assertThat(value).isEqualTo(expected[index++]);
      }
   }

   @Test
   public void testComplexMatchingWithGroupsInRegexConstraints() throws Exception
   {
      String url = "http://domain.com:8080/context/application/pathy?foo=bar&baz=bazaar";

      ParameterizedPatternParser path = new RegexParameterizedPatternParser(
               "{prefix}/application/{seg}{suffix}");

      ParameterStore store = DefaultParameterStore.getInstance(context);
      initialize(store, path);

      ((ConfigurableParameter<?>) store.get("prefix")).constrainedBy(new RegexConstraint("((.*)?)"));
      ((ConfigurableParameter<?>) store.get("seg"))
               .constrainedBy(new RegexConstraint("([^/]+)"))
               .constrainedBy(new RegexConstraint("(pathy)"))
               .constrainedBy(new RegexConstraint("\\w{5}"));
      ((ConfigurableParameter<?>) store.get("suffix")).constrainedBy(new RegexConstraint("\\?.*"));

      initialize(store, path);

      assertThat(path.parse(url).matches()).isTrue();

      String[] expected = new String[] { "http://domain.com:8080/context", "pathy", "?foo=bar&baz=bazaar" };
      Map<Parameter<?>, String> parsed = path.parse(url).getParameters(context);

      int index = 0;
      for (Entry<Parameter<?>, String> entry : parsed.entrySet())
      {
         String value = entry.getValue();
         assertThat(value).isEqualTo(expected[index++]);
      }
   }

   @Test
   public void testEscapingWindowsFilePathsNoParams()
   {
      String pattern = "c:\\\\Users\\\\Admin\\\\Documents and Settings\\\\Folder";
      ParameterizedPatternParser parameterized = new RegexParameterizedPatternParser(CaptureType.BRACE, pattern);

      assertThat(parameterized.getRequiredParameterNames().size()).isEqualTo(0);
      assertThat(parameterized.parse(pattern).matches()).isFalse();
      String value = pattern.replaceAll("\\\\\\\\", "\\\\");
      assertThat(parameterized.parse(value).matches()).isTrue();
   }

   @Test
   public void testEscapingWindowsFilePathsWithParams()
   {
      String pattern = "c:\\\\Users\\\\{user}\\\\Documents and Settings\\\\Folder";
      ParameterizedPatternParser parameterized = new RegexParameterizedPatternParser(CaptureType.BRACE, pattern);

      assertThat(parameterized.getRequiredParameterNames().size()).isEqualTo(1);
      assertThat(parameterized.parse(pattern).matches()).isFalse();
      String value = pattern.replaceAll("\\\\\\\\", "\\\\");
      assertThat(parameterized.parse(value).matches()).isTrue();
   }

   @Test
   public void testEscapingParams()
   {
      String pattern = "Something \\{wicked this way comes.";
      ParameterizedPatternParser parameterized = new RegexParameterizedPatternParser(CaptureType.BRACE, pattern);

      assertThat(parameterized.getRequiredParameterNames().size()).isEqualTo(0);
      assertThat(parameterized.parse("Something {wicked this way comes.").matches()).isTrue();
   }

   @Test
   public void testEscapingParams1()
   {
      String pattern = "Something \\{wicked this way comes.}";
      ParameterizedPatternParser parameterized = new RegexParameterizedPatternParser(CaptureType.BRACE, pattern);

      assertThat(parameterized.getRequiredParameterNames().size()).isEqualTo(0);
      assertThat(parameterized.parse("Something {wicked this way comes.}").matches()).isTrue();
   }

   @Test
   public void testEscapingParams2()
   {
      String pattern = "Something \\{{wicked this way comes}.";
      ParameterizedPatternParser parameterized = new RegexParameterizedPatternParser(CaptureType.BRACE, pattern);

      assertThat(parameterized.getRequiredParameterNames().size()).isEqualTo(1);
      assertThat(parameterized.parse("Something {cool.").matches()).isTrue();
   }

   @Test
   public void testEscapingParams3()
   {
      String pattern = "Something \\{{wicked this way comes}. More {stuff}";
      ParameterizedPatternParser parameterized = new RegexParameterizedPatternParser(CaptureType.BRACE, pattern);

      assertThat(parameterized.getRequiredParameterNames().size()).isEqualTo(2);
      assertThat(parameterized.parse("Something {cool. More anything").matches()).isTrue();
   }

   @Test
   public void testEscapingParams4()
   {
      String pattern = "beginning \\{middle\\} end";
      ParameterizedPatternParser parameterized = new RegexParameterizedPatternParser(CaptureType.BRACE, pattern);

      assertThat(parameterized.getRequiredParameterNames().size()).isEqualTo(0);
      assertThat(parameterized.parse("beginning {middle\\} end").matches()).isTrue();
   }

   @Test
   public void testEscapingParamsWithNewlines()
   {
      String pattern = "beginning {\nmiddle\n} end";
      ParameterizedPatternParser parameterized = new RegexParameterizedPatternParser(CaptureType.BRACE, pattern);

      assertThat(parameterized.getRequiredParameterNames().size()).isEqualTo(1);
      assertThat(parameterized.parse("beginning middle end").matches()).isTrue();
   }

   @Test
   public void testEscapingParamsWithNewlines2()
   {
      String pattern = "beginning \\{\n                                 ...\n                             } end";
      ParameterizedPatternParser parameterized = new RegexParameterizedPatternParser(CaptureType.BRACE, pattern);

      assertThat(parameterized.getRequiredParameterNames().size()).isEqualTo(0);
      assertThat(parameterized.parse("beginning {\n                                 ...\n                             } end").matches()).isTrue();
   }

   @Test
   public void testEscapingWithNewlines()
   {
      String pattern = "beginning \\{\nmiddle\n} end";
      ParameterizedPatternParser parameterized = new RegexParameterizedPatternParser(CaptureType.BRACE, pattern);

      assertThat(parameterized.getRequiredParameterNames().size()).isEqualTo(0);
      assertThat(parameterized.parse("beginning {\nmiddle\n} end").matches()).isTrue();
   }

   @Test(expected = ParameterizedPatternSyntaxException.class)
   public void testIllegalEscapingParams()
   {
      String pattern = "Something {wicked\\}.";
      new RegexParameterizedPatternParser(CaptureType.BRACE, pattern);
   }

   @Test(expected = ParameterizedPatternSyntaxException.class)
   public void testIllegalEscaping()
   {
      String pattern = "\\c";
      new RegexParameterizedPatternParser(CaptureType.BRACE, pattern);
   }

   @Test(expected = ParameterizedPatternSyntaxException.class)
   public void testIllegalPartialEscapingAtStart()
   {
      String pattern = "\\";
      new RegexParameterizedPatternParser(CaptureType.BRACE, pattern);
   }

   @Test(expected = ParameterizedPatternSyntaxException.class)
   public void testIllegalPartialEscapingAtEnd()
   {
      String pattern = "asdfdsf\\";
      new RegexParameterizedPatternParser(CaptureType.BRACE, pattern);
   }

   @Test
   public void testCannotUseRegularExpressionsWithoutParameter()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternParser(CaptureType.BRACE, ".*");

      assertThat(path.getRequiredParameterNames().size()).isEqualTo(0);
      assertThat(path.parse("/omg/doesnt/matter").matches()).isFalse();
   }

   @Test
   public void testMatchesEmptyPath()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternParser(CaptureType.BRACE, "");

      assertThat(path.getRequiredParameterNames().size()).isEqualTo(0);
      assertThat(path.parse("").matches()).isTrue();
      Map<Parameter<?>, String> results = path.parse("").getParameters(context);
      assertThat(results).isNotNull();
   }

   @Test
   public void testMatchesEmptyPathNoTransforms()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternParser(CaptureType.BRACE, "");

      assertThat(path.getRequiredParameterNames().size()).isEqualTo(0);
      assertThat(path.parse("").matches()).isTrue();
      Map<Parameter<?>, String> results = path.parse("").getParameters(context);
      assertThat(results).isNotNull();
   }

   @Test
   public void testMatchesBarePath()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternParser("/");

      assertThat(path.getRequiredParameterNames().size()).isEqualTo(0);
      assertThat(path.parse("/").matches()).isTrue();

      Map<Parameter<?>, String> results = path.parse("/").getParameters(context);
      assertThat(results).isNotNull();
   }

   @Test
   public void testMatchesWithParameters()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternParser("[^/]+", "/{customer}/orders/{id}");

      ParameterStore parameters = DefaultParameterStore.getInstance(context);

      initialize(parameters, path);

      assertThat(parameters.size()).isEqualTo(2);
      assertThat(parameters.get("customer").getName()).isEqualTo("customer");
      assertThat(parameters.get("id").getName()).isEqualTo("id");

      Map<Parameter<?>, String> results = path.parse("/lincoln/orders/24").getParameters(context);
      assertThat(results.get(parameters.get("customer"))).isEqualTo("lincoln");
      assertThat(results.get(parameters.get("id"))).isEqualTo("24");
   }

   @Test
   public void testMatchesWithParametersNoTransforms()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternParser("[^/]+", "/{customer}/orders/{id}");

      ParameterStore parameters = DefaultParameterStore.getInstance(context);
      initialize(parameters, path);

      assertThat(parameters.size()).isEqualTo(2);
      assertThat(parameters.get("customer").getName()).isEqualTo("customer");
      assertThat(parameters.get("id").getName()).isEqualTo("id");

      Map<Parameter<?>, String> results = path.parse("/lincoln/orders/24").getParameters(context);
      assertThat(results.get(parameters.get("customer"))).isEqualTo("lincoln");
      assertThat(results.get(parameters.get("id"))).isEqualTo("24");
   }

   @Test
   public void testMatchesWithParametersRespectsTrailingCharsWithWildcardParameter()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternParser(".*", "/{customer}/");

      ParameterStore parameters = new DefaultParameterStore();
      initialize(parameters, path);

      assertThat(path.parse("/lincoln/").matches()).isTrue();
      assertThat(path.parse("/lincoln/foo").matches()).isFalse();
   }

   @Test
   public void testRegularExpressionsAreDisabled()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternParser("[^/]+", ".*/{customer}/");
      assertThat(path.parse(".*/lincoln/").matches()).isTrue();
      assertThat(path.parse("foobar/lincoln/").matches()).isFalse();
   }

   @Test
   public void testAccessExistingParameterSucceeds()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternParser("[^/]+", ".*/{customer}/");

      ParameterStore parameters = new DefaultParameterStore();
      initialize(parameters, path);

      Parameter<?> parameter = parameters.get("customer");
      assertThat(parameter).isNotNull();
   }

   @Test(expected = IllegalArgumentException.class)
   public void testAccessNonExistentParameterThrowsException()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternParser("[^/]+", ".*/{customer}/");

      ParameterStore parameters = new DefaultParameterStore();
      initialize(parameters, path);

      parameters.get("something else");
   }

   @Test
   public void testParametersUsedMultipleRequireSingleConfiguration()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternParser("/{f}/{f}/");

      ParameterStore parameters = new DefaultParameterStore();
      initialize(parameters, path);

      ((ConfigurableParameter<?>) parameters.get("f")).constrainedBy(new RegexConstraint("foo"));

      assertThat(path.parse("/foo/foo/").matches()).isTrue();
      assertThat(path.parse("/foo/bar/").matches()).isFalse();
   }

   @Test
   public void testParsesWithParametersRespectsTrailingCharsWithWildcardParameter()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternParser(".*", "/{customer}/");

      ParameterStore parameters = DefaultParameterStore.getInstance(context);
      initialize(parameters, path);

      assertThat(parameters.size()).isEqualTo(1);
      assertThat(parameters.get("customer").getName()).isEqualTo("customer");

      Map<Parameter<?>, String> results = path.parse("/lincoln/").getParameters(context);
      assertThat(results.get(parameters.get("customer"))).isEqualTo("lincoln");
   }

   @Test
   public void testParsesWithParametersEscapesTrailingChars()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternParser(".*", "/{customer}.");

      ParameterStore parameters = DefaultParameterStore.getInstance(context);
      initialize(parameters, path);

      assertThat(parameters.size()).isEqualTo(1);
      assertThat(parameters.get("customer").getName()).isEqualTo("customer");

      assertThat(path.parse("/lincolnX").matches()).isFalse();
      assertThat(path.parse("/lincolnX").isValid(event, context)).isFalse();
      assertThat(path.parse("/lincoln/").submit(event, context)).isFalse();
      assertThat(path.parse("/lincoln.").matches()).isTrue();
   }

   @Test
   public void testMatchesWithParametersAndTrailingSlash()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternBuilder("[^/]+", "/{customer}/orders/{id}/")
               .getParser();

      ParameterStore parameters = DefaultParameterStore.getInstance(context);
      initialize(parameters, path);

      assertThat(parameters.size()).isEqualTo(2);
      assertThat(parameters.get("customer").getName()).isEqualTo("customer");
      assertThat(parameters.get("id").getName()).isEqualTo("id");

      ParameterizedPatternResult result = path.parse("/lincoln/orders/24/");
      assertThat(result.matches()).isTrue();
      assertThat(result.isValid(event, context)).isTrue();
      assertThat(result.submit(event, context)).isTrue();
      Map<Parameter<?>, String> results = result.getParameters(context);
      assertThat(results.get(parameters.get("customer"))).isEqualTo("lincoln");
      assertThat(results.get(parameters.get("id"))).isEqualTo("24");
   }

   @Test(expected = IllegalArgumentException.class)
   public void testBuildNull()
   {
      ParameterizedPatternBuilder path = new RegexParameterizedPatternBuilder(null);
      assertThat(path.build(new LinkedHashMap<String, Object>())).isEqualTo("");
   }

   @Test
   public void testBuildEmpty()
   {
      ParameterizedPatternBuilder path = new RegexParameterizedPatternBuilder("");
      assertThat(path.build(new LinkedHashMap<String, Object>())).isEqualTo("");
   }

   @Test
   public void testBuildBarePath()
   {
      ParameterizedPatternBuilder path = new RegexParameterizedPatternBuilder("/");
      assertThat(path.build(new LinkedHashMap<String, Object>())).isEqualTo("/");
   }

   @Test
   public void testBuildWithMapParameters()
   {
      ParameterizedPatternBuilder path = new RegexParameterizedPatternParser("[^/]*", "/{customer}/orders/{id}")
               .getBuilder();
      Map<String, Object> map = new LinkedHashMap<String, Object>();
      map.put("customer", "lincoln");
      map.put("id", "24");
      assertThat(path.build(map)).isEqualTo("/lincoln/orders/24");
   }

   @Test
   public void testBuildWithListParameters()
   {
      ParameterizedPatternBuilder path = new RegexParameterizedPatternBuilder("[^/]*", "/{customer}/orders/{id}");
      assertThat(path.build(Arrays.<Object>asList("lincoln", "24"))).isEqualTo("/lincoln/orders/24");
   }

}
