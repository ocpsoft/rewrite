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
package org.ocpsoft.rewrite.faces.annotation.handler;

import javax.faces.context.FacesContext;

import org.ocpsoft.rewrite.annotation.api.ClassContext;
import org.ocpsoft.rewrite.annotation.api.HandlerChain;
import org.ocpsoft.rewrite.annotation.api.MethodContext;
import org.ocpsoft.rewrite.annotation.handler.HandlerWeights;
import org.ocpsoft.rewrite.annotation.spi.AnnotationHandler;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.faces.annotation.IgnorePostback;

public class IgnorePostbackHandler implements AnnotationHandler<IgnorePostback>
{

   @Override
   public Class<IgnorePostback> handles()
   {
      return IgnorePostback.class;
   }

   @Override
   public int priority()
   {
      // must execute after @Deferred
      return HandlerWeights.WEIGHT_TYPE_ENRICHING + 10;
   }

   @Override
   public void process(ClassContext context, IgnorePostback annotation, HandlerChain chain)
   {

      chain.proceed();

      if (context instanceof MethodContext) {
         Operation operation = (Operation) context.get(Operation.class);
         if (operation != null) {
            // replace the operation with a wrapped one that ignores postbacks
            context.put(Operation.class, new IgnorePostbackOperation(operation));
         }
      }

   }

   private static class IgnorePostbackOperation implements Operation
   {

      private final Operation delegate;

      public IgnorePostbackOperation(Operation delegate)
      {
         this.delegate = delegate;
      }

      @Override
      public void perform(Rewrite event, EvaluationContext context)
      {

         FacesContext facesContext = FacesContext.getCurrentInstance();
         if (facesContext == null) {
            throw new IllegalStateException("FacesContext.getCurrentInstance() returned null");
         }

         boolean postback = facesContext.getRenderKit().getResponseStateManager().isPostback(facesContext);
         if (!postback) {
            delegate.perform(event, context);
         }

      }

   }
}