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

import org.ocpsoft.rewrite.showcase.bookstore.model.Category;

@Stateless
public class CategoryDao
{

   @PersistenceContext
   protected EntityManager entityManager;

   public List<Category> list()
   {
      return entityManager
               .createQuery("SELECT c FROM Category c", Category.class)
               .getResultList();
   }

   public Category getBySeoKey(String key)
   {
      try {
         return entityManager
                  .createQuery("SELECT c FROM Category c WHERE c.seoKey = :key", Category.class)
                  .setParameter("key", key)
                  .setMaxResults(1)
                  .getSingleResult();
      }
      catch (NoResultException e) {
         return null;
      }
   }

}
