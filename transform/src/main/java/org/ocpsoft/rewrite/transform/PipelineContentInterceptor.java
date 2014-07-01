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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.ocpsoft.common.util.Streams;
import org.ocpsoft.rewrite.servlet.config.response.ResponseContent;
import org.ocpsoft.rewrite.servlet.config.response.ResponseContentInterceptor;
import org.ocpsoft.rewrite.servlet.config.response.ResponseContentInterceptorChain;
import org.ocpsoft.rewrite.servlet.config.response.ResponseStreamWrapper;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * This pipeline is responsible to apply the registered {@link Transformer} implementations to the intercepted response
 * in the expected order.
 * <p>
 * TODO An alternate implementation should probably be provided that uses a {@link ResponseStreamWrapper}.
 * {@link Transformer} implementations would then need to select between stream wrapping or content interception.
 * 
 * @author Christian Kaltepoth
 */
class PipelineContentInterceptor implements ResponseContentInterceptor
{

   private List<Transformer> pipeline = new ArrayList<Transformer>();

   public void add(Transformer transformer)
   {
      pipeline.add(transformer);
   }

   @Override
   public void intercept(HttpServletRewrite event, ResponseContent buffer, ResponseContentInterceptorChain chain)
   {
      try {
         ByteArrayOutputStream output = new ByteArrayOutputStream();
         transform(event, new ByteArrayInputStream(buffer.getContents()), output);
         buffer.setContents(output.toByteArray());
         chain.proceed();
      }
      catch (IOException e) {
         throw new IllegalStateException("Failed to apply transformation pipeline", e);
      }
   }

   public void transform(HttpServletRewrite event, InputStream input, OutputStream output) throws IOException
   {

      // just do a copy if no transformers have been added
      if (pipeline.isEmpty()) {
         Streams.copy(input, output);
      }

      // delegate if there is only one transformer
      else if (pipeline.size() == 1) {
         pipeline.get(0).transform(event, input, output);
      }

      // multiple transformers
      else {

         // prepar for the first transformation step
         InputStream in = input;
         OutputStream out = new ByteArrayOutputStream();

         for (int i = 0; i < pipeline.size(); i++) {

            // run transformation
            pipeline.get(i).transform(event, in, out);

            // if it isn't the last transformation step -> prepare next one
            if (i < pipeline.size() - 1) {

               // prepare input for next iteration
               ByteArrayOutputStream lastOutput = (ByteArrayOutputStream) out;
               in = new ByteArrayInputStream(lastOutput.toByteArray());

               // if the next transform step is the last one, write directly to the output
               if (i == pipeline.size() - 2) {
                  out = output;
               }

               // other steps write into the last byte array again
               else {
                  lastOutput.reset();
               }
            }
         }
      }
   }

   @Override
   public String toString()
   {
      return "" + pipeline;
   }
}
