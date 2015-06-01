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
package org.ocpsoft.rewrite.cdi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;

import org.ocpsoft.common.spi.ServiceEnricher;
import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.cdi.manager.BeanManagerAware;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class CdiServiceEnricher extends BeanManagerAware implements ServiceEnricher
{
   Logger log = Logger.getLogger(CdiServiceEnricher.class);

   @SuppressWarnings("unchecked")
   @Override
   public <T> Collection<T> produce(final Class<T> type)
   {
      Collection<T> result = new ArrayList<T>();

      BeanManager manager = getBeanManager();
      Set<Bean<?>> beans = manager.getBeans(type);
      for (Bean<?> bean : beans) {
         if (bean != null)
         {
            CreationalContext<T> context = (CreationalContext<T>) manager.createCreationalContext(bean);

            if (context != null)
            {
               result.add((T) manager.getReference(bean, type, context));
               if (log.isDebugEnabled())
               {
                  log.debug("Created CDI enriched service [" + bean.toString() + "]");
               }
            }
         }
      }

      return result;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T> void enrich(final T service)
   {
      if (service != null)
      {
         BeanManager manager = getBeanManager();
         CreationalContext<Object> context = manager.createCreationalContext(null);
         InjectionTarget<Object> injectionTarget = (InjectionTarget<Object>) manager
                  .createInjectionTarget(manager.createAnnotatedType(service.getClass()));

         injectionTarget.inject(service, context);

         if ((context != null) && log.isDebugEnabled())
         {
            log.debug("Enriched non-contextual instance of service [" + service.getClass().getName() + "]");
         }
      }
   }

}
