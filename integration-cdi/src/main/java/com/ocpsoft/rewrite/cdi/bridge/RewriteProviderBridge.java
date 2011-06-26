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

import com.ocpsoft.rewrite.cdi.events.RewriteInbound;
import com.ocpsoft.rewrite.cdi.events.RewriteOutbound;
import com.ocpsoft.rewrite.inbound.HttpRewriteEvent;
import com.ocpsoft.rewrite.spi.helper.HttpRewriteProvider;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class RewriteProviderBridge extends HttpRewriteProvider
{
   @Inject
   private BeanManager manager;

   @Override
   public void rewriteInbound(final HttpRewriteEvent event)
   {
      manager.fireEvent(event, new AnnotationLiteral<RewriteInbound>()
      {
      });
   }

   @Override
   public void rewriteOutbound(final HttpRewriteEvent event)
   {
      manager.fireEvent(event, new AnnotationLiteral<RewriteOutbound>()
      {
      });
   }

   @Override
   public int priority()
   {
      return 100;
   }

}
