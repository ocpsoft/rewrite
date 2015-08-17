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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ocpsoft.rewrite.MockEvaluationContext;
import org.ocpsoft.rewrite.MockRewrite;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.util.ParseTools.CaptureType;

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

      Assert.assertTrue(path.parse(url).matches());

      String[] expected = new String[] { "http://domain.com:8080/context", "pathy", "?foo=bar&baz=bazaar" };
      Map<Parameter<?>, String> parsed = path.parse(url).getParameters(context);

      int index = 0;
      for (Entry<Parameter<?>, String> entry : parsed.entrySet())
      {
         String value = entry.getValue();
         Assert.assertEquals(expected[index++], value);
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

      Assert.assertTrue(path.parse(url).matches());

      String[] expected = new String[] { "http://domain.com:8080/context", "pathy", "?foo=bar&baz=bazaar" };
      Map<Parameter<?>, String> parsed = path.parse(url).getParameters(context);

      int index = 0;
      for (Entry<Parameter<?>, String> entry : parsed.entrySet())
      {
         String value = entry.getValue();
         Assert.assertEquals(expected[index++], value);
      }
   }

   @Test
   public void testEscapingWindowsFilePathsNoParams()
   {
      String pattern = "c:\\\\Users\\\\Admin\\\\Documents and Settings\\\\Folder";
      ParameterizedPatternParser parameterized = new RegexParameterizedPatternParser(CaptureType.BRACE, pattern);

      Assert.assertEquals(0, parameterized.getRequiredParameterNames().size());
      Assert.assertFalse(parameterized.parse(pattern).matches());
      String value = pattern.replaceAll("\\\\\\\\", "\\\\");
      Assert.assertTrue(parameterized.parse(value).matches());
   }

   @Test
   public void testEscapingWindowsFilePathsWithParams()
   {
      String pattern = "c:\\\\Users\\\\{user}\\\\Documents and Settings\\\\Folder";
      ParameterizedPatternParser parameterized = new RegexParameterizedPatternParser(CaptureType.BRACE, pattern);

      Assert.assertEquals(1, parameterized.getRequiredParameterNames().size());
      Assert.assertFalse(parameterized.parse(pattern).matches());
      String value = pattern.replaceAll("\\\\\\\\", "\\\\");
      Assert.assertTrue(parameterized.parse(value).matches());
   }

   @Test
   public void testEscapingParams()
   {
      String pattern = "Something \\{wicked this way comes.";
      ParameterizedPatternParser parameterized = new RegexParameterizedPatternParser(CaptureType.BRACE, pattern);

      Assert.assertEquals(0, parameterized.getRequiredParameterNames().size());
      Assert.assertTrue(parameterized.parse("Something {wicked this way comes.").matches());
   }

   @Test
   public void testEscapingParams1()
   {
      String pattern = "Something \\{wicked this way comes.}";
      ParameterizedPatternParser parameterized = new RegexParameterizedPatternParser(CaptureType.BRACE, pattern);

      Assert.assertEquals(0, parameterized.getRequiredParameterNames().size());
      Assert.assertTrue(parameterized.parse("Something {wicked this way comes.}").matches());
   }

   @Test
   public void testEscapingParams2()
   {
      String pattern = "Something \\{{wicked this way comes}.";
      ParameterizedPatternParser parameterized = new RegexParameterizedPatternParser(CaptureType.BRACE, pattern);

      Assert.assertEquals(1, parameterized.getRequiredParameterNames().size());
      Assert.assertTrue(parameterized.parse("Something {cool.").matches());
   }

   @Test
   public void testEscapingParams3()
   {
      String pattern = "Something \\{{wicked this way comes}. More {stuff}";
      ParameterizedPatternParser parameterized = new RegexParameterizedPatternParser(CaptureType.BRACE, pattern);

      Assert.assertEquals(2, parameterized.getRequiredParameterNames().size());
      Assert.assertTrue(parameterized.parse("Something {cool. More anything").matches());
   }

   @Test
   public void testEscapingParams4()
   {
      String pattern = "beginning \\{middle\\} end";
      ParameterizedPatternParser parameterized = new RegexParameterizedPatternParser(CaptureType.BRACE, pattern);

      Assert.assertEquals(0, parameterized.getRequiredParameterNames().size());
      Assert.assertTrue(parameterized.parse("beginning {middle\\} end").matches());
   }

   @Test
   public void testEscapingParamsWithNewlines()
   {
      String pattern = "beginning {\nmiddle\n} end";
      ParameterizedPatternParser parameterized = new RegexParameterizedPatternParser(CaptureType.BRACE, pattern);

      Assert.assertEquals(1, parameterized.getRequiredParameterNames().size());
      Assert.assertTrue(parameterized.parse("beginning middle end").matches());
   }

   @Test
   public void testEscapingParamsWithNewlines2()
   {
      String pattern = "beginning \\{\n                                 ...\n                             } end";
      ParameterizedPatternParser parameterized = new RegexParameterizedPatternParser(CaptureType.BRACE, pattern);

      Assert.assertEquals(0, parameterized.getRequiredParameterNames().size());
      Assert.assertTrue(parameterized.parse("beginning {\n                                 ...\n                             } end").matches());
   }

   @Test
   public void testEscapingWithNewlines()
   {
      String pattern = "beginning \\{\nmiddle\n} end";
      ParameterizedPatternParser parameterized = new RegexParameterizedPatternParser(CaptureType.BRACE, pattern);

      Assert.assertEquals(0, parameterized.getRequiredParameterNames().size());
      Assert.assertTrue(parameterized.parse("beginning {\nmiddle\n} end").matches());
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

      Assert.assertEquals(0, path.getRequiredParameterNames().size());
      Assert.assertFalse(path.parse("/omg/doesnt/matter").matches());
   }

   @Test
   public void testMatchesEmptyPath()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternParser(CaptureType.BRACE, "");

      Assert.assertEquals(0, path.getRequiredParameterNames().size());
      Assert.assertTrue(path.parse("").matches());
      Map<Parameter<?>, String> results = path.parse("").getParameters(context);
      Assert.assertNotNull(results);
   }

   @Test
   public void testMatchesEmptyPathNoTransforms()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternParser(CaptureType.BRACE, "");

      Assert.assertEquals(0, path.getRequiredParameterNames().size());
      Assert.assertTrue(path.parse("").matches());
      Map<Parameter<?>, String> results = path.parse("").getParameters(context);
      Assert.assertNotNull(results);
   }

   @Test
   public void testMatchesBarePath()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternParser("/");

      Assert.assertEquals(0, path.getRequiredParameterNames().size());
      Assert.assertTrue(path.parse("/").matches());

      Map<Parameter<?>, String> results = path.parse("/").getParameters(context);
      Assert.assertNotNull(results);
   }

   @Test
   public void testMatchesWithParameters()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternParser("[^/]+", "/{customer}/orders/{id}");

      ParameterStore parameters = DefaultParameterStore.getInstance(context);

      initialize(parameters, path);

      Assert.assertEquals(2, parameters.size());
      Assert.assertEquals("customer", parameters.get("customer").getName());
      Assert.assertEquals("id", parameters.get("id").getName());

      Map<Parameter<?>, String> results = path.parse("/lincoln/orders/24").getParameters(context);
      Assert.assertEquals("lincoln", results.get(parameters.get("customer")));
      Assert.assertEquals("24", results.get(parameters.get("id")));
   }

   @Test
   public void testMatchesWithParametersNoTransforms()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternParser("[^/]+", "/{customer}/orders/{id}");

      ParameterStore parameters = DefaultParameterStore.getInstance(context);
      initialize(parameters, path);

      Assert.assertEquals(2, parameters.size());
      Assert.assertEquals("customer", parameters.get("customer").getName());
      Assert.assertEquals("id", parameters.get("id").getName());

      Map<Parameter<?>, String> results = path.parse("/lincoln/orders/24").getParameters(context);
      Assert.assertEquals("lincoln", results.get(parameters.get("customer")));
      Assert.assertEquals("24", results.get(parameters.get("id")));
   }

   @Test
   public void testMatchesWithParametersRespectsTrailingCharsWithWildcardParameter()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternParser(".*", "/{customer}/");

      ParameterStore parameters = new DefaultParameterStore();
      initialize(parameters, path);

      Assert.assertTrue(path.parse("/lincoln/").matches());
      Assert.assertFalse(path.parse("/lincoln/foo").matches());
   }

   @Test
   public void testRegularExpressionsAreDisabled()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternParser("[^/]+", ".*/{customer}/");
      Assert.assertTrue(path.parse(".*/lincoln/").matches());
      Assert.assertFalse(path.parse("foobar/lincoln/").matches());
   }

   @Test
   public void testAccessExistingParameterSucceeds()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternParser("[^/]+", ".*/{customer}/");

      ParameterStore parameters = new DefaultParameterStore();
      initialize(parameters, path);

      Parameter<?> parameter = parameters.get("customer");
      Assert.assertNotNull(parameter);
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

      Assert.assertTrue(path.parse("/foo/foo/").matches());
      Assert.assertFalse(path.parse("/foo/bar/").matches());
   }

   @Test
   public void testParsesWithParametersRespectsTrailingCharsWithWildcardParameter()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternParser(".*", "/{customer}/");

      ParameterStore parameters = DefaultParameterStore.getInstance(context);
      initialize(parameters, path);

      Assert.assertEquals(1, parameters.size());
      Assert.assertEquals("customer", parameters.get("customer").getName());

      Map<Parameter<?>, String> results = path.parse("/lincoln/").getParameters(context);
      Assert.assertEquals("lincoln", results.get(parameters.get("customer")));
   }

   @Test
   public void testParsesWithParametersEscapesTrailingChars()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternParser(".*", "/{customer}.");

      ParameterStore parameters = DefaultParameterStore.getInstance(context);
      initialize(parameters, path);

      Assert.assertEquals(1, parameters.size());
      Assert.assertEquals("customer", parameters.get("customer").getName());

      Assert.assertFalse(path.parse("/lincolnX").matches());
      Assert.assertFalse(path.parse("/lincolnX").isValid(event, context));
      Assert.assertFalse(path.parse("/lincoln/").submit(event, context));
      Assert.assertTrue(path.parse("/lincoln.").matches());
   }

   @Test
   public void testMatchesWithParametersAndTrailingSlash()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternBuilder("[^/]+", "/{customer}/orders/{id}/")
               .getParser();

      ParameterStore parameters = DefaultParameterStore.getInstance(context);
      initialize(parameters, path);

      Assert.assertEquals(2, parameters.size());
      Assert.assertEquals("customer", parameters.get("customer").getName());
      Assert.assertEquals("id", parameters.get("id").getName());

      ParameterizedPatternResult result = path.parse("/lincoln/orders/24/");
      Assert.assertTrue(result.matches());
      Assert.assertTrue(result.isValid(event, context));
      Assert.assertTrue(result.submit(event, context));
      Map<Parameter<?>, String> results = result.getParameters(context);
      Assert.assertEquals("lincoln", results.get(parameters.get("customer")));
      Assert.assertEquals("24", results.get(parameters.get("id")));
   }

   @Test(expected = IllegalArgumentException.class)
   public void testBuildNull()
   {
      ParameterizedPatternBuilder path = new RegexParameterizedPatternBuilder(null);
      Assert.assertEquals("", path.build(new LinkedHashMap<String, Object>()));
   }

   @Test
   public void testBuildEmpty()
   {
      ParameterizedPatternBuilder path = new RegexParameterizedPatternBuilder("");
      Assert.assertEquals("", path.build(new LinkedHashMap<String, Object>()));
   }

   @Test
   public void testBuildBarePath()
   {
      ParameterizedPatternBuilder path = new RegexParameterizedPatternBuilder("/");
      Assert.assertEquals("/", path.build(new LinkedHashMap<String, Object>()));
   }

   @Test
   public void testBuildWithMapParameters()
   {
      ParameterizedPatternBuilder path = new RegexParameterizedPatternParser("[^/]*", "/{customer}/orders/{id}")
               .getBuilder();
      Map<String, Object> map = new LinkedHashMap<String, Object>();
      map.put("customer", "lincoln");
      map.put("id", "24");
      Assert.assertEquals("/lincoln/orders/24", path.build(map));
   }

   @Test
   public void testBuildWithListParameters()
   {
      ParameterizedPatternBuilder path = new RegexParameterizedPatternBuilder("[^/]*", "/{customer}/orders/{id}");
      Assert.assertEquals("/lincoln/orders/24", path.build(Arrays.<Object> asList("lincoln", "24")));
   }

}
