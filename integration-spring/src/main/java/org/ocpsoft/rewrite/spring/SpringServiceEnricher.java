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
package org.ocpsoft.rewrite.spring;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.ocpsoft.common.spi.ServiceEnricher;
import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.servlet.spi.ContextListener;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 * {@link ServiceEnricher} implementation for Spring.
 * 
 * @author Christian Kaltepoth
 */
public class SpringServiceEnricher implements ServiceEnricher, ContextListener
{

   private final Logger log = Logger.getLogger(SpringServiceEnricher.class);
   private static ServletContext context;

   @Override
   public <T> void enrich(final T service)
   {
      if (context != null) {
         SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(service, context);
      }
      else {
         SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(service);
      }
      if (log.isDebugEnabled())
         log.debug("Enriched instance of service [" + service.getClass().getName() + "]");

   }

   @Override
   public <T> Collection<T> produce(final Class<T> type)
   {
      // TODO implement
      return new ArrayList<T>();
   }

   @Override
   public void contextInitialized(ServletContextEvent event)
   {
      context = event.getServletContext();
   }

   @Override
   public void contextDestroyed(ServletContextEvent event)
   {
      context = null;
   }

   @Override
   public int priority()
   {
      return 0;
   }

}
