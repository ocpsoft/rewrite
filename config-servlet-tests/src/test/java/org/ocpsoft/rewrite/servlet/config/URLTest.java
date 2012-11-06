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

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.ocpsoft.rewrite.bind.Evaluation;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.mock.MockBinding;
import org.ocpsoft.rewrite.mock.MockEvaluationContext;
import org.ocpsoft.rewrite.mock.MockRewrite;
import org.ocpsoft.rewrite.servlet.impl.HttpInboundRewriteImpl;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class URLTest
{
   private Rewrite rewrite;
   private HttpServletRequest request;

   @Before
   public void before()
   {
      request = Mockito.mock(HttpServletRequest.class);

      Mockito.when(request.getRequestURI())
      .thenReturn("/context/application/path");

      Mockito.when(request.getContextPath())
      .thenReturn("/context");

      Mockito.when(request.getQueryString())
      .thenReturn("foo=bar&baz=bazaar");

      Mockito.when(request.getScheme())
      .thenReturn("http");

      Mockito.when(request.getServerName())
      .thenReturn("domain.com");

      Mockito.when(request.getServerPort())
      .thenReturn(8080);

      rewrite = new HttpInboundRewriteImpl(request, null);
   }

   @Test
   public void testMatchesWithParameters()
   {
      Assert.assertTrue(URL.matches("http://domain.com:8080/context/application/{seg}?foo=bar&baz=bazaar").evaluate(
               rewrite, new MockEvaluationContext()));
   }

   @Test
   public void testAttemptsToBindParameters()
   {
      MockBinding mockBinding = new MockBinding();
      IURL url = URL.matches("{prefix}/application/{seg}{suffix}")
               .where("prefix").matches(".*")
               .where("suffix").matches("\\?.*")
               .where("seg", mockBinding);
      MockEvaluationContext context = new MockEvaluationContext();
      Assert.assertTrue(url.evaluate(rewrite, context));

      List<Operation> operations = context.getPreOperations();
      Assert.assertEquals(1, operations.size());
      for (Operation operation : operations) {
         operation.perform(rewrite, context);
      }

      Assert.assertTrue(mockBinding.isConverted());
      Assert.assertTrue(mockBinding.isValidated());
      Assert.assertTrue(mockBinding.isSubmitted());
      Assert.assertEquals("path", mockBinding.getBoundValue());
      Assert.assertEquals("path", Evaluation.property("seg").retrieve(rewrite, context));
   }

   @Test
   public void testCaptureInBindsParameters()
   {
      MockBinding mockBinding = new MockBinding();
      IURL url = URL.captureIn("foo")
               .where("foo", mockBinding);
      MockEvaluationContext context = new MockEvaluationContext();
      Assert.assertTrue(url.evaluate(rewrite, context));

      List<Operation> operations = context.getPreOperations();
      Assert.assertEquals(1, operations.size());
      for (Operation operation : operations) {
         operation.perform(rewrite, context);
      }

      Assert.assertTrue(mockBinding.isConverted());
      Assert.assertTrue(mockBinding.isValidated());
      Assert.assertTrue(mockBinding.isSubmitted());
      Assert.assertEquals("http://domain.com:8080/context/application/path?foo=bar&baz=bazaar",
               mockBinding.getBoundValue());
      Assert.assertEquals("http://domain.com:8080/context/application/path?foo=bar&baz=bazaar",
               Evaluation.property("foo").retrieve(rewrite, context));
   }

   @Test
   public void testMatchesLiteral()
   {
      Assert.assertTrue(URL.matches("http://domain.com:8080/context/application/path?foo=bar&baz=bazaar").evaluate(
               rewrite, new MockEvaluationContext()));
   }

   @Test
   public void testCannotUseRegexWithoutParam()
   {
      Assert.assertFalse(URL.matches(".*/context/application/pa.*").evaluate(rewrite,
               new MockEvaluationContext()));
   }

   @Test
   public void testDoesNotMatchNonHttpRewrites()
   {
      Assert.assertFalse(URL.matches("/blah").evaluate(new MockRewrite(), new MockEvaluationContext()));
   }

   @Test(expected = IllegalArgumentException.class)
   public void testNullCausesException()
   {
      Path.matches(null);
   }
}
