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

import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.mock.MockEvaluationContext;
import com.ocpsoft.rewrite.servlet.http.impl.HttpInboundRewriteImpl;

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
      request = EasyMock.createNiceMock(HttpServletRequest.class);
      EasyMock.expect(request.getServerPort())
               .andReturn(8080).anyTimes();

      EasyMock.replay(request);

      rewrite = new HttpInboundRewriteImpl(request, null);
   }

   @Test
   public void testPortMatches()
   {
      Assert.assertTrue(Port.is(8080).evaluate(rewrite, new MockEvaluationContext()));
   }

   @Test
   public void testMultiPortMatches()
   {
      Assert.assertTrue(Port.is(8080, 9090).evaluate(rewrite, new MockEvaluationContext()));
   }

   @Test
   public void testMultiPortDoesNotMatch()
   {
      Assert.assertFalse(Port.is(9080, 9090).evaluate(rewrite, new MockEvaluationContext()));
   }

   @Test(expected = IllegalArgumentException.class)
   public void testOutOfRangePortThrowsException()
   {
      Port.is(0).evaluate(rewrite, new MockEvaluationContext());
   }

   @Test(expected = IllegalArgumentException.class)
   public void testOutOfRangePortThrowsExceptionWithMultiPort()
   {
      Port.is(8080, 0).evaluate(rewrite, new MockEvaluationContext());
   }

   @Test(expected = IllegalArgumentException.class)
   public void testOutOfRangePortThrowsExceptionWithMultiPort2()
   {
      Port.is(0, 8080).evaluate(rewrite, new MockEvaluationContext());
   }

   @Test
   public void testDoesNotMatchNonHttpRewrites()
   {
      Assert.assertFalse(Port.is(9090).evaluate(rewrite, new MockEvaluationContext()));
   }
}
