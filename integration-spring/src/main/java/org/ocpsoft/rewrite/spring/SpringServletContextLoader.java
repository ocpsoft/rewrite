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

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRequestEvent;

import org.ocpsoft.rewrite.servlet.spi.ContextListener;
import org.ocpsoft.rewrite.servlet.spi.RequestListener;

/**
 * Thread-safe {@link ServletContext} loader implementation for Spring.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class SpringServletContextLoader implements ContextListener, RequestListener
{
   private static final Map<ClassLoader, ServletContext> contextMap = new ConcurrentHashMap<>(1);

   @Override
   public void contextInitialized(ServletContextEvent event)
   {
      ServletContext servletContext = event.getServletContext();
      contextMap.put(Thread.currentThread().getContextClassLoader(), servletContext);
   }

   @Override
   public void contextDestroyed(ServletContextEvent event)
   {
      removeContext(event.getServletContext());
   }

   @Override
   public void requestInitialized(ServletRequestEvent event)
   {
      ServletContext servletContext = event.getServletContext();
      contextMap.put(Thread.currentThread().getContextClassLoader(), servletContext);
   }

   @Override
   public void requestDestroyed(ServletRequestEvent event)
   {
      removeContext(event.getServletContext());

   }

   private static void removeContext(ServletContext context)
   {
      if (contextMap.containsValue(context)) {
         for (Entry<ClassLoader, ServletContext> entry : contextMap.entrySet()) {
            if (entry.getValue() == context) {
               contextMap.remove(entry.getKey());
            }
         }
      }
   }

   public static ServletContext getCurrentServletContext()
   {
      return contextMap.get(Thread.currentThread().getContextClassLoader());
   }

   @Override
   public int priority()
   {
      return 0;
   }

}
