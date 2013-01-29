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
package org.ocpsoft.rewrite.servlet.config;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.mock.MockEvaluationContext;
import org.ocpsoft.rewrite.mock.MockRewrite;
import org.ocpsoft.rewrite.servlet.impl.HttpInboundRewriteImpl;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class QueryEncodingTest
{
   private Rewrite rewrite;
   private HttpServletRequest request;

   @Before
   public void before()
   {
      request = Mockito.mock(HttpServletRequest.class);

      Mockito.when(request.getQueryString())
               .thenReturn("foo=bar&one=1&my+cat=al∂ve");

      Mockito.when(request.getRequestURI())
               .thenReturn("/context/application/path");

      Mockito.when(request.getContextPath())
               .thenReturn("/context");

      rewrite = new HttpInboundRewriteImpl(request, null, null);
   }

   @Test
   public void testQueryStringMatchesWithParameters()
   {
      Assert.assertTrue(Query.parameterExists("my cat").evaluate(rewrite, new MockEvaluationContext()));
   }

   @Test
   public void testQueryStringMatchesWithRegex()
   {
      // FIXME remove regex support
      Query query = Query.valueExists(".*∂ve.*");
      MockEvaluationContext context = new MockEvaluationContext();
      Assert.assertTrue(query.evaluate(rewrite, context));
   }

   @Test
   public void testQueryStringMatchesLiteral()
   {
      Assert.assertTrue(Path.matches("/application/path").evaluate(rewrite, new MockEvaluationContext()));
   }

   @Test
   public void testQueryStringMatchesPattern()
   {
      Assert.assertTrue(Query.matches(".*&one=1.*").evaluate(rewrite, new MockEvaluationContext()));
   }

   @Test
   public void testQueryStringBindsToEntireValue()
   {
      Query query = Query.matches(".*&one=1.*");
      Assert.assertTrue(query.evaluate(rewrite, new MockEvaluationContext()));

      MockEvaluationContext context = new MockEvaluationContext();
      Assert.assertTrue(query.evaluate(rewrite, context));
   }

   @Test
   public void testDoesNotMatchNonHttpRewrites()
   {
      Assert.assertFalse(Query.matches(".*").evaluate(new MockRewrite(), new MockEvaluationContext()));
   }

   @Test(expected = IllegalArgumentException.class)
   public void testNullCausesException()
   {
      Query.matches(null);
   }
}
