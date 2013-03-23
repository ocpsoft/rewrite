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
package org.ocpsoft.rewrite.config;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.InboundRewrite;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * An {@link Operation} that is only performed if the current {@link Rewrite} event is an
 * {@link InboundRewrite} event.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public abstract class InboundOperation extends DefaultOperationBuilder
{

   @Override
   public final void perform(final Rewrite event, final EvaluationContext context)
   {
      if (event instanceof InboundRewrite)
      {
         performInbound((InboundRewrite) event);
      }
   }

   /**
    * Perform the {@link Operation}.
    */
   public abstract void performInbound(InboundRewrite event);

}
