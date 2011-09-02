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
package com.ocpsoft.rewrite.showcase.domain;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;

import com.ocpsoft.rewrite.config.Operation;
import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.servlet.config.HttpOperation;
import com.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import com.ocpsoft.rewrite.servlet.util.URLBuilder;

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

   public Operation load()
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
             * For the purposes of this example, let's just disable this so we 
             * can keep working
             */
            return new HttpOperation() {
               @Override
               public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
               {
                  URLBuilder url = URLBuilder.begin().addPathSegments(event.getRequestPath())
                           .addQueryParameters(event.getRequestQueryString())
                           .addQueryParameters("disableDomainRewrite");
                  ((HttpInboundServletRewrite) event).redirectTemporary(url.toURL());
               }
            };
         }
         System.out.println("Loaded settings for domain [" + current + "]");
      }
      return null;
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
