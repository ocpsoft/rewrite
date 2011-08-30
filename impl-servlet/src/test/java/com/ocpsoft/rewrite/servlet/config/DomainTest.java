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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.ocpsoft.rewrite.bind.Evaluation;
import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.mock.MockEvaluationContext;
import com.ocpsoft.rewrite.servlet.config.bind.Request;
import com.ocpsoft.rewrite.servlet.impl.HttpInboundRewriteImpl;
import com.ocpsoft.rewrite.servlet.impl.HttpOutboundRewriteImpl;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class DomainTest
{
   private Rewrite inbound;
   private Rewrite outbound;
   private HttpServletRequest request;

   @Before
   public void before()
   {
      request = Mockito.mock(HttpServletRequest.class);
      Mockito.when(request.getServerName())
               .thenReturn("example.com");

      inbound = new HttpInboundRewriteImpl(request, null);
      outbound = new HttpOutboundRewriteImpl(request, null, "http://example.com:8080/path?query=value");
   }

   @Test
   public void testDomainMatchesInbound()
   {
      Assert.assertTrue(Domain.matches("example.com").evaluate(inbound, new MockEvaluationContext()));
   }

   @Test
   public void testDomainMatchesBindsInbound()
   {
      MockEvaluationContext context = new MockEvaluationContext();
      Assert.assertTrue(Domain.matches("{domain}.com").where("domain").bindsTo(Request.attribute("domain"))
               .evaluate(inbound, context));

      // Invoke the binding.
      context.getPreOperations().get(0).perform(inbound, context);
      context.getPreOperations().get(1).perform(inbound, context);

      Assert.assertEquals("example", ((String[]) Evaluation.property("domain").retrieve(inbound, context))[0]);
   }

   @Test
   public void testDomainNotMatchesInbound()
   {
      Assert.assertFalse(Domain.matches("other.com").evaluate(inbound, new MockEvaluationContext()));
   }

   @Test
   public void testDomainMatchesOutbound()
   {
      Assert.assertTrue(Domain.matches("example.com").evaluate(outbound, new MockEvaluationContext()));
   }

   @Test
   public void testDomainNotMatchesOutbound()
   {
      Assert.assertFalse(Domain.matches("other.com").evaluate(outbound, new MockEvaluationContext()));
   }
}
