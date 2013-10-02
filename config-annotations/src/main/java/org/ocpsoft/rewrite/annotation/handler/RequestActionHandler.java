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
package org.ocpsoft.rewrite.annotation.handler;

import java.lang.reflect.Method;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.annotation.api.HandlerChain;
import org.ocpsoft.rewrite.annotation.api.MethodContext;
import org.ocpsoft.rewrite.annotation.spi.MethodAnnotationHandler;
import org.ocpsoft.rewrite.config.Invoke;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.config.Operations;
import org.ocpsoft.rewrite.el.El;

public class RequestActionHandler extends MethodAnnotationHandler<RequestAction>
{

   @Override
   public Class<RequestAction> handles()
   {
      return RequestAction.class;
   }

   @Override
   public int priority()
   {
      return HandlerWeights.WEIGHT_TYPE_STRUCTURAL;
   }

   @Override
   public void process(MethodContext context, RequestAction annotation, HandlerChain chain)
   {

      // create an Operation for executing this method
      Method method = context.getJavaMethod();
      El el = El.retrievalMethod(context.getJavaClass(), method.getName());
      Operation plainOperation = Invoke.binding(el);

      // let subsequent handlers enrich the operation
      context.put(Operation.class, plainOperation);
      chain.proceed();
      Operation enrichedOperation = (Operation) context.get(Operation.class);
      Assert.notNull(enrichedOperation, "Operation was removed from the context");

      // append this operation to the rule
      context.getRuleBuilder().perform(Operations.onInbound(enrichedOperation));

   }

}
