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

import java.util.Arrays;
import java.util.Collections;

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
public class RequestParameterTest
{
   private Rewrite rewrite;
   private HttpServletRequest request;

   @Before
   public void before()
   {
      request = Mockito.mock(HttpServletRequest.class);
      Mockito.when(request.getParameterNames())
      .thenReturn(Collections.enumeration(Arrays.asList("foo", "baz")));

      Mockito.when(request.getParameterValues("foo"))
      .thenReturn(new String[] { "bar" });

      Mockito.when(request.getParameterValues("baz"))
      .thenReturn(new String[] { "cab" });

      Mockito.when(request.getParameter("foo"))
      .thenReturn("bar");

      Mockito.when(request.getParameter("baz"))
      .thenReturn("cab");

      rewrite = new HttpInboundRewriteImpl(request, null);
   }

   @Test
   public void testRequestParameterExists()
   {
      Assert.assertTrue(RequestParameter.exists("foo").evaluate(rewrite, new MockEvaluationContext()));
   }

   @Test
   public void testRequestParameterExists2()
   {
      Assert.assertTrue(RequestParameter.exists("baz").evaluate(rewrite, new MockEvaluationContext()));
   }

   @Test
   public void testRequestParameterExistsFalse()
   {
      Assert.assertFalse(RequestParameter.exists("nope").evaluate(rewrite, new MockEvaluationContext()));
   }

   @Test
   public void testRequestParameterContains()
   {
      Assert.assertTrue(RequestParameter.valueExists("bar").evaluate(rewrite, new MockEvaluationContext()));
   }

   @Test
   public void testRequestParameterMatches()
   {
      Assert.assertTrue(RequestParameter.matches("foo", "{value}").where("value").matches("(bar|baz)")
               .evaluate(rewrite, new MockEvaluationContext()));
   }

   @Test
   public void testBadRegexThrowsException()
   {
      Assert.assertFalse(RequestParameter.matches(".*", "bar").evaluate(rewrite, new MockEvaluationContext()));
   }

   @Test(expected = IllegalArgumentException.class)
   public void testNullNameInput()
   {
      RequestParameter.exists(null);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testNullValueExistsInput()
   {
      RequestParameter.valueExists(null);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testNullInputs()
   {
      RequestParameter.matches(null, null);
   }

   @Test
   public void testDoesNotMatchNonHttpRewrites()
   {
      Assert.assertFalse(RequestParameter.exists("foo").evaluate(new MockRewrite(), new MockEvaluationContext()));
   }
}
