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
package org.ocpsoft.rewrite.cdi.bridge;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.ocpsoft.rewrite.cdi.events.AfterRewrite;
import org.ocpsoft.rewrite.cdi.events.AfterRewriteLifecycle;
import org.ocpsoft.rewrite.cdi.events.BeforeRewrite;
import org.ocpsoft.rewrite.cdi.events.BeforeRewriteLifecycle;
import org.ocpsoft.rewrite.servlet.http.HttpRewriteLifecycleListener;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.servlet.spi.RewriteLifecycleListener;

/**
 * Propagates events from {@link RewriteLifecycleListener} to CDI Event bus.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class RewriteLifecycleEventBridge extends HttpRewriteLifecycleListener
{
   @Inject
   private BeanManager manager;

   @Override
   public int priority()
   {
      return -100;
   }

   @Override
   public void beforeInboundLifecycle(final HttpServletRewrite event)
   {
      manager.fireEvent(new BeforeRewriteLifecycle(event));
   }

   @Override
   public void beforeInboundRewrite(final HttpServletRewrite event)
   {
      manager.fireEvent(new BeforeRewrite(event));
   }

   @Override
   public void afterInboundRewrite(final HttpServletRewrite event)
   {
      manager.fireEvent(new AfterRewrite(event));
   }

   @Override
   public void afterInboundLifecycle(final HttpServletRewrite event)
   {
      manager.fireEvent(new AfterRewriteLifecycle(event));
   }

   @Override
   public void beforeOutboundRewrite(final HttpServletRewrite event)
   {
      manager.fireEvent(new BeforeRewrite(event));
   }

   @Override
   public void afterOutboundRewrite(final HttpServletRewrite event)
   {
      manager.fireEvent(new AfterRewrite(event));
   }
}
