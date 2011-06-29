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
package com.ocpsoft.rewrite.cdi.bridge;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import com.ocpsoft.rewrite.cdi.events.AfterRewrite;
import com.ocpsoft.rewrite.cdi.events.AfterRewriteLifecycle;
import com.ocpsoft.rewrite.cdi.events.BeforeRewrite;
import com.ocpsoft.rewrite.cdi.events.BeforeRewriteLifecycle;
import com.ocpsoft.rewrite.servlet.http.HttpRewriteLifecycleListener;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import com.ocpsoft.rewrite.servlet.spi.RewriteLifecycleListener;

/**
 * Propagates events from {@link RewriteLifecycleListener} to CDI Event bus.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@SuppressWarnings("serial")
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
      manager.fireEvent(event, new AnnotationLiteral<BeforeRewriteLifecycle>() {});
   }

   @Override
   public void beforeInboundRewrite(final HttpServletRewrite event)
   {
      manager.fireEvent(event, new AnnotationLiteral<BeforeRewrite>() {});
   }

   @Override
   public void afterInboundRewrite(final HttpServletRewrite event)
   {
      manager.fireEvent(event, new AnnotationLiteral<AfterRewrite>() {});
   }

   @Override
   public void afterInboundLifecycle(final HttpServletRewrite event)
   {
      manager.fireEvent(event, new AnnotationLiteral<AfterRewriteLifecycle>() {});
   }

   @Override
   public void beforeOutboundRewrite(HttpServletRewrite event)
   {
      manager.fireEvent(event, new AnnotationLiteral<BeforeRewrite>() {});
   }

   @Override
   public void afterOutboundRewrite(HttpServletRewrite event)
   {
      manager.fireEvent(event, new AnnotationLiteral<AfterRewrite>() {});
   }
}
