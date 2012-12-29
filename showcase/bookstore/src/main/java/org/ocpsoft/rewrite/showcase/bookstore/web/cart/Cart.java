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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.ocpsoft.rewrite.showcase.bookstore.model.Book;

@Named
@SessionScoped
public class Cart implements Serializable
{

   private static final long serialVersionUID = 1L;

   private List<Book> books = new ArrayList<Book>();

   public void addBook(Book book)
   {
      books.add(book);
   }

   public List<Book> getBooks()
   {
      return Collections.unmodifiableList(books);
   }

   public int getNumberOfBooks()
   {
      return books.size();
   }

}
