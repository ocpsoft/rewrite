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
package org.ocpsoft.rewrite.render;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;

public class Render implements Rule
{

   private final Logger log = Logger.getLogger(Render.class);

   private final Renderer renderer;

   private String suffix;

   public static Render with(Renderer renderer)
   {
      return new Render(renderer);
   }

   public static Render with(Class<? extends Renderer> rendererType)
   {
      try {
         return new Render(rendererType.newInstance());
      }
      catch (InstantiationException e) {
         throw new IllegalArgumentException(e);
      }
      catch (IllegalAccessException e) {
         throw new IllegalArgumentException(e);
      }
   }

   private Render(Renderer renderer)
   {
      this.renderer = renderer;
      fileType(renderer.defaultFileType());
   }

   public Render fileType(String fileType)
   {
      this.suffix = "." + fileType;
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

         // the rule matches if the path ends with the suffix
         String path = ((HttpInboundServletRewrite) event).getRequestPath();
         if (path.endsWith(suffix)) {
            return true;
         }

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
         ServletContext servletContext = inboundRewrite.getRequest().getServletContext();
         InputStream inputStream = servletContext.getResourceAsStream(inboundRewrite.getRequestPath());

         // proceed only if requested resource has been found
         if (inputStream != null) {

            // IO errors must be handled here
            try {

               // run the rendering process
               HttpServletResponse response = inboundRewrite.getResponse();
               renderer.render(inputStream, response.getOutputStream());
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
