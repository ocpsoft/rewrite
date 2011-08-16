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
package com.ocpsoft.rewrite.servlet.util;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.mock.MockEvaluationContext;
import com.ocpsoft.rewrite.servlet.config.QueryString;
import com.ocpsoft.rewrite.servlet.impl.HttpInboundRewriteImpl;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class QueryStringBuilderTest
{
   private Rewrite rewrite;
   private HttpServletRequest request;

   @Before
   public void before()
   {
      request = Mockito.mock(HttpServletRequest.class);

      Mockito.when(request.getRequestURI())
               .thenReturn("/context/application/path");

      Mockito.when(request.getQueryString())
               .thenReturn("foo=bar&bar=baz");

      Mockito.when(request.getContextPath())
               .thenReturn("/context");

      rewrite = new HttpInboundRewriteImpl(request, null);
   }

   @Test
   public void testQueryStringMatchesLiteral()
   {
      Assert.assertTrue(QueryString.matches("foo=bar&bar=baz").evaluate(rewrite, new MockEvaluationContext()));
   }

   @Test
   public void testQueryStringMatchesPattern()
   {
      Assert.assertTrue(QueryString.matches("foo=bar.*").evaluate(rewrite, new MockEvaluationContext()));
   }

   @Test
   public void testQueryStringParameterExists()
   {
      Assert.assertTrue(QueryString.parameterExists(".oo").evaluate(rewrite, new MockEvaluationContext()));
   }

   @Test
   public void testQueryStringParameterDoesNotExist()
   {
      Assert.assertFalse(QueryString.parameterExists("nothing").evaluate(rewrite, new MockEvaluationContext()));
   }

   @Test
   public void testQueryStringValueExists()
   {
      Assert.assertTrue(QueryString.valueExists(".ar").evaluate(rewrite, new MockEvaluationContext()));
   }

   @Test
   public void testQueryStringValueDoesNotExist()
   {
      Assert.assertFalse(QueryString.valueExists("nothing").evaluate(rewrite, new MockEvaluationContext()));
   }

   @Test
   public void testDoesNotMatchNonHttpRewrites()
   {
      Assert.assertTrue(QueryString.matches(".*bar=baz").evaluate(rewrite, new MockEvaluationContext()));
   }

   @Test(expected = IllegalArgumentException.class)
   public void testNullCausesException()
   {
      QueryString.matches(null);
   }
}
