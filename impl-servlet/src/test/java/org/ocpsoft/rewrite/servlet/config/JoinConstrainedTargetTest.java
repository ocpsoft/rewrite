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
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.ocpsoft.rewrite.context.Context;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.mock.MockEvaluationContext;
import org.ocpsoft.rewrite.servlet.RewriteLifecycleContext;
import org.ocpsoft.rewrite.servlet.config.rule.Join;
import org.ocpsoft.rewrite.servlet.impl.HttpInboundRewriteImpl;
import org.ocpsoft.rewrite.servlet.impl.HttpRewriteContextImpl;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class JoinConstrainedTargetTest
{
   private Rewrite rewrite;
   private HttpServletRequest request;
   private Context context = new HttpRewriteContextImpl(null, null, null, null, null);

   @Before
   public void before()
   {
      request = Mockito.mock(HttpServletRequest.class);

      Mockito.when(request.getServerName())
      .thenReturn("me.example.com");

      Mockito.when(request.getRequestURI())
      .thenReturn("/context/application/path");

      Mockito.when(request.getContextPath())
      .thenReturn("/context");
      
      Mockito.when(request.getAttribute(RewriteLifecycleContext.LIFECYCLE_CONTEXT_KEY))
      .thenReturn(context );

      rewrite = new HttpInboundRewriteImpl(request, null);
   }

   @Test
   public void testConstrainTargetSuccess() throws Exception
   {
      Assert.assertTrue(Join.path("/application/path").to("/{domain}/path")
               .where("domain").matches("me")
               .when(Domain.matches("{domain}.example.com")).evaluate(rewrite, new MockEvaluationContext()));
   }

   @Ignore
   @Test
   public void testConstrainTargetFails() throws Exception
   {
      Assert.assertFalse(Join.path("/application/path").to("/{domain}/path")
               .where("domain").matches("you")
               .when(Domain.matches("{domain}.example.com")).evaluate(rewrite, new MockEvaluationContext()));
   }
}
