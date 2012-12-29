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
package org.ocpsoft.rewrite.showcase.bookstore.web.list;

import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.faces.annotation.Deferred;
import org.ocpsoft.rewrite.showcase.bookstore.dao.BookDao;
import org.ocpsoft.rewrite.showcase.bookstore.dao.CategoryDao;
import org.ocpsoft.rewrite.showcase.bookstore.model.Book;
import org.ocpsoft.rewrite.showcase.bookstore.model.Category;
import org.ocpsoft.rewrite.showcase.bookstore.web.utils.ResponseUtils;

@Named
@RequestScoped
@Join(path = "/category/{seoKey}", to = "/faces/category.xhtml")
public class CategoryBean
{

   @Parameter
   private String seoKey;

   @EJB
   private CategoryDao categoryDao;

   @EJB
   private BookDao bookDao;

   private List<Book> books;

   @RequestAction
   @Deferred
   public String loadData()
   {

      Category category = categoryDao.getBySeoKey(seoKey);

      if (category == null) {
         ResponseUtils.sendError(404);
         return null;
      }

      books = bookDao.findByCategory(category);

      return null;

   }

   public List<Book> getBooks()
   {
      return books;
   }

   public String getSeoKey()
   {
      return seoKey;
   }

   public void setSeoKey(String seoKey)
   {
      this.seoKey = seoKey;
   }

}
