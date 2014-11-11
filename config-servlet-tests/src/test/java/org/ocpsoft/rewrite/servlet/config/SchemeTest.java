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
import org.ocpsoft.rewrite.servlet.impl.HttpInboundRewriteImpl;
import org.ocpsoft.rewrite.servlet.impl.HttpOutboundRewriteImpl;
import org.ocpsoft.rewrite.util.ParameterUtils;
import org.ocpsoft.urlbuilder.AddressBuilder;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class SchemeTest
{
   private Rewrite inbound;
   private Rewrite outbound;
   private HttpServletRequest request;

   @Before
   public void before()
   {
      request = Mockito.mock(HttpServletRequest.class);
      Mockito.when(request.getScheme())
               .thenReturn("https");

      inbound = new HttpInboundRewriteImpl(request, null, null);
      outbound = new HttpOutboundRewriteImpl(request, null, null,
               AddressBuilder.create("http://example.com:8080/path?query=value"));
   }

   @Test
   public void testSchemeMatchesInbound()
   {
      Assert.assertTrue(Scheme.matches("https").evaluate(inbound, new MockEvaluationContext()));
   }

   @Test
   public void testShemeMatchesInboundParameterized()
   {
      MockEvaluationContext context = new MockEvaluationContext();
      Scheme scheme = Scheme.matches("{scheme}");

      ParameterUtils.initialize(context, scheme);

      Assert.assertTrue(scheme.evaluate(inbound, context));
   }

   @Test
   public void testSchemeNotMatchesInbound()
   {
      Assert.assertFalse(Scheme.matches("ftp").evaluate(inbound, new MockEvaluationContext()));
   }

   @Test
   public void testSchemeMatchesOutbound()
   {
      Assert.assertTrue(Scheme.matches("http").evaluate(outbound, new MockEvaluationContext()));
   }

   @Test
   public void testSchemeNotMatchesOutbound()
   {
      Assert.assertFalse(Scheme.matches("scp").evaluate(outbound, new MockEvaluationContext()));
   }
}
