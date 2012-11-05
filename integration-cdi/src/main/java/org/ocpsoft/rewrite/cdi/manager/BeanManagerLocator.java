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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

import org.ocpsoft.common.pattern.WeightedComparator;
import org.ocpsoft.rewrite.cdi.spi.BeanManagerProvider;

/**
 * <p>
 * A utility for use in non-managed classes, which are not able to obtain a reference to the {@link BeanManager} using
 * injection.
 * </p>
 * <p/>
 * <p>
 * {@link BeanManagerProvider} is an SPI that may be implemented to allow third parties to register custom methods of
 * looking up the BeanManager in an external context. This class will consult implementations according to precedence.
 * </p>
 * <p/>
 * <p>
 * <b>**WARNING**</b> This class is <b>NOT</b> a clever way to get the BeanManager, and should be <b>avoided at all
 * costs</b>. If you need a handle to the {@link BeanManager} you should probably register an {@link Extension} instead
 * of using this class; have you tried using @{@link Inject}?
 * </p>
 * <p/>
 * <p>
 * If you think you need to use this class, chat to the community and make sure you aren't missing a trick!
 * </p>
 * 
 * @author Pete Muir
 * @author Nicklas Karlsson
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 * @author Stuart Douglas
 * @see BeanManagerProvider
 * @see BeanManagerAware
 */
public class BeanManagerLocator
{
   private volatile BeanManager beanManager;

   private volatile boolean lookupPerformed = false;

   private volatile BeanManagerProvider locatingProvider;

   private volatile List<BeanManagerProvider> providers;

   /**
    * If a lookup has not yet been performed, consult registered {@link BeanManagerProvider} implementations to locate
    * the {@link BeanManager} and return the result. If the {@link BeanManager} cannot be resolved, throw a
    * {@link BeanManagerUnavailableException}.
    * 
    * @return the BeanManager for the current bean archive
    * @throws BeanManagerUnavailableException if the BeanManager cannot be resolved
    */
   public BeanManager getBeanManager()
   {
      if (!lookupPerformed) {
         lookupBeanManager();
      }

      if (beanManager == null) {
         throw new BeanManagerUnavailableException(providers);
      }

      return beanManager;
   }

   /**
    * If a lookup has not yet been performed, consult registered {@link BeanManagerProvider} implementations to locate
    * the {@link BeanManager} and return whether it was found, caching the result.
    * 
    * @return <code>true</code> if the bean manager has been found, otherwise <code>false</code>
    */
   public boolean isBeanManagerAvailable()
   {
      if (!lookupPerformed) {
         lookupBeanManager();
      }

      return beanManager != null;
   }

   /**
    * Return the {@link BeanManagerProvider} that was used to locate the BeanManager. This method will not attempt a
    * lookup.
    * 
    * @return the BeanManagerProvider implementation
    */
   public BeanManagerProvider getLocatingProvider()
   {
      return locatingProvider;
   }

   private synchronized void lookupBeanManager()
   {
      if (!lookupPerformed) {
         final List<BeanManagerProvider> providers = loadServices();
         Collections.sort(providers, new WeightedComparator());
         for (BeanManagerProvider provider : providers) {
            beanManager = provider.getBeanManager();
            if (beanManager != null) {
               locatingProvider = provider;
               break;
            }
         }
         this.providers = providers;
         lookupPerformed = true;
      }
   }

   private List<BeanManagerProvider> loadServices()
   {
      List<BeanManagerProvider> providers = new ArrayList<BeanManagerProvider>();

      for (Iterator<BeanManagerProvider> it = ServiceLoader.load(BeanManagerProvider.class).iterator(); it.hasNext();) {
         providers.add(it.next());
      }
      return providers;
   }
}
