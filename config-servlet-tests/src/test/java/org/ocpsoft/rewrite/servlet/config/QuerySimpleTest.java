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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.mock.MockEvaluationContext;
import org.ocpsoft.rewrite.mock.MockRewrite;
import org.ocpsoft.rewrite.param.DefaultParameter;
import org.ocpsoft.rewrite.param.DefaultParameterStore;
import org.ocpsoft.rewrite.param.DefaultParameterValueStore;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.ParameterValueStore;
import org.ocpsoft.rewrite.servlet.impl.HttpInboundRewriteImpl;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class QuerySimpleTest
{
   private Rewrite rewrite;
   private HttpServletRequest request;
   private MockEvaluationContext context;
   private ParameterStore store;

   @Before
   public void before()
   {
      request = Mockito.mock(HttpServletRequest.class);

      Mockito.when(request.getRequestURI())
               .thenReturn("/context/application/path");

      Mockito.when(request.getQueryString())
               .thenReturn("foo=bar&bar=baz&ee");

      Mockito.when(request.getContextPath())
               .thenReturn("/context");

      rewrite = new HttpInboundRewriteImpl(request, null, null);
      context = new MockEvaluationContext();
      context.put(ParameterValueStore.class, new DefaultParameterValueStore());
      store = new DefaultParameterStore();
      context.put(ParameterStore.class, store);
   }

   @Test
   public void testQueryStringMatchesLiteral()
   {
      assertThat(Query.matches("foo=bar&bar=baz&ee").evaluate(rewrite, context)).isTrue();
   }

   @Test
   public void testQueryStringMatchesPattern()
   {
      store.get("t", new DefaultParameter("t"));
      Query query = Query.matches("foo=bar{t}");
      query.setParameterStore(store);
      assertThat(query.evaluate(rewrite, context)).isTrue();
   }

   @Test
   public void testQueryStringParameterExists()
   {
      Query query = Query.parameterExists("foo");
      query.setParameterStore(store);
      store.get("foo", new DefaultParameter("foo"));
      assertThat(query.evaluate(rewrite, context)).isTrue();
   }

   @Test
   public void testQueryStringUnvaluedParameterExists()
   {
      Query query = Query.parameterExists("ee");
      query.setParameterStore(store);
      store.get("ee", new DefaultParameter("ee"));
      assertThat(query.evaluate(rewrite, context)).isTrue();
   }

   @Test
   public void testQueryStringParameterDoesNotExist()
   {
      assertThat(Query.parameterExists("nothing").evaluate(rewrite, context)).isFalse();
   }

   @Test
   public void testQueryStringValueExists()
   {
      Query query = Query.valueExists("{b}ar");
      query.setParameterStore(store);
      store.get("b", new DefaultParameter("b"));
      assertThat(query.evaluate(rewrite, context)).isTrue();
   }

   @Test
   public void testQueryStringValueDoesNotExist()
   {
      assertThat(Query.valueExists("nothing").evaluate(rewrite, context)).isFalse();
   }

   @Test
   public void testDoesNotMatchNonHttpRewrites()
   {
      assertThat(Query.matches(".*").evaluate(new MockRewrite(), context)).isFalse();
   }

   @Test(expected = IllegalArgumentException.class)
   public void testNullCausesException()
   {
      Query.matches(null);
   }
}
