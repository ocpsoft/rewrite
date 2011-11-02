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
package com.ocpsoft.rewrite.servlet.config;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.ocpsoft.rewrite.config.Operation;
import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.mock.MockBinding;
import com.ocpsoft.rewrite.mock.MockEvaluationContext;
import com.ocpsoft.rewrite.mock.MockRewrite;
import com.ocpsoft.rewrite.servlet.impl.HttpInboundRewriteImpl;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class QueryStringEncodingTest
{
   private Rewrite rewrite;
   private HttpServletRequest request;

   @Before
   public void before()
   {
      request = Mockito.mock(HttpServletRequest.class);

      Mockito.when(request.getQueryString())
               .thenReturn("foo=bar&one=1&my%20cat=al∂ve");

      Mockito.when(request.getRequestURI())
               .thenReturn("/context/application/path");

      Mockito.when(request.getContextPath())
               .thenReturn("/context");

      rewrite = new HttpInboundRewriteImpl(request, null);
   }

   @Test
   public void testQueryStringMatchesWithParameters()
   {
      Assert.assertTrue(QueryString.parameterExists("my cat").evaluate(rewrite, new MockEvaluationContext()));
   }

   @Test
   public void testQueryStringAttemptsToBindParameters()
   {
      MockBinding mockBinding = new MockBinding();
      QueryString query = QueryString.valueExists(".*∂ve.*")
               .bindsTo(mockBinding);
      MockEvaluationContext context = new MockEvaluationContext();
      Assert.assertTrue(query.evaluate(rewrite, context));

      List<Operation> operations = context.getPreOperations();
      Assert.assertEquals(1, operations.size());
      for (Operation operation : operations) {
         operation.perform(rewrite, context);
      }

      Assert.assertTrue(mockBinding.isConverted());
      Assert.assertTrue(mockBinding.isValidated());
      Assert.assertTrue(mockBinding.isSubmitted());
      Assert.assertEquals("al∂ve", mockBinding.getBoundValue());
   }

   @Test
   public void testQueryStringMatchesLiteral()
   {
      Assert.assertTrue(Path.matches("/application/path").evaluate(rewrite, new MockEvaluationContext()));
   }

   @Test
   public void testQueryStringMatchesPattern()
   {
      Assert.assertTrue(QueryString.matches(".*&one=1.*").evaluate(rewrite, new MockEvaluationContext()));
   }

   @Test
   public void testQueryStringBindsToEntireValue()
   {
      MockBinding mockBinding = new MockBinding();
      QueryString query = QueryString.matches(".*&one=1.*").bindsTo(mockBinding);
      Assert.assertTrue(query.evaluate(rewrite, new MockEvaluationContext()));

      MockEvaluationContext context = new MockEvaluationContext();
      Assert.assertTrue(query.evaluate(rewrite, context));

      List<Operation> operations = context.getPreOperations();
      Assert.assertEquals(1, operations.size());
      for (Operation operation : operations) {
         operation.perform(rewrite, context);
      }

      Assert.assertTrue(mockBinding.isConverted());
      Assert.assertTrue(mockBinding.isValidated());
      Assert.assertTrue(mockBinding.isSubmitted());
      Assert.assertEquals("foo=bar&one=1&my cat=al∂ve", mockBinding.getBoundValue());
   }

   @Test
   public void testDoesNotMatchNonHttpRewrites()
   {
      Assert.assertFalse(QueryString.matches(".*").evaluate(new MockRewrite(), new MockEvaluationContext()));
   }

   @Test(expected = IllegalArgumentException.class)
   public void testNullCausesException()
   {
      QueryString.matches(null);
   }
}
