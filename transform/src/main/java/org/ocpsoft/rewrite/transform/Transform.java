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

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import org.ocpsoft.rewrite.transform.resolve.ResourceResolver;
import org.ocpsoft.rewrite.transform.resolve.WebResourceResolver;

public class Transform implements Rule
{

   private final Logger log = Logger.getLogger(Transform.class);

   private final Condition condition;

   private ResourceResolver resolver = WebResourceResolver.identity();

   private Pipeline pipeline = new Pipeline();

   public static Transform request(Condition condition)
   {
      return new Transform(condition);
   }

   public static Transform request(String fileType)
   {
      return request(Path.matches("{something}" + fileType).where("something").matches(".*"));
   }

   public Transform(Condition condition)
   {
      this.condition = condition;
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

   public Transform resolvedBy(ResourceResolver resourceResolver)
   {
      this.resolver = resourceResolver;
      return this;
   }

   @Override
   public String getId()
   {
      return null;
   }

   @Override
   public boolean evaluate(Rewrite event, EvaluationContext context)
   {

      // rendering effects only inbound requests
      if (event instanceof HttpInboundServletRewrite) {
         return condition.evaluate(event, context);
      }

      return false;

   }

   @Override
   public void perform(Rewrite event, EvaluationContext context)
   {

      // rendering effects only inbound requests
      if (event instanceof HttpInboundServletRewrite) {

         HttpInboundServletRewrite inboundRewrite = (HttpInboundServletRewrite) event;

         // try to load the underlying resource
         InputStream inputStream = resolver.getResource(event, context);

         // proceed only if requested resource has been found
         if (inputStream != null) {

            // IO errors must be handled here
            try {

               // run the rendering process
               HttpServletResponse response = inboundRewrite.getResponse();
               pipeline.transform(inputStream, response.getOutputStream());
               response.flushBuffer();

               // the application doesn't need to process the request anymore
               inboundRewrite.abort();

            }
            catch (IOException e) {
               log.error("Failed to render resource", e);
            }

         }

      }

   }

}
