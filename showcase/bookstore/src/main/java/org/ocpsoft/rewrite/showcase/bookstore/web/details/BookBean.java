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

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.annotation.Rule;
import org.ocpsoft.rewrite.faces.annotation.Deferred;
import org.ocpsoft.rewrite.faces.navigate.Navigate;
import org.ocpsoft.rewrite.showcase.bookstore.dao.BookDao;
import org.ocpsoft.rewrite.showcase.bookstore.model.Book;
import org.ocpsoft.rewrite.showcase.bookstore.web.cart.Cart;
import org.ocpsoft.rewrite.showcase.bookstore.web.utils.ResponseUtils;

@Named
@RequestScoped
@Rule("book")
@Join(path = "/buch/{isbn}", to = "/faces/book.xhtml")
public class BookBean
{

   @Parameter
   private Long isbn;

   private Book book;

   @EJB
   private BookDao bookDao;

   @Inject
   private Cart cartBean;

   @RequestAction
   @Deferred
   public String loadData()
   {

      book = bookDao.getByIsbn(isbn);

      if (book == null) {
         ResponseUtils.sendError(404);
         return null;
      }

      return null;

   }

   public Navigate addToCart()
   {

      cartBean.addBook(book);

      return Navigate.to(BookBean.class).with("isbn", isbn);

   }

   public Long getIsbn()
   {
      return isbn;
   }

   public void setIsbn(Long isbn)
   {
      this.isbn = isbn;
   }

   public Book getBook()
   {
      return book;
   }

}
