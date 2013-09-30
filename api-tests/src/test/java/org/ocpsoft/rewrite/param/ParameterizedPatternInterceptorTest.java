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
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.ocpsoft.rewrite.mock.MockEvaluationContext;
import org.ocpsoft.rewrite.mock.MockRewrite;

/**
 * This test case was intended to begin mocking out an i18n API.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ParameterizedPatternInterceptorTest
{
   private MockEvaluationContext context;
   private MockRewrite rewrite;

   @Before
   public void before()
   {
      context = new MockEvaluationContext();
      rewrite = new MockRewrite();
   }

   @Ignore
   @Test
   public void testMatchesWithParametersRespectsTrailingCharsWithWildcardParameter()
   {
      RegexParameterizedPatternParser path = new RegexParameterizedPatternParser("[^/]+",
               "/{i18n:customer}/{customer}/orders/{id}");
      Assert.assertTrue(path.matches(rewrite, context, "/cust/lincoln/orders/3"));
      Assert.assertFalse(path.matches(rewrite, context, "/wrong/lincoln/orders/3"));
   }

   @Ignore
   @Test
   public void testBuildWithParameters()
   {
      RegexParameterizedPatternBuilder path = new RegexParameterizedPatternBuilder("[^/]+",
               "/{i18n:customer}/{customer}/orders/{id}");
      Map<String, Object> map = new LinkedHashMap<String, Object>();
      map.put("customer", "lincoln");
      map.put("id", "24");
      Assert.assertEquals("/cust/lincoln/orders/24", path.build(map));
   }

}
