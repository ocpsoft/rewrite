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

import static org.junit.Assert.assertTrue;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.mock.MockEvaluationContext;
import org.ocpsoft.rewrite.mock.MockRewrite;
import org.ocpsoft.rewrite.param.DefaultParameterStore;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.servlet.impl.HttpInboundRewriteImpl;
import org.ocpsoft.rewrite.util.ParameterUtils;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class PathTest
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

      rewrite = new HttpInboundRewriteImpl(request, null, null);
   }

   @Test
   public void testPathImplementsParameterized() throws Exception
   {
      Assert.assertTrue(Path.matches("") instanceof Parameterized);
   }

   @Test
   public void testMatchesWithParameters()
   {
      Path path = Path.matches("/application/{seg}");
      MockEvaluationContext context = new MockEvaluationContext();
      ParameterUtils.initialize(context, path);
      Assert.assertTrue(path.evaluate(rewrite, context));
   }

   @Test
   public void testAttemptsMatchesParameters()
   {
      Path path = Path.matches("/application/{seg}");

      MockEvaluationContext context = new MockEvaluationContext();
      ParameterUtils.initialize(context, path);

      Assert.assertTrue(path.evaluate(rewrite, context));

   }

   @Test
   public void testMatchesLiteral()
   {
      Assert.assertTrue(Path.matches("/application/path").evaluate(rewrite, new MockEvaluationContext()));
   }

   @Test
   public void testMatchesPattern()
   {
      Path path = Path.matches("/application/{param}");
      MockEvaluationContext context = new MockEvaluationContext();
      ParameterUtils.initialize(context, path);
      Assert.assertTrue(path.evaluate(rewrite, context));
   }

   @Test
   public void testDoesNotMatchNonHttpRewrites()
   {
      Assert.assertFalse(Path.matches("/blah").evaluate(new MockRewrite(), new MockEvaluationContext()));
   }

   @Test(expected = IllegalArgumentException.class)
   public void testNullCausesException()
   {
      Path.matches(null);
   }

   @Test
   public void testMultipleParameterStoreInvocationsReturnSameParam()
   {
      Path path = Path.matches("/something/#{param}");
      ParameterStore store = new DefaultParameterStore();
      ParameterUtils.initialize(store, path);

      Parameter<?> p1 = store.get("param");
      Parameter<?> p2 = store.get("param");
      assertTrue(p1 == p2);
   }

}
