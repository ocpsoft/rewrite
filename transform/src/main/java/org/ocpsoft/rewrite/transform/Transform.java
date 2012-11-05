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
package org.ocpsoft.rewrite.transform;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.config.HttpOperation;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.servlet.impl.HttpRewriteWrappedResponse;

public class Transform extends HttpOperation
{

   private PipelineContentInterceptor pipeline = new PipelineContentInterceptor();

   public static Transform with(Class<? extends Transformer> transformerType)
   {
      return new Transform().apply(transformerType);
   }

   public Transform apply(Class<? extends Transformer> transformerType)
   {
      try {
         return apply(transformerType.newInstance());
      }
      catch (InstantiationException e) {
         throw new IllegalArgumentException(e);
      }
      catch (IllegalAccessException e) {
         throw new IllegalArgumentException(e);
      }
   }

   public Transform apply(Class<? extends Transformer> first, Class<? extends Transformer> second,
            Class<? extends Transformer> third)
   {
      apply(first);
      apply(second);
      apply(third);
      return this;
   }

   public Transform apply(Class<? extends Transformer> first, Class<? extends Transformer> second)
   {
      apply(first);
      apply(second);
      return this;
   }

   /*
    * invoking this one will create compiler warnings :(
    */
   public Transform apply(Class<? extends Transformer>... transformerTypes)
   {
      for (Class<? extends Transformer> transformerType : transformerTypes) {
         apply(transformerType);
      }
      return this;
   }

   public Transform apply(Transformer... transformers)
   {
      for (Transformer transformer : transformers) {
         pipeline.add(transformer);
      }
      return this;
   }

   @Override
   public void performHttp(HttpServletRewrite event, EvaluationContext context)
   {
      if (event instanceof HttpInboundServletRewrite) {
         HttpRewriteWrappedResponse.getCurrentInstance(event.getRequest()).addContentInterceptor(pipeline);
      }
   }

}
