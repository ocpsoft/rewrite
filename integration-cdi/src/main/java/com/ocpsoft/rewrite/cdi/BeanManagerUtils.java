/*
 * JBoss, by Red Hat.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.ocpsoft.rewrite.cdi;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class BeanManagerUtils
{
   @SuppressWarnings("unchecked")
   public static <T> T getContextualInstance(final BeanManager manager, final Class<T> type)
   {
      T result = null;
      Bean<T> bean = (Bean<T>) manager.resolve(manager.getBeans(type));
      if (bean != null)
      {
         CreationalContext<T> context = manager.createCreationalContext(bean);
         if (context != null)
         {
            result = (T) manager.getReference(bean, type, context);
         }
      }
      return result;
   }

   @SuppressWarnings("unchecked")
   public static CreationalContext<Object> injectNonContextualInstance(final BeanManager manager, final Object instance)
   {
      if (instance != null)
      {
         CreationalContext<Object> creationalContext = manager.createCreationalContext(null);
         InjectionTarget<Object> injectionTarget = (InjectionTarget<Object>) manager
                  .createInjectionTarget(manager.createAnnotatedType(instance.getClass()));
         injectionTarget.inject(instance, creationalContext);
         return creationalContext;
      }
      return null;
   }
}
