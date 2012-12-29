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
package org.ocpsoft.rewrite.showcase.bookstore.web.search;

import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.faces.navigate.Navigate;
import org.ocpsoft.rewrite.showcase.bookstore.dao.BookDao;
import org.ocpsoft.rewrite.showcase.bookstore.model.Book;

@Named
@RequestScoped
@Join(path = "/search", to = "/faces/search.xhtml")
public class SearchBean
{

   @Parameter("q")
   private String query;

   private List<Book> books;

   @EJB
   private BookDao bookDao;

   public Navigate search()
   {
      return Navigate.to(SearchBean.class).with("q", query);
   }

   @RequestAction
   public void init()
   {
      if (query != null && query.trim().length() > 0) {
         books = bookDao.findByQuery(query);
      }
   }

   public String getQuery()
   {
      return query;
   }

   public void setQuery(String query)
   {
      this.query = query;
   }

   public List<Book> getBooks()
   {
      return books;
   }

}
