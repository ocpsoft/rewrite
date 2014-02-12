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
package org.ocpsoft.rewrite.bind;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.mock.MockEvaluationContext;
import org.ocpsoft.rewrite.servlet.impl.HttpInboundRewriteImpl;
import org.ocpsoft.rewrite.servlet.impl.HttpOutboundRewriteImpl;
import org.ocpsoft.urlbuilder.AddressBuilder;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class EvaluationBindingTest
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

      inbound = new HttpInboundRewriteImpl(request, null, null);
      outbound = new HttpOutboundRewriteImpl(request, null, null,
               AddressBuilder.create("http://example.com:8080/path?query=value"));
   }

   @Test(expected = IllegalArgumentException.class)
   public void testCannotAccessNonexistentEvaluationContextPropertyInbound() throws Exception
   {
      MockEvaluationContext context = new MockEvaluationContext();
      Evaluation.property("property").retrieve(inbound, context);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testCannotAccessNonexistentEvaluationContextPropertyOutbound() throws Exception
   {
      MockEvaluationContext context = new MockEvaluationContext();
      Evaluation.property("property").retrieve(outbound, context);
   }

   @Test
   public void testCanAccessEvaluationContextPropertyInbound() throws Exception
   {
      MockEvaluationContext context = new MockEvaluationContext();
      try {
         Evaluation.property("property").retrieve(inbound, context);
         Assert.fail();
      }
      catch (IllegalArgumentException e) {}

      Evaluation.property("property").submit(inbound, context, "Foo");
      Object value = Evaluation.property("property").retrieve(inbound, context);

      Assert.assertEquals("Foo", value);
   }

   @Test
   public void testCanAccessEvaluationContextPropertyOutbound() throws Exception
   {
      MockEvaluationContext context = new MockEvaluationContext();
      try {
         Evaluation.property("property").retrieve(outbound, context);
         Assert.fail();
      }
      catch (IllegalArgumentException e) {}

      Evaluation.property("property").submit(outbound, context, "Foo");
      Object value = Evaluation.property("property").retrieve(outbound, context);

      Assert.assertEquals("Foo", value);
   }

}
