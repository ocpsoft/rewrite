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
package org.ocpsoft.rewrite.showcase.domain;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Named
@Stateful
@RequestScoped
public class Domains
{
   private String currentName;
   private DomainEntity current;

   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager em;

   public void load()
   {
      if (current == null)
      {
         try {
            TypedQuery<DomainEntity> query = em.createQuery("from DomainEntity where name = :name", DomainEntity.class);
            query.setParameter("name", currentName);
            DomainEntity result = query.getSingleResult();

            current = result;
         }
         catch (Exception e) {
            /*
             * For the purposes of this example, let's just keep going so we 
             * can keep working
             */
         }
         System.out.println("Loaded settings for domain [" + current + "]");
      }
   }

   public DomainEntity getCurrent()
   {
      return current;
   }

   public void setCurrent(final DomainEntity currentDomain)
   {
      this.current = currentDomain;
   }

   public String getCurrentName()
   {
      return currentName;
   }

   public void setCurrentName(final String currentName)
   {
      this.currentName = currentName;
   }
}
