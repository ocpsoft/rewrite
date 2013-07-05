/*
 * Copyright 2011 <a href="mailto:fabmars@gmail.com">Fabien Marsaud</a>
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
import org.ocpsoft.rewrite.servlet.impl.HttpOutboundRewriteImpl;
import org.ocpsoft.urlbuilder.AddressBuilder;

/**
 * @author <a href="mailto:fabmars@gmail.com">Fabien Marsaud</a>
 * 
 */
public class NoPathTest
{
   private Rewrite outboundDryDomain;
   private Rewrite outboundLoneAnchor;
   private HttpServletRequest request;

   @Before
   public void before()
   {
      request = Mockito.mock(HttpServletRequest.class);

      outboundDryDomain = new HttpOutboundRewriteImpl(request, null, null,
               AddressBuilder.create("http://ocpsoft.org")); // No trailing slash on purpose
      outboundLoneAnchor = new HttpOutboundRewriteImpl(request, null, null,
          AddressBuilder.create("#subsection"));
   }

   @Test
   public void testDomainNoPath() throws Exception
   {
      Assert.assertFalse(Path.matches("").evaluate(outboundDryDomain, new MockEvaluationContext()));
   }

   @Test
   public void testLoneAnchor() throws Exception
   {
      Assert.assertFalse(Path.matches("").evaluate(outboundLoneAnchor, new MockEvaluationContext()));
   }

}
