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

import com.ocpsoft.rewrite.config.Operation;
import com.ocpsoft.rewrite.config.OperationBuilder;
import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * An {@link Operation} that is only performed if the current {@link Rewrite} event is an instance of
 * {@link HttpServletRewrite}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class HttpOperation extends OperationBuilder
{
   /**
    * Perform this operation for the given {@link HttpServletRewrite} event.
    */
   public abstract void performHttp(HttpServletRewrite event, EvaluationContext context);

   @Override
   public void perform(final Rewrite event, final EvaluationContext context)
   {
      if (event instanceof HttpServletRewrite)
      {
         performHttp((HttpServletRewrite) event, context);
      }
      else {
         throw new IllegalArgumentException("Cannot apply " + HttpOperation.class.getName() + " to event of type ["
                  + event.getClass().getName() + "]. Must be of type " + HttpServletRewrite.class.getName());
      }
   }
}
