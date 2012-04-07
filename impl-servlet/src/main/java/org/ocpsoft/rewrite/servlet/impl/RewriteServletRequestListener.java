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

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.common.util.Iterators;
import org.ocpsoft.rewrite.servlet.spi.RequestListener;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class RewriteServletRequestListener implements ServletRequestListener
{
   private final List<RequestListener> listeners;

   @SuppressWarnings("unchecked")
   public RewriteServletRequestListener()
   {
      this.listeners = Iterators.asList(ServiceLoader.load(RequestListener.class));
   }

   @Override
   public void requestInitialized(final ServletRequestEvent event)
   {
      for (RequestListener listener : listeners) {
         listener.requestInitialized(event);
      }
   }

   @Override
   public void requestDestroyed(final ServletRequestEvent event)
   {
      for (RequestListener listener : listeners) {
         listener.requestDestroyed(event);
      }
   }

}
