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
package org.ocpsoft.rewrite.showcase.bookstore.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.ocpsoft.rewrite.showcase.bookstore.model.Book;
import org.ocpsoft.rewrite.showcase.bookstore.model.Category;

@Stateless
public class BookDao
{

   @Inject
   private TestDataRepository repository;

   public List<Book> findByCategory(Category category)
   {
      List<Book> result = new ArrayList<Book>();
      for (Book book : repository.getBooks()) {
         if (book.getCategory().equals(category)) {
            result.add(book);
         }
      }
      return Collections.unmodifiableList(result);
   }

   public Book getByIsbn(Long isbn)
   {
      for (Book book : repository.getBooks()) {
         if (book.getIsbn().equals(isbn)) {
            return book;
         }
      }
      return null;
   }

   public List<Book> findByQuery(String query)
   {
      List<Book> result = new ArrayList<Book>();
      for (Book book : repository.getBooks()) {
         if (book.getTitle().toLowerCase().contains(query.toLowerCase())
                  || book.getAuthor().toLowerCase().contains(query.toLowerCase())) {
            result.add(book);
         }
      }
      return Collections.unmodifiableList(result);
   }

   public List<Book> findByYear(Integer year)
   {
      List<Book> result = new ArrayList<Book>();
      for (Book book : repository.getBooks()) {
         if (book.getYear().equals(year)) {
            result.add(book);
         }
      }
      return Collections.unmodifiableList(result);
   }

}
