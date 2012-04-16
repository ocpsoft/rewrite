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
package org.ocpsoft.rewrite.test;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.ocpsoft.rewrite.context.Context;
import org.ocpsoft.rewrite.mock.MockRewriteContext;
import org.ocpsoft.rewrite.servlet.event.BaseRewrite.Flow;
import org.ocpsoft.rewrite.servlet.event.ServletRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MockServletRewrite implements ServletRewrite<ServletRequest, ServletResponse>
{
   private Flow flow;
   private final ServletRequest request;
   private final ServletResponse response;

   public MockServletRewrite(ServletRequest request, ServletResponse response)
   {
      this.request = request;
      this.response = response;
   }

   @Override
   public Context getRewriteContext()
   {
      return new MockRewriteContext();
   }

   @Override
   public ServletRequest getRequest()
   {
      return request;
   }

   @Override
   public ServletResponse getResponse()
   {
      return response;
   }

   @Override
   public void abort()
   {
      this.flow = Flow.ABORT_REQUEST;
   }

   @Override
   public void proceed()
   {
      this.flow = Flow.PROCEED;
   }

   @Override
   public void handled()
   {
      this.flow = Flow.HANDLED;
   }

   @Override
   public Flow getFlow()
   {
      return flow;
   }

   @Override
   public void setFlow(Flow flow)
   {
      this.flow = flow;
   }
}
