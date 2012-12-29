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
package org.ocpsoft.rewrite.showcase.bookstore.web.details;

import java.io.IOException;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.faces.annotation.Deferred;
import org.ocpsoft.rewrite.showcase.bookstore.service.BookImageService;
import org.ocpsoft.rewrite.showcase.bookstore.web.utils.ResponseUtils;

@Named
@RequestScoped
@Join(path = "/images/book/{isbn}.jpg", to = "/faces/home.xhtml")
public class BookImageBean
{

   @Parameter
   private Long isbn;

   @EJB
   private BookImageService bookImageService;

   @RequestAction
   @Deferred
   public void getImage() throws IOException
   {

      // load image from the database
      byte[] image = bookImageService.getBookImage(isbn);

      // send 404 for unknown books
      if (image == null) {
         ResponseUtils.sendError(404);
         return;
      }

      FacesContext facesContext = FacesContext.getCurrentInstance();
      HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

      // set content type and send image
      response.setContentType("image/jpg");
      response.getOutputStream().write(image);
      response.flushBuffer();

      // end JSF lifecycle
      facesContext.responseComplete();

   }

   public Long getIsbn()
   {
      return isbn;
   }

   public void setIsbn(Long isbn)
   {
      this.isbn = isbn;
   }

}
