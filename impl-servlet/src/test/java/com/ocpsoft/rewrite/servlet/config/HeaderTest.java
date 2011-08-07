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

import java.util.Arrays;
import java.util.Collections;
import java.util.regex.PatternSyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.mock.MockEvaluationContext;
import com.ocpsoft.rewrite.mock.MockRewrite;
import com.ocpsoft.rewrite.servlet.http.impl.HttpInboundRewriteImpl;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class HeaderTest
{
   private Rewrite rewrite;
   private HttpServletRequest request;

   @Before
   public void before()
   {
      request = Mockito.mock(HttpServletRequest.class);
      Mockito.when(request.getHeaderNames())
               .thenReturn(Collections.enumeration(Arrays.asList("Accept-Charset", "Content-Length")));

      Mockito.when(request.getHeaders("Content-Length"))
               .thenReturn(Collections.enumeration(Arrays.asList("06091984")));

      Mockito.when(request.getHeaders("Accept-Charset"))
               .thenReturn(Collections.enumeration(Arrays.asList("ISO-9965", "UTF-8")));

      rewrite = new HttpInboundRewriteImpl(request, null);
   }

   @Test
   public void testHeaderExists()
   {
      Assert.assertTrue(Header.exists("Accept-.*").evaluate(rewrite, new MockEvaluationContext()));
   }

   @Test
   public void testHeaderExists2()
   {
      Assert.assertTrue(Header.exists("Content-Length").evaluate(rewrite, new MockEvaluationContext()));
   }

   @Test
   public void testHeaderExistsFalse()
   {
      Assert.assertFalse(Header.exists("Host").evaluate(rewrite, new MockEvaluationContext()));
   }

   @Test
   public void testHeaderContains()
   {
      Assert.assertTrue(Header.valueExists("UTF-.*").evaluate(rewrite, new MockEvaluationContext()));
   }

   @Test
   public void testHeaderMatches()
   {
      Assert.assertTrue(Header.matches("Accept-Charset", "(ISO|UTF)-\\d+").evaluate(rewrite,
               new MockEvaluationContext()));
   }

   @Test(expected = PatternSyntaxException.class)
   public void testBadRegexThrowsException()
   {
      Header.matches("*Accept-Charset", "blah");
   }

   @Test(expected = IllegalArgumentException.class)
   public void testNullNameInput()
   {
      Header.exists(null);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testNullValueExistsInput()
   {
      Header.valueExists(null);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testNullInputs()
   {
      Header.matches(null, null);
   }

   @Test
   public void testDoesNotMatchNonHttpRewrites()
   {
      Assert.assertFalse(Header.exists("Accept-Charset").evaluate(new MockRewrite(), new MockEvaluationContext()));
   }
}
