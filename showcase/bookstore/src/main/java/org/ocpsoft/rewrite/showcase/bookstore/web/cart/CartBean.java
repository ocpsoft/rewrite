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
package org.ocpsoft.rewrite.showcase.bookstore.web.cart;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.faces.annotation.Deferred;
import org.ocpsoft.rewrite.showcase.bookstore.model.Book;

@Named
@RequestScoped
@Join(path = "/cart", to = "/faces/cart.xhtml")
public class CartBean
{

   @Inject
   private Cart cart;

   private List<Book> books;

   private float sum;

   @RequestAction
   @Deferred
   public void init()
   {

      books = cart.getBooks();

      sum = 0.0f;
      for (Book book : books) {
         sum += book.getPrice();
      }

   }

   public List<Book> getBooks()
   {
      return books;
   }

   public float getSum()
   {
      return sum;
   }

}
