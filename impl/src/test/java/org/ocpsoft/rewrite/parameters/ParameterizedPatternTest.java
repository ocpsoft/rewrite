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
package org.ocpsoft.rewrite.parameters;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.ocpsoft.rewrite.bind.ParameterizedPatternImpl;
import org.ocpsoft.rewrite.bind.parse.CaptureType;
import org.ocpsoft.rewrite.mock.MockEvaluationContext;
import org.ocpsoft.rewrite.mock.MockRewrite;
import org.ocpsoft.rewrite.param.ParameterizedPattern;
import org.ocpsoft.rewrite.param.PatternParameter;
import org.ocpsoft.rewrite.util.Maps;

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

      ParameterizedPattern parameterizedPattern = new ParameterizedPatternImpl("{prefix}/application/{seg}{suffix}");
      parameterizedPattern.getParameter("prefix").matches(".*");
      parameterizedPattern.getParameter("seg").matches("[^/]+");
      parameterizedPattern.getParameter("suffix").matches("\\?.*");

      Assert.assertTrue(parameterizedPattern.matches(rewrite, new MockEvaluationContext(), url));

      int index = 0;
      String[] expected = new String[] { "http://domain.com:8080/context", "pathy", "?foo=bar&baz=bazaar" };
      Map<PatternParameter, String[]> parsed = parameterizedPattern.parse(rewrite, new MockEvaluationContext(), url);
      for (Entry<PatternParameter, String[]> entry : parsed.entrySet()) {
         String[] value = entry.getValue();
         for (int i = 0; i < value.length; i++) {
            Assert.assertEquals(expected[index++], value[0]);
         }
      }
   }

   @Test
   public void testCannotUseRegularExpressionsWithoutParameter()
   {
      ParameterizedPattern path = new ParameterizedPatternImpl(CaptureType.BRACE, ".*");

      Assert.assertEquals(0, path.getParameterMap().size());
      Assert.assertFalse(path.matches(rewrite, context, "/omg/doesnt/matter"));
   }

   @Test
   public void testMatchesEmptyPath()
   {
      ParameterizedPattern path = new ParameterizedPatternImpl(CaptureType.BRACE, "");

      Assert.assertEquals(0, path.getParameterMap().size());
      Assert.assertTrue(path.matches(rewrite, context, ""));
      Map<PatternParameter, String[]> results = path.parse(rewrite, context, "");
      Assert.assertNotNull(results);
   }

   @Test
   public void testMatchesEmptyPathNoTransforms()
   {
      ParameterizedPattern path = new ParameterizedPatternImpl(CaptureType.BRACE, "");

      Assert.assertEquals(0, path.getParameterMap().size());
      Assert.assertTrue(path.matches(rewrite, context, ""));
      Map<PatternParameter, String[]> results = path.parse("");
      Assert.assertNotNull(results);
   }

   @Test
   public void testMatchesBarePath()
   {
      ParameterizedPattern path = new ParameterizedPatternImpl("/");

      Assert.assertEquals(0, path.getParameterMap().size());
      Assert.assertTrue(path.matches(rewrite, context, "/"));

      Map<PatternParameter, String[]> results = path.parse(rewrite, context, "/");
      Assert.assertNotNull(results);
   }

   @Test
   public void testMatchesWithParameters()
   {
      ParameterizedPattern path = new ParameterizedPatternImpl("[^/]+", "/{customer}/orders/{id}");

      Map<String, PatternParameter> parameters = path.getParameterMap();
      Assert.assertEquals(2, parameters.size());
      Assert.assertEquals("customer", parameters.get("customer").getName());
      Assert.assertEquals("id", parameters.get("id").getName());

      Map<PatternParameter, String[]> results = path.parse(rewrite, context, "/lincoln/orders/24");
      Assert.assertEquals("lincoln", results.get(path.getParameter("customer"))[0]);
      Assert.assertEquals("24", results.get(path.getParameter("id"))[0]);
   }

   @Test
   public void testMatchesWithParametersNoTransforms()
   {
      ParameterizedPattern path = new ParameterizedPatternImpl("[^/]+", "/{customer}/orders/{id}");

      Map<String, PatternParameter> parameters = path.getParameterMap();
      Assert.assertEquals(2, parameters.size());
      Assert.assertEquals("customer", parameters.get("customer").getName());
      Assert.assertEquals("id", parameters.get("id").getName());

      Map<PatternParameter, String[]> results = path.parse("/lincoln/orders/24");
      Assert.assertEquals("lincoln", results.get(path.getParameter("customer"))[0]);
      Assert.assertEquals("24", results.get(path.getParameter("id"))[0]);
   }

   @Test
   public void testMatchesWithParametersRespectsTrailingCharsWithWildcardParameter()
   {
      ParameterizedPattern path = new ParameterizedPatternImpl(".*", "/{customer}/");
      Assert.assertTrue(path.matches(rewrite, context, "/lincoln/"));
      Assert.assertFalse(path.matches(rewrite, context, "/lincoln/foo"));
   }

   @Test
   public void testRegularExpressionsAreDisabled()
   {
      ParameterizedPattern path = new ParameterizedPatternImpl("[^/]+", ".*/{customer}/");
      Assert.assertTrue(path.matches(rewrite, context, ".*/lincoln/"));
      Assert.assertFalse(path.matches(rewrite, context, "foobar/lincoln/"));
   }

   @Test(expected=IllegalArgumentException.class)
   public void testAccessNonExistentParameterThrowsException()
   {
      ParameterizedPattern path = new ParameterizedPatternImpl("[^/]+", ".*/{customer}/");
      path.where("foo");
   }

   @Test
   public void testParametersUsedMultipleRequireSingleConfiguration()
   {
      ParameterizedPattern path = new ParameterizedPatternImpl("/{*}/{*}/").where("*").matches("foo");
      Assert.assertTrue(path.matches(rewrite, context, "/foo/foo/"));
      Assert.assertFalse(path.matches(rewrite, context, "/foo/bar/"));
   }

   @Test
   public void testParsesWithParametersRespectsTrailingCharsWithWildcardParameter()
   {
      ParameterizedPattern path = new ParameterizedPatternImpl(".*", "/{customer}/");

      Map<String, PatternParameter> parameters = path.getParameterMap();
      Assert.assertEquals(1, parameters.size());
      Assert.assertEquals("customer", parameters.get("customer").getName());

      Map<PatternParameter, String[]> results = path.parse(rewrite, context, "/lincoln/");
      Assert.assertEquals("lincoln", results.get(path.getParameter("customer"))[0]);
   }

   @Test
   public void testMatchesWithParametersAndTrailingSlash()
   {
      ParameterizedPattern path = new ParameterizedPatternImpl("[^/]+", "/{customer}/orders/{id}/");

      Map<String, PatternParameter> parameters = path.getParameterMap();
      Assert.assertEquals(2, parameters.size());
      Assert.assertEquals("customer", parameters.get("customer").getName());
      Assert.assertEquals("id", parameters.get("id").getName());

      Map<PatternParameter, String[]> results = path.parse(rewrite, context, "/lincoln/orders/24/");
      Assert.assertEquals("lincoln", results.get(path.getParameter("customer"))[0]);
      Assert.assertEquals("24", results.get(path.getParameter("id"))[0]);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testBuildNull()
   {
      ParameterizedPattern path = new ParameterizedPatternImpl(null);
      Assert.assertEquals("", path.buildUnsafe(new LinkedHashMap<String, List<Object>>()));
   }

   @Test
   public void testBuildEmpty()
   {
      ParameterizedPattern path = new ParameterizedPatternImpl("");
      Assert.assertEquals("", path.buildUnsafe(new LinkedHashMap<String, List<Object>>()));
   }

   @Test
   public void testBuildBarePath()
   {
      ParameterizedPattern path = new ParameterizedPatternImpl("/");
      Assert.assertEquals("/", path.buildUnsafe(new LinkedHashMap<String, List<Object>>()));
   }

   @Test
   public void testBuildWithMapParameters()
   {
      ParameterizedPattern path = new ParameterizedPatternImpl("[^/]*", "/{customer}/orders/{id}");
      Map<String, List<Object>> map = new LinkedHashMap<String, List<Object>>();
      Maps.addListValue(map, "customer", "lincoln");
      Maps.addListValue(map, "id", "24");
      Assert.assertEquals("/lincoln/orders/24", path.buildUnsafe(map));
   }

   @Test
   public void testBuildWithListParameters()
   {
      ParameterizedPattern path = new ParameterizedPatternImpl("[^/]*", "/{customer}/orders/{id}");
      Assert.assertEquals("/lincoln/orders/24", path.buildUnsafe("lincoln", "24"));
   }

}
