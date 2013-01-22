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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.ocpsoft.rewrite.mock.MockEvaluationContext;
import org.ocpsoft.rewrite.mock.MockRewrite;
import org.ocpsoft.rewrite.util.Maps;
import org.ocpsoft.rewrite.util.ParseTools.CaptureType;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public class ParameterizedPatternTest
{
   private MockEvaluationContext context = new MockEvaluationContext();
   private MockRewrite rewrite = new MockRewrite();

   @After
   public void after()
   {
      context = new MockEvaluationContext();
      rewrite = new MockRewrite();
   }

   @Test
   public void testComplexMatchingWithMutipleAdjacentParameters() throws Exception
   {
      String url = "http://domain.com:8080/context/application/pathy?foo=bar&baz=bazaar";

      ParameterizedPatternParser parameterizedPattern = new RegexParameterizedPatternParser(
               "{prefix}/application/{seg}{suffix}");
      parameterizedPattern.getParameterStore().get("prefix").matches(".*");
      parameterizedPattern.getParameterStore().get("seg").matches("[^/]+");
      parameterizedPattern.getParameterStore().get("suffix").matches("\\?.*");

      Assert.assertTrue(parameterizedPattern.matches(rewrite, new MockEvaluationContext(), url));

      int index = 0;
      String[] expected = new String[] { "http://domain.com:8080/context", "pathy", "?foo=bar&baz=bazaar" };
      Map<ParameterizedPatternParameter, String[]> parsed = parameterizedPattern.parse(rewrite,
               new MockEvaluationContext(), url);
      for (Entry<ParameterizedPatternParameter, String[]> entry : parsed.entrySet()) {
         String[] value = entry.getValue();
         for (int i = 0; i < value.length; i++) {
            Assert.assertEquals(expected[index++], value[0]);
         }
      }
   }

   @Test
   public void testCannotUseRegularExpressionsWithoutParameter()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternParser(CaptureType.BRACE, ".*");

      Assert.assertEquals(0, path.getParameterStore().size());
      Assert.assertFalse(path.matches(rewrite, context, "/omg/doesnt/matter"));
   }

   @Test
   public void testMatchesEmptyPath()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternParser(CaptureType.BRACE, "");

      Assert.assertEquals(0, path.getParameterStore().size());
      Assert.assertTrue(path.matches(rewrite, context, ""));
      Map<ParameterizedPatternParameter, String[]> results = path.parse(rewrite, context, "");
      Assert.assertNotNull(results);
   }

   @Test
   public void testMatchesEmptyPathNoTransforms()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternParser(CaptureType.BRACE, "");

      Assert.assertEquals(0, path.getParameterStore().size());
      Assert.assertTrue(path.matches(rewrite, context, ""));
      Map<ParameterizedPatternParameter, String[]> results = path.parse("");
      Assert.assertNotNull(results);
   }

   @Test
   public void testMatchesBarePath()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternParser("/");

      Assert.assertEquals(0, path.getParameterStore().size());
      Assert.assertTrue(path.matches(rewrite, context, "/"));

      Map<ParameterizedPatternParameter, String[]> results = path.parse(rewrite, context, "/");
      Assert.assertNotNull(results);
   }

   @Test
   public void testMatchesWithParameters()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternParser("[^/]+", "/{customer}/orders/{id}");

      ParameterStore<ParameterizedPatternParameter> parameters = path.getParameterStore();
      Assert.assertEquals(2, parameters.size());
      Assert.assertEquals("customer", parameters.get("customer").getName());
      Assert.assertEquals("id", parameters.get("id").getName());

      Map<ParameterizedPatternParameter, String[]> results = path.parse(rewrite, context, "/lincoln/orders/24");
      Assert.assertEquals("lincoln", results.get(path.getParameterStore().get("customer"))[0]);
      Assert.assertEquals("24", results.get(path.getParameterStore().get("id"))[0]);
   }

   @Test
   public void testMatchesWithParametersNoTransforms()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternParser("[^/]+", "/{customer}/orders/{id}");

      Map<String, ParameterizedPatternParameter> parameters = path.getParameterStore();
      Assert.assertEquals(2, parameters.size());
      Assert.assertEquals("customer", parameters.get("customer").getName());
      Assert.assertEquals("id", parameters.get("id").getName());

      Map<ParameterizedPatternParameter, String[]> results = path.parse("/lincoln/orders/24");
      Assert.assertEquals("lincoln", results.get(path.getParameterStore().get("customer"))[0]);
      Assert.assertEquals("24", results.get(path.getParameterStore().get("id"))[0]);
   }

   @Test
   public void testMatchesWithParametersRespectsTrailingCharsWithWildcardParameter()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternParser(".*", "/{customer}/");
      Assert.assertTrue(path.matches(rewrite, context, "/lincoln/"));
      Assert.assertFalse(path.matches(rewrite, context, "/lincoln/foo"));
   }

   @Test
   public void testRegularExpressionsAreDisabled()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternParser("[^/]+", ".*/{customer}/");
      Assert.assertTrue(path.matches(rewrite, context, ".*/lincoln/"));
      Assert.assertFalse(path.matches(rewrite, context, "foobar/lincoln/"));
   }

   @Test(expected = IllegalArgumentException.class)
   public void testAccessNonExistentParameterThrowsException()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternParser("[^/]+", ".*/{customer}/");
      path.getParameterStore().get("something else");
   }

   @Test
   public void testParametersUsedMultipleRequireSingleConfiguration()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternParser("/{*}/{*}/");
      path.getParameterStore().get("*").matches("foo");

      Assert.assertTrue(path.matches(rewrite, context, "/foo/foo/"));
      Assert.assertFalse(path.matches(rewrite, context, "/foo/bar/"));
   }

   @Test
   public void testParsesWithParametersRespectsTrailingCharsWithWildcardParameter()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternParser(".*", "/{customer}/");

      ParameterStore<ParameterizedPatternParameter> parameters = path.getParameterStore();
      Assert.assertEquals(1, parameters.size());
      Assert.assertEquals("customer", parameters.get("customer").getName());

      Map<ParameterizedPatternParameter, String[]> results = path.parse(rewrite, context, "/lincoln/");
      Assert.assertEquals("lincoln", results.get(path.getParameterStore().get("customer"))[0]);
   }

   @Test
   public void testMatchesWithParametersAndTrailingSlash()
   {
      ParameterizedPatternParser path = new RegexParameterizedPatternBuilder("[^/]+", "/{customer}/orders/{id}/")
               .getParser();

      ParameterStore<ParameterizedPatternParameter> parameters = path.getParameterStore();
      Assert.assertEquals(2, parameters.size());
      Assert.assertEquals("customer", parameters.get("customer").getName());
      Assert.assertEquals("id", parameters.get("id").getName());

      Map<ParameterizedPatternParameter, String[]> results = path.parse(rewrite, context, "/lincoln/orders/24/");
      Assert.assertEquals("lincoln", results.get(path.getParameterStore().get("customer"))[0]);
      Assert.assertEquals("24", results.get(path.getParameterStore().get("id"))[0]);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testBuildNull()
   {
      ParameterizedPatternBuilder path = new RegexParameterizedPatternBuilder(null);
      Assert.assertEquals("", path.build(new LinkedHashMap<String, List<String>>()));
   }

   @Test
   public void testBuildEmpty()
   {
      ParameterizedPatternBuilder path = new RegexParameterizedPatternBuilder("");
      Assert.assertEquals("", path.build(new LinkedHashMap<String, List<String>>()));
   }

   @Test
   public void testBuildBarePath()
   {
      ParameterizedPatternBuilder path = new RegexParameterizedPatternBuilder("/");
      Assert.assertEquals("/", path.build(new LinkedHashMap<String, List<String>>()));
   }

   @Test
   public void testBuildWithMapParameters()
   {
      ParameterizedPatternBuilder path = new RegexParameterizedPatternParser("[^/]*", "/{customer}/orders/{id}")
               .getBuilder();
      Map<String, List<String>> map = new LinkedHashMap<String, List<String>>();
      Maps.addListValue(map, "customer", "lincoln");
      Maps.addListValue(map, "id", "24");
      Assert.assertEquals("/lincoln/orders/24", path.build(map));
   }

   @Test
   public void testBuildWithListParameters()
   {
      ParameterizedPatternBuilder path = new RegexParameterizedPatternBuilder("[^/]*", "/{customer}/orders/{id}");
      Assert.assertEquals("/lincoln/orders/24", path.build("lincoln", "24"));
   }

}
