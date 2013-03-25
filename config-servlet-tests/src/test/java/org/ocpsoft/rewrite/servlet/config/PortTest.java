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
import org.ocpsoft.rewrite.servlet.impl.HttpInboundRewriteImpl;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class PortTest
{
   private Rewrite rewrite;
   private HttpServletRequest request;

   @Before
   public void before()
   {
      request = Mockito.mock(HttpServletRequest.class);
      Mockito.when(request.getServerPort())
               .thenReturn(8080);
      Mockito.when(request.getRequestURI())
               .thenReturn("/path");

      rewrite = new HttpInboundRewriteImpl(request, null, null);
   }

   @Test
   public void testPortMatches()
   {
      Assert.assertTrue(ServerPort.is(8080).evaluate(rewrite, new MockEvaluationContext()));
   }

   @Test
   public void testMultiPortMatches()
   {
      Assert.assertTrue(ServerPort.is(8080, 9090).evaluate(rewrite, new MockEvaluationContext()));
   }

   @Test
   public void testMultiPortDoesNotMatch()
   {
      Assert.assertFalse(ServerPort.is(9080, 9090).evaluate(rewrite, new MockEvaluationContext()));
   }

   @Test(expected = IllegalArgumentException.class)
   public void testOutOfRangePortThrowsException()
   {
      ServerPort.is(0).evaluate(rewrite, new MockEvaluationContext());
   }

   @Test(expected = IllegalArgumentException.class)
   public void testOutOfRangePortThrowsExceptionWithMultiPort()
   {
      ServerPort.is(8080, 0).evaluate(rewrite, new MockEvaluationContext());
   }

   @Test(expected = IllegalArgumentException.class)
   public void testOutOfRangePortThrowsExceptionWithMultiPort2()
   {
      ServerPort.is(0, 8080).evaluate(rewrite, new MockEvaluationContext());
   }

   @Test
   public void testDoesNotMatchNonHttpRewrites()
   {
      Assert.assertFalse(ServerPort.is(9090).evaluate(rewrite, new MockEvaluationContext()));
   }
}
