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
package org.ocpsoft.rewrite.servlet.impl;

import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.common.util.Iterators;
import org.ocpsoft.rewrite.servlet.spi.ContextListener;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class RewriteServletContextListener implements ServletContextListener
{
   private List<ContextListener> listeners;

   @Override
   public void contextInitialized(final ServletContextEvent event)
   {
      for (ContextListener listener : getListeners()) {
         listener.contextInitialized(event);
      }
   }

   @Override
   public void contextDestroyed(final ServletContextEvent event)
   {
      for (ContextListener listener : getListeners()) {
         listener.contextDestroyed(event);
      }
   }

   private List<ContextListener> getListeners()
   {
      if (listeners == null)
         listeners = Iterators.asList(ServiceLoader.loadTypesafe(ContextListener.class));
      return listeners;
   }

}
