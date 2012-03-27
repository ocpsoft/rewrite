/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ocpsoft.rewrite.cdi.manager;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

/**
 * A convenient base class for obtaining a reference to the BeanManager from a non-managed class, or a class which may
 * not be managed in certain environments (i.e., a listener in a Servlet container).
 * <p/>
 * <p>
 * This class will first check whether the {@link BeanManager} was injected. If the injection point was not satisfied,
 * then this class will use the {@link BeanManagerLocator} to lookup the BeanManager in an external context (e.g.,
 * JNDI).
 * </p>
 * <p/>
 * <p>
 * It's generally acceptable to use this class in cases where injection is not guaranteed. However, in general you
 * should ensure that the classes needing CDI services, such as injection, be available as managed classes.
 * </p>
 * 
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
public abstract class BeanManagerAware
{
   @Inject
   private BeanManager beanManager;

   private BeanManagerLocator locator = new BeanManagerLocator();

   public BeanManager getBeanManager()
   {
      if (beanManager != null) {
         return beanManager;
      }

      return locator.getBeanManager();
   }

   public boolean isBeanManagerAvailable()
   {
      if (beanManager != null) {
         return true;
      }

      return locator.isBeanManagerAvailable();
   }

   public boolean isBeanManagerInjected()
   {
      return beanManager != null;
   }

   public BeanManagerLocator getBeanManagerLocator()
   {
      return locator;
   }
}
