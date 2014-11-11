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
import org.ocpsoft.rewrite.param.DefaultParameter;
import org.ocpsoft.rewrite.param.DefaultParameterStore;
import org.ocpsoft.rewrite.param.DefaultParameterValueStore;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.ParameterValueStore;
import org.ocpsoft.rewrite.param.RegexConstraint;
import org.ocpsoft.rewrite.servlet.impl.HttpInboundRewriteImpl;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class QueryEncodingTest
{
   private Rewrite rewrite;
   private HttpServletRequest request;
   private MockEvaluationContext context;
   private ParameterStore store;

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
      context = new MockEvaluationContext();
      context.put(ParameterValueStore.class, new DefaultParameterValueStore());
      store = new DefaultParameterStore();
      context.put(ParameterStore.class, store);
   }

   @Test
   public void testQueryStringMatchesWithParameters()
   {
      store.get("my cat", new DefaultParameter("my cat"));
      Assert.assertTrue(Query.parameterExists("my cat").evaluate(rewrite, context));
   }

   @Test
   public void testQueryStringMatchesWithRegex()
   {
      Query query = Query.valueExists("{*}∂ve{*}");
      query.setParameterStore(store);
      Assert.assertTrue(query.evaluate(rewrite, context));
   }

   @Test
   public void testQueryStringMatchesLiteral()
   {
      Assert.assertTrue(Path.matches("/application/path").evaluate(rewrite, context));
   }

   @Test
   public void testQueryStringMatchesPattern()
   {
      store.get("x", new DefaultParameter("x").constrainedBy(new RegexConstraint(".*&one=1.*")));
      Query query = Query.matches("{x}");
      query.setParameterStore(store);
      Assert.assertTrue(query.evaluate(rewrite, context));
   }

   @Test
   public void testQueryStringBindsToEntireValue()
   {
      store.get("x", new DefaultParameter("x").constrainedBy(new RegexConstraint(".*&one=1.*")));
      Query query = Query.matches("{x}");
      query.setParameterStore(store);
      Assert.assertTrue(query.evaluate(rewrite, context));

      Assert.assertTrue(query.evaluate(rewrite, context));
   }

   @Test
   public void testDoesNotMatchNonHttpRewrites()
   {
      Assert.assertFalse(Query.matches(".*").evaluate(new MockRewrite(), context));
   }

   @Test(expected = IllegalArgumentException.class)
   public void testNullCausesException()
   {
      Query.matches(null);
   }
}
