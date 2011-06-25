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
package com.ocpsoft.rewrite.servlet;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.ocpsoft.rewrite.RewriteEvent;
import com.ocpsoft.rewrite.spi.RewriteListener;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class HttpRewriteListener implements RewriteListener<ServletRequest, ServletResponse>
{
   @Override
   public boolean handles(final ServletRequest request, final ServletResponse response)
   {
      return true;
   }

   @Override
   public int priority()
   {
      return 0;
   }

   @Override
   public void requestReceived(final ServletRequest request, final ServletResponse response)
   {
   }

   @Override
   public void rewriteStarted(final RewriteEvent<ServletRequest, ServletResponse> event)
   {
   }

   @Override
   public void rewriteCompleted(final RewriteEvent<ServletRequest, ServletResponse> event)
   {
   }

   @Override
   public void requestProcessed(final RewriteEvent<ServletRequest, ServletResponse> event)
   {
   }

}
