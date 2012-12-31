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
import org.ocpsoft.rewrite.bind.Evaluation;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.mock.MockEvaluationContext;
import org.ocpsoft.rewrite.servlet.config.bind.Request;
import org.ocpsoft.rewrite.servlet.impl.HttpInboundRewriteImpl;
import org.ocpsoft.rewrite.servlet.impl.HttpOutboundRewriteImpl;
import org.ocpsoft.urlbuilder.AddressBuilder;

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
      outbound = new HttpOutboundRewriteImpl(request, null,
               AddressBuilder.create("http://example.com:8080/path?query=value"));
   }

   @Test
   public void testDomainMatchesInbound()
   {
      Assert.assertTrue(Hostname.matches("example.com").evaluate(inbound, new MockEvaluationContext()));
   }

   @Test
   public void testCanConvertAndValidateDomain() throws Exception
   {
      MockEvaluationContext context = new MockEvaluationContext();
      Hostname.matches("{p}.com").where("p").convertedBy(new DomainConverter()).evaluate(inbound, context);

      Assert.assertEquals("example", Evaluation.property("p").retrieve(inbound, context));
      Assert.assertEquals(new DomainConvertedType("example"),
               Evaluation.property("p").retrieveConverted(inbound, context));
   }

   @Test
   public void testDomainMatchesBindsInbound()
   {
      MockEvaluationContext context = new MockEvaluationContext();
      Assert.assertTrue(Hostname.matches("{domain}.com").where("domain").bindsTo(Request.attribute("domain"))
               .evaluate(inbound, context));

      // Invoke the binding.
      context.getPreOperations().get(0).perform(inbound, context);

      Assert.assertEquals("example", Evaluation.property("domain").retrieve(inbound, context));
   }

   @Test
   public void testDomainNotMatchesInbound()
   {
      Assert.assertFalse(Hostname.matches("other.com").evaluate(inbound, new MockEvaluationContext()));
   }

   @Test
   public void testDomainMatchesOutbound()
   {
      Assert.assertTrue(Hostname.matches("example.com").evaluate(outbound, new MockEvaluationContext()));
   }

   @Test
   public void testDomainNotMatchesOutbound()
   {
      Assert.assertFalse(Hostname.matches("other.com").evaluate(outbound, new MockEvaluationContext()));
   }
}
