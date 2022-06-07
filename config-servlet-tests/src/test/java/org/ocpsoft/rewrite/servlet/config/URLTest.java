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

import jakarta.servlet.http.HttpServletRequest;

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

import static org.assertj.core.api.Assertions.assertThat;

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
      assertThat(url.evaluate(rewrite, context)).isTrue();
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

      assertThat(url.evaluate(rewrite, context)).isTrue();
   }

   @Test
   public void testCaptureMatches()
   {
      URL url = URL.captureIn("foo");

      MockEvaluationContext context = new MockEvaluationContext();
      ParameterUtils.initialize(context, url);

      assertThat(url.evaluate(rewrite, context)).isTrue();
   }

   @Test
   public void testMatchesLiteral()
   {
      assertThat(URL.matches("http://domain.com:8080/context/application/path?foo=bar&baz=bazaar").evaluate(
              rewrite, new MockEvaluationContext())).isTrue();
   }

   @Test
   public void testCannotUseRegexWithoutParam()
   {
      assertThat(URL.matches(".*/context/application/pa.*").evaluate(rewrite,
              new MockEvaluationContext())).isFalse();
   }

   @Test
   public void testDoesNotMatchNonHttpRewrites()
   {
      assertThat(URL.matches("/blah").evaluate(new MockRewrite(), new MockEvaluationContext())).isFalse();
   }

   @Test(expected = IllegalArgumentException.class)
   public void testNullCausesException()
   {
      Path.matches(null);
   }
}
