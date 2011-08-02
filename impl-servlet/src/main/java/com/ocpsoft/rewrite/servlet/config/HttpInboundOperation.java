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
package com.ocpsoft.rewrite.servlet.config;

import com.ocpsoft.rewrite.config.InboundOperation;
import com.ocpsoft.rewrite.event.InboundRewrite;
import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;

/**
 * An operation that is only performed if the current {@link Rewrite} event is an {@link HttpInboundServletRewrite}
 * event.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public abstract class HttpInboundOperation extends InboundOperation
{
   /**
    * Perform the operation.
    */
   public abstract void performInbound(HttpInboundServletRewrite event);

   @Override
   public final void performInbound(final InboundRewrite event)
   {
      performInbound((HttpInboundServletRewrite) event);
   }
}
