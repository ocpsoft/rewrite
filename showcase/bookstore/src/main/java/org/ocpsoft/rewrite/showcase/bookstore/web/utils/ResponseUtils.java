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
package org.ocpsoft.rewrite.showcase.bookstore.web.utils;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

public class ResponseUtils
{

   public static void sendError(int code)
   {
      try {
         FacesContext context = FacesContext.getCurrentInstance();
         HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
         response.sendError(code);
         context.responseComplete();
      }
      catch (IOException e) {
         throw new IllegalStateException(e);
      }
   }

}
