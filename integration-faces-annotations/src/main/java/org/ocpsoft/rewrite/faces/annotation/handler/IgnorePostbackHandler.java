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

import org.ocpsoft.rewrite.annotation.api.ClassContext;
import org.ocpsoft.rewrite.annotation.api.FieldContext;
import org.ocpsoft.rewrite.annotation.api.HandlerChain;
import org.ocpsoft.rewrite.annotation.api.MethodContext;
import org.ocpsoft.rewrite.annotation.handler.HandlerWeights;
import org.ocpsoft.rewrite.annotation.spi.AnnotationHandler;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.faces.annotation.IgnorePostback;
import org.ocpsoft.rewrite.faces.annotation.config.IgnorePostbackBinding;
import org.ocpsoft.rewrite.faces.annotation.config.IgnorePostbackOperation;

/**
 * Handler implementation of {@link IgnorePostback} annotation.
 * 
 * @author Christian Kaltepoth
 */
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
      /*
       * Must be executed after @Deferred but before all others so that the IgnorePostback wrappers
       * are deferred but also wrap everything else
       */
      return HandlerWeights.WEIGHT_TYPE_ENRICHING + 5;
   }

   @Override
   public void process(ClassContext context, IgnorePostback annotation, HandlerChain chain)
   {

      if (context instanceof MethodContext) {
         Operation operation = (Operation) context.get(Operation.class);
         if (operation != null) {
            // replace the operation with a wrapped one that ignores postbacks
            context.put(Operation.class, new IgnorePostbackOperation(operation));
         }
      }

      if (context instanceof FieldContext) {
         Binding binding = (Binding) context.get(Binding.class);
         if (binding != null) {
            // replace the binding with a wrapped one that ignores postbacks
            context.put(Binding.class, new IgnorePostbackBinding(binding));
         }
      }

      // first let subsequent handlers wrap or enrich the binding/operation
      chain.proceed();

   }
}