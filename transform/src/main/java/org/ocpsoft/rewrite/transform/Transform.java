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

import javax.servlet.http.HttpServletResponse;

import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.RewriteWrappedResponse;
import org.ocpsoft.rewrite.servlet.config.HttpOperation;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * An {@link Operation} that applies one or more {@link Transformer} implementations to the {@link HttpServletResponse}.
 * 
 * @author Christian Kaltepoth
 */
public class Transform extends HttpOperation
{
   private PipelineContentInterceptor pipeline = new PipelineContentInterceptor();

   private Transform()
   {
      // hide default constructor
   }

   /**
    * Create a {@link Transposition} instance that applies the given {@link Transformer}.
    */
   public static Transform with(Transformer transformer)
   {
      return new Transform().then(transformer);
   }

   /**
    * Add another {@link Transformer} implementation to the transformer pipeline. The supplied transformer will be
    * executed AFTER all other previously added transformer instances.
    */
   public Transform then(Transformer transformer)
   {
      pipeline.add(transformer);
      return this;
   }

   @Override
   public void performHttp(HttpServletRewrite event, EvaluationContext context)
   {
      if (event instanceof HttpInboundServletRewrite) {
         RewriteWrappedResponse.getCurrentInstance(event.getRequest()).addContentInterceptor(pipeline);
      }
   }

   @Override
   public String toString()
   {
      return "Transform.with(" + pipeline + ")";
   }

}
