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

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.ocpsoft.rewrite.showcase.bookstore.model.Book;
import org.ocpsoft.rewrite.showcase.bookstore.model.Category;

@Stateless
public class BookDao
{

   @PersistenceContext
   private EntityManager entityManager;

   public List<Book> findByCategory(Category category)
   {
      return entityManager
               .createQuery("SELECT b FROM Book b WHERE b.category = :category ORDER BY b.title", Book.class)
               .setParameter("category", category)
               .getResultList();
   }

   public Book getByIsbn(Long isbn)
   {
      try {
         return entityManager
                  .createQuery("SELECT b FROM Book b WHERE b.isbn = :isbn", Book.class)
                  .setParameter("isbn", isbn)
                  .setMaxResults(1)
                  .getSingleResult();
      }
      catch (NoResultException e) {
         return null;
      }
   }

   public List<Book> findByQuery(String query)
   {
      return entityManager
               .createQuery(
                        "SELECT b FROM Book b WHERE LOWER(b.title) LIKE :query OR LOWER(b.author) LIKE :query ORDER BY b.title",
                        Book.class)
               .setParameter("query", "%" + query.toLowerCase() + "%")
               .getResultList();
   }

   public List<Book> findByYear(Integer year)
   {
      return entityManager
               .createQuery("SELECT b FROM Book b WHERE b.year= :year ORDER BY b.title", Book.class)
               .setParameter("year", year)
               .getResultList();
   }

}
