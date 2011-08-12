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
package com.ocpsoft.rewrite.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.ocpsoft.rewrite.logging.Logger;
import com.ocpsoft.rewrite.logging.LoggerFactory;
import com.ocpsoft.rewrite.spi.ServiceEnricher;
import com.ocpsoft.rewrite.util.Iterators;
import com.ocpsoft.rewrite.util.ServiceLogger;

/**
 * This class handles looking up service providers on the class path. It implements the <a
 * href="http://java.sun.com/javase/6/docs/technotes/guides/jar/jar.html#Service%20Provider" >Service Provider section
 * of the JAR File Specification</a>.
 * 
 * The Service Provider programmatic lookup was not specified prior to Java 6 so this interface allows use of the
 * specification prior to Java 6.
 * 
 * The API is copied from <a href="http://java.sun.com/javase/6/docs/api/java/util/ServiceLoader.html"
 * >java.util.ServiceLoader</a>
 * 
 * @author Pete Muir
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @author Nicklas Karlsson
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ServiceLoader<S> implements Iterable<S>
{
   private static final String SERVICES = "META-INF/services";

   Logger log = LoggerFactory.getLogger(ServiceLoader.class);

   /**
    * Creates a new service loader for the given service type, using the current thread's context class loader.
    * 
    * An invocation of this convenience method of the form
    * 
    * {@code ServiceLoader.load(service)</code>}
    * 
    * is equivalent to
    * 
    * <code>ServiceLoader.load(service,
    *                   Thread.currentThread().getContextClassLoader())</code>
    * 
    * @param service The interface or abstract class representing the service
    * @return A new service loader
    */
   @SuppressWarnings("rawtypes")
   public static <S> ServiceLoader load(final Class<S> service)
   {
      return load(service, Thread.currentThread().getContextClassLoader());
   }

   /**
    * Creates a new service loader for the given service type, using the current thread's context class loader.
    * 
    * An invocation of this convenience method of the form
    * 
    * {@code ServiceLoader.load(service)</code>}
    * 
    * is equivalent to
    * 
    * <code>ServiceLoader.load(service,
    *                   Thread.currentThread().getContextClassLoader())</code>
    * 
    * @param service The interface or abstract class representing the service
    * @return A new service loader
    */
   public static <S> ServiceLoader<S> loadTypesafe(final Class<S> service)
   {
      return load(service, Thread.currentThread().getContextClassLoader());
   }

   /**
    * Creates a new service loader for the given service type and class loader.
    * 
    * @param service The interface or abstract class representing the service
    * @param loader The class loader to be used to load provider-configuration files and provider classes, or null if
    *           the system class loader (or, failing that, the bootstrap class loader) is to be used
    * @return A new service loader
    */
   public static <S> ServiceLoader<S> load(final Class<S> service, ClassLoader loader)
   {
      if (loader == null)
      {
         loader = service.getClassLoader();
      }
      return new ServiceLoader<S>(service, loader);
   }

   /**
    * Creates a new service loader for the given service type, using the extension class loader.
    * 
    * This convenience method simply locates the extension class loader, call it extClassLoader, and then returns
    * 
    * <code>ServiceLoader.load(service, extClassLoader)</code>
    * 
    * If the extension class loader cannot be found then the system class loader is used; if there is no system class
    * loader then the bootstrap class loader is used.
    * 
    * This method is intended for use when only installed providers are desired. The resulting service will only find
    * and load providers that have been installed into the current Java virtual machine; providers on the application's
    * class path will be ignored.
    * 
    * @param service The interface or abstract class representing the service
    * @return A new service loader
    */
   public static <S> ServiceLoader<S> loadInstalled(final Class<S> service)
   {
      throw new UnsupportedOperationException("Not implemented");
   }

   private final String serviceFile;
   private final Class<S> expectedType;
   private final ClassLoader loader;

   private Set<S> providers;

   private ServiceLoader(final Class<S> service, final ClassLoader loader)
   {
      this.loader = loader;
      this.serviceFile = SERVICES + "/" + service.getName();
      this.expectedType = service;
   }

   /**
    * Clear this loader's provider cache so that all providers will be reloaded.
    * 
    * After invoking this method, subsequent invocations of the iterator method will lazily look up and instantiate
    * providers from scratch, just as is done by a newly-created loader.
    * 
    * This method is intended for use in situations in which new providers can be installed into a running Java virtual
    * machine.
    */
   public void reload()
   {
      providers = new HashSet<S>();

      for (URL serviceFile : loadServiceFiles())
      {
         loadServiceFile(serviceFile);
      }
   }

   private List<URL> loadServiceFiles()
   {
      List<URL> serviceFiles = new ArrayList<URL>();
      try
      {
         Enumeration<URL> serviceFileEnumerator = loader.getResources(serviceFile);
         while (serviceFileEnumerator.hasMoreElements())
         {
            serviceFiles.add(serviceFileEnumerator.nextElement());
         }
      }
      catch (IOException e)
      {
         throw new RuntimeException("Could not load resources from " + serviceFile, e);
      }
      return serviceFiles;
   }

   private void loadServiceFile(final URL serviceFile)
   {
      InputStream is = null;
      try
      {
         is = serviceFile.openStream();
         BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
         String serviceClassName = null;
         while ((serviceClassName = reader.readLine()) != null)
         {
            serviceClassName = trim(serviceClassName);
            if (serviceClassName.length() > 0)
            {
               loadService(serviceClassName);
            }
         }
      }
      catch (IOException e)
      {
         throw new RuntimeException("Could not read services file " + serviceFile, e);
      }
      finally
      {
         if (is != null)
         {
            try
            {
               is.close();
            }
            catch (IOException e)
            {
               throw new RuntimeException("Could not close services file " + serviceFile, e);
            }
         }
      }
   }

   private String trim(String line)
   {
      final int comment = line.indexOf('#');

      if (comment > -1)
      {
         line = line.substring(0, comment);
      }
      return line.trim();
   }

   private void loadService(final String serviceClassName)
   {
      Class<? extends S> serviceClass = loadClass(serviceClassName);
      if (serviceClass == null)
      {
         return;
      }
      S serviceInstance = prepareInstance(serviceClass);
      if (serviceInstance == null)
      {
         return;
      }
      providers.add(serviceInstance);
   }

   private Class<? extends S> loadClass(final String serviceClassName)
   {
      Class<?> clazz = null;
      Class<? extends S> serviceClass = null;
      try
      {
         clazz = loader.loadClass(serviceClassName);
         serviceClass = clazz.asSubclass(expectedType);
      }
      catch (ClassNotFoundException e)
      {
         log.warn("ClassNotFoundException: Service class [" + serviceClassName + "] could not be loaded.");
      }
      catch (ClassCastException e)
      {
         throw new ClassCastException("ClassCastException: Service class [" + serviceClassName
                  + "] did not implement the interface [" + expectedType.getName() + "]");
      }
      return serviceClass;
   }

   private static java.util.ServiceLoader<ServiceEnricher> enricherLoader = null;

   /**
    * Prepare our enriched service instance using any provided {@link ServiceEnricher} classes.
    * 
    * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
    */
   private S prepareInstance(final Class<? extends S> serviceClass)
   {
      try
      {
         S service = null;
         ServiceEnricher origin = null;

         if (!NonEnriching.class.isAssignableFrom(serviceClass))
         {
            if (enricherLoader == null)
            {
               enricherLoader = java.util.ServiceLoader
                        .load(ServiceEnricher.class);
               ServiceLogger.logLoadedServices(log, ServiceEnricher.class, Iterators.asList(enricherLoader.iterator()));
            }

            for (ServiceEnricher enricher : enricherLoader)
            {
               service = enricher.produce(serviceClass);
               if (service != null)
               {
                  origin = enricher;
                  break;
               }
            }
         }

         if (service == null)
         {
            Constructor<? extends S> constructor = serviceClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            service = constructor.newInstance();
         }

         if (!NonEnriching.class.isAssignableFrom(serviceClass))
         {
            for (ServiceEnricher enricher : enricherLoader)
            {
               if (!enricher.equals(origin))
               {
                  service = enricher.enrich(service);
               }
            }
         }

         return service;
      }
      catch (NoClassDefFoundError e)
      {
         log.warn("Could not instantiate service class " + serviceClass.getName(), e);
         return null;
      }
      catch (InvocationTargetException e)
      {
         throw new RuntimeException("Error instantiating " + serviceClass, e.getCause());
      }
      catch (IllegalArgumentException e)
      {
         throw new RuntimeException("Error instantiating " + serviceClass, e);
      }
      catch (InstantiationException e)
      {
         throw new RuntimeException("Error instantiating " + serviceClass, e);
      }
      catch (IllegalAccessException e)
      {
         throw new RuntimeException("Error instantiating " + serviceClass, e);
      }
      catch (SecurityException e)
      {
         throw new RuntimeException("Error instantiating " + serviceClass, e);
      }
      catch (NoSuchMethodException e)
      {
         throw new RuntimeException("Error instantiating " + serviceClass, e);
      }
   }

   /**
    * Lazily loads the available providers of this loader's service.
    * 
    * The iterator returned by this method first yields all of the elements of the provider cache, in instantiation
    * order. It then lazily loads and instantiates any remaining providers, adding each one to the cache in turn.
    * 
    * To achieve laziness the actual work of parsing the available provider-configuration files and instantiating
    * providers must be done by the iterator itself. Its hasNext and next methods can therefore throw a
    * ServiceConfigurationError if a provider-configuration file violates the specified format, or if it names a
    * provider class that cannot be found and instantiated, or if the result of instantiating the class is not
    * assignable to the service type, or if any other kind of exception or error is thrown as the next provider is
    * located and instantiated. To write robust code it is only necessary to catch ServiceConfigurationError when using
    * a service iterator.
    * 
    * If such an error is thrown then subsequent invocations of the iterator will make a best effort to locate and
    * instantiate the next available provider, but in general such recovery cannot be guaranteed.
    * 
    * Design Note Throwing an error in these cases may seem extreme. The rationale for this behavior is that a malformed
    * provider-configuration file, like a malformed class file, indicates a serious problem with the way the Java
    * virtual machine is configured or is being used. As such it is preferable to throw an error rather than try to
    * recover or, even worse, fail silently.
    * 
    * The iterator returned by this method does not support removal. Invoking its remove method will cause an
    * UnsupportedOperationException to be thrown.
    * 
    * @return An iterator that lazily loads providers for this loader's service
    */
   @Override
   public Iterator<S> iterator()
   {
      if (providers == null)
      {
         reload();
      }
      return providers.iterator();
   }

   /**
    * Returns a string describing this service.
    * 
    * @return A descriptive string
    */
   @Override
   public String toString()
   {
      return "Services for " + serviceFile;
   }
}