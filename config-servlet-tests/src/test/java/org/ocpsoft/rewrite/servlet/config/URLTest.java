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
import org.ocpsoft.rewrite.param.DefaultParameterStore;
import org.ocpsoft.rewrite.param.ParameterConfiguration;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.RegexConstraint;
import org.ocpsoft.rewrite.servlet.impl.HttpInboundRewriteImpl;
import org.ocpsoft.rewrite.util.ParameterUtils;

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

      rewrite = new HttpInboundRewriteImpl(request, null, null);
   }

   @Test
   public void testMatchesWithParameters()
   {
      MockEvaluationContext context = new MockEvaluationContext();
      URL url = URL.matches("http://domain.com:8080/context/application/{seg}?foo=bar&baz=bazaar");
      ParameterUtils.initialize(context, url);
      Assert.assertTrue(url.evaluate(rewrite, context));
   }

   @Test
   public void testUrlMatchesWithParameters()
   {
      URL url = URL.matches("{prefix}/application/{seg}{suffix}");

      MockEvaluationContext context = new MockEvaluationContext();
      ParameterUtils.initialize(context, url);

      ParameterStore store = DefaultParameterStore.getInstance(context);
      ((ParameterConfiguration<?>) store .get("prefix")).constrainedBy(new RegexConstraint(".*"));
      ((ParameterConfiguration<?>) store.get("suffix")).constrainedBy(new RegexConstraint("\\?.*"));

      Assert.assertTrue(url.evaluate(rewrite, context));
   }

   @Test
   public void testCaptureMatches()
   {
      URL url = URL.captureIn("foo");

      MockEvaluationContext context = new MockEvaluationContext();
      ParameterUtils.initialize(context, url);

      Assert.assertTrue(url.evaluate(rewrite, context));
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
