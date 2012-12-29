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
package org.ocpsoft.rewrite.showcase.bookstore.web;

import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.faces.annotation.Deferred;
import org.ocpsoft.rewrite.showcase.bookstore.dao.CategoryDao;
import org.ocpsoft.rewrite.showcase.bookstore.model.Category;

@Named
@RequestScoped
@Join(path = "/home", to = "/faces/home.xhtml")
public class HomeBean
{

   @EJB
   private CategoryDao categoryDao;

   private List<Category> categories;

   @RequestAction
   @Deferred
   public void loadData()
   {
      categories = categoryDao.list();
   }

   public List<Category> getCategories()
   {
      return categories;
   }

}
