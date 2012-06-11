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
package org.ocpsoft.rewrite.transform.resolve;

import java.io.File;

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.transform.resource.FileResource;
import org.ocpsoft.rewrite.transform.resource.Resource;

public class WebResourceResolver implements ResourceResolver
{

   private final String fileType;

   public static WebResourceResolver fileType(String fileType)
   {
      return new WebResourceResolver(fileType);
   }

   public static WebResourceResolver identity()
   {
      return new WebResourceResolver(null);
   }

   private WebResourceResolver(String fileType)
   {
      this.fileType = fileType;
   }

   @Override
   public Resource getResource(Rewrite event, EvaluationContext context)
   {

      // will only work for HTTP requests
      if (event instanceof HttpServletRewrite) {
         HttpServletRewrite httpServletRewrite = (HttpServletRewrite) event;

         // the name of the requested resource
         String path = httpServletRewrite.getRequestPath();

         if (fileType != null) {
            path = path.replaceAll("\\.\\w+$", fileType);
         }

         // obtain the file system path of the resource
         ServletContext servletContext = httpServletRewrite.getRequest().getServletContext();
         String realPath = servletContext.getRealPath(path);

         // wrap the file in a FileResource
         if (realPath != null) {
            File resource = new File(realPath);
            if (resource.isFile() && resource.canRead()) {
               return new FileResource(resource);
            }
         }

      }

      return null;

   }

}
