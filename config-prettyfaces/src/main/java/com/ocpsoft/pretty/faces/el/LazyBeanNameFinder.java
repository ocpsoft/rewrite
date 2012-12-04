/*
 * Copyright 2010 Lincoln Baxter, III
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
package com.ocpsoft.pretty.faces.el;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.rewrite.el.spi.BeanNameResolver;

/**
 * <p>
 * Class implementing lazy resolving of bean names.
 * </p>
 * <p>
 * This class is typically created once and is than supplied to {@link LazyExpression} instances in the
 * {@link LazyExpression#LazyExpression(LazyBeanNameFinder, Class, String)} constructor.
 * </p>
 * 
 * @author Christian Kaltepoth
 */
public class LazyBeanNameFinder
{

   private final static Log log = LogFactory.getLog(LazyBeanNameFinder.class);

   /**
    * List of all resolvers. Initialized once on object creation
    */
   private final List<BeanNameResolver> resolvers = new ArrayList<BeanNameResolver>();

   /**
    * Creates a new {@link LazyBeanNameFinder}. The constructor will find all implementations of
    * {@link ELBeanNameResolver} by using the {@link ServiceLoader} mechanism.
    * 
    * @param servletContext The servlet context
    */
   public LazyBeanNameFinder(ServletContext servletContext)
   {

      // we use the context classloader
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

      // fallback, if no context classloader exists
      if (classLoader == null)
      {
         classLoader = this.getClass().getClassLoader();
      }

      // find resolvers via ServiceLoader
      @SuppressWarnings("unchecked")
      Iterator<BeanNameResolver> beanNameFinderIterator = ServiceLoader.load(BeanNameResolver.class).iterator();

      // call init() method on all resolvers
      while (beanNameFinderIterator.hasNext())
      {

         // log resolver name
         BeanNameResolver resolver = beanNameFinderIterator.next();
         if (log.isTraceEnabled())
         {
            log.trace("Initializing BeanNameResolver: " + resolver.getClass().getName());
         }

         try
         {
            resolvers.add(resolver);
         }
         catch (ClassFormatError e)
         {
            /*
             * Seems to happen for CDI classes when using GWT
             * In this case just ignore the resolver
             * See: http://code.google.com/p/prettyfaces/issues/detail?id=101
             */
            log.warn("Failed to initialize " + resolver.getClass().getSimpleName() + ": " + e.getMessage());
         }
      }
   }

   /**
    * Find the bean name of the supplied class. This method will try to resolve the bean name by calling all registered
    * implementations of {@link ELBeanNameResolver}. This method will either return the resolved name or throw an
    * {@link IllegalStateException}, if no resolver knows the name of the bean.
    * 
    * @param clazz The class of the bean
    * @return The resolved bean name
    * @throws IllegalStateException If the name of the bean cannot be resolved
    */
   public String findBeanName(Class<?> clazz) throws IllegalStateException
   {

      // process all resolvers
      for (BeanNameResolver resolver : resolvers)
      {

         // try to resolve bean name with current resolver
         String name = resolver.getBeanName(clazz);

         // return the bean name, if the resolver was successful
         if (name != null)
         {
            return name;
         }

      }

      // No resolver knows the name of the bean
      throw new IllegalStateException("Cannot find name of bean '" + clazz.getName()
               + "'! You should place a @URLBeanName annotation on this class to let PrettyFaces know its name.");

   }

}
