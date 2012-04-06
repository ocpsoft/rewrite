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
import org.ocpsoft.rewrite.bind.ParameterizedPattern;
import org.ocpsoft.rewrite.bind.RegexCapture;
import org.ocpsoft.rewrite.bind.parse.CaptureType;
import org.ocpsoft.rewrite.bind.util.Maps;
import org.ocpsoft.rewrite.mock.MockEvaluationContext;
import org.ocpsoft.rewrite.mock.MockRewrite;

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

      ParameterizedPattern parameterizedPattern = new ParameterizedPattern("{prefix}/application/{seg}{suffix}");
      parameterizedPattern.getParameter("prefix").matches(".*");
      parameterizedPattern.getParameter("seg").matches("[^/]+");
      parameterizedPattern.getParameter("suffix").matches("\\?.*");

      Assert.assertTrue(parameterizedPattern.matches(rewrite, new MockEvaluationContext(), url));

      int index = 0;
      String[] expected = new String[] { "http://domain.com:8080/context", "pathy", "?foo=bar&baz=bazaar" };
      Map<RegexCapture, String[]> parsed = parameterizedPattern.parse(rewrite, new MockEvaluationContext(), url);
      for (Entry<RegexCapture, String[]> entry : parsed.entrySet()) {
         String[] value = entry.getValue();
         for (int i = 0; i < value.length; i++) {
            Assert.assertEquals(expected[index++], value[0]);
         }
      }
   }

   @Test
   public void testCannotUseRegularExpressionsWithoutParameter()
   {
      ParameterizedPattern path = new ParameterizedPattern(CaptureType.BRACE, ".*");

      Assert.assertEquals(0, path.getParameters().size());
      Assert.assertFalse(path.matches(rewrite, context, "/omg/doesnt/matter"));
   }

   @Test
   public void testMatchesEmptyPath()
   {
      ParameterizedPattern path = new ParameterizedPattern(CaptureType.BRACE, "");

      Assert.assertEquals(0, path.getParameters().size());
      Assert.assertTrue(path.matches(rewrite, context, ""));
      Map<RegexCapture, String[]> results = path.parse(rewrite, context, "");
      Assert.assertNotNull(results);
   }

   @Test
   public void testMatchesBarePath()
   {
      ParameterizedPattern path = new ParameterizedPattern("/");

      Assert.assertEquals(0, path.getParameters().size());
      Assert.assertTrue(path.matches(rewrite, context, "/"));

      Map<RegexCapture, String[]> results = path.parse(rewrite, context, "/");
      Assert.assertNotNull(results);
   }

   @Test
   public void testMatchesWithParameters()
   {
      ParameterizedPattern path = new ParameterizedPattern("[^/]+", "/{customer}/orders/{id}");

      Map<String, RegexCapture> parameters = path.getParameters();
      Assert.assertEquals(2, parameters.size());
      Assert.assertEquals("customer", parameters.get("customer").getName());
      Assert.assertEquals("id", parameters.get("id").getName());

      Map<RegexCapture, String[]> results = path.parse(rewrite, context, "/lincoln/orders/24");
      Assert.assertEquals("lincoln", results.get(path.getParameter("customer"))[0]);
      Assert.assertEquals("24", results.get(path.getParameter("id"))[0]);
   }

   @Test
   public void testMatchesWithParametersRespectsTrailingCharsWithWildcardParameter()
   {
      ParameterizedPattern path = new ParameterizedPattern(".*", "/{customer}/");
      Assert.assertTrue(path.matches(rewrite, context, "/lincoln/"));
      Assert.assertFalse(path.matches(rewrite, context, "/lincoln/foo"));
   }

   @Test
   public void testRegularExpressionsAreDisabled()
   {
      ParameterizedPattern path = new ParameterizedPattern("[^/]+", ".*/{customer}/");
      Assert.assertTrue(path.matches(rewrite, context, ".*/lincoln/"));
      Assert.assertFalse(path.matches(rewrite, context, "foobar/lincoln/"));
   }

   @Test
   public void testParsesWithParametersRespectsTrailingCharsWithWildcardParameter()
   {
      ParameterizedPattern path = new ParameterizedPattern(".*", "/{customer}/");

      Map<String, RegexCapture> parameters = path.getParameters();
      Assert.assertEquals(1, parameters.size());
      Assert.assertEquals("customer", parameters.get("customer").getName());

      Map<RegexCapture, String[]> results = path.parse(rewrite, context, "/lincoln/");
      Assert.assertEquals("lincoln", results.get(path.getParameter("customer"))[0]);
   }

   @Test
   public void testMatchesWithParametersAndTrailingSlash()
   {
      ParameterizedPattern path = new ParameterizedPattern("[^/]+", "/{customer}/orders/{id}/");

      Map<String, RegexCapture> parameters = path.getParameters();
      Assert.assertEquals(2, parameters.size());
      Assert.assertEquals("customer", parameters.get("customer").getName());
      Assert.assertEquals("id", parameters.get("id").getName());

      Map<RegexCapture, String[]> results = path.parse(rewrite, context, "/lincoln/orders/24/");
      Assert.assertEquals("lincoln", results.get(path.getParameter("customer"))[0]);
      Assert.assertEquals("24", results.get(path.getParameter("id"))[0]);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testBuildNull()
   {
      ParameterizedPattern path = new ParameterizedPattern(null);
      Assert.assertEquals("", path.buildUnsafe(new LinkedHashMap<String, List<Object>>()));
   }

   @Test
   public void testBuildEmpty()
   {
      ParameterizedPattern path = new ParameterizedPattern("");
      Assert.assertEquals("", path.buildUnsafe(new LinkedHashMap<String, List<Object>>()));
   }

   @Test
   public void testBuildBarePath()
   {
      ParameterizedPattern path = new ParameterizedPattern("/");
      Assert.assertEquals("/", path.buildUnsafe(new LinkedHashMap<String, List<Object>>()));
   }

   @Test
   public void testBuildWithParameters()
   {
      ParameterizedPattern path = new ParameterizedPattern("[^/]*", "/{customer}/orders/{id}");
      Map<String, List<Object>> map = new LinkedHashMap<String, List<Object>>();
      Maps.addListValue(map, "customer", "lincoln");
      Maps.addListValue(map, "id", "24");
      Assert.assertEquals("/lincoln/orders/24", path.buildUnsafe(map));
   }

}
