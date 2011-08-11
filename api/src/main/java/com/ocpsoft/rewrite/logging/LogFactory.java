package com.ocpsoft.rewrite.logging;

import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

import com.ocpsoft.rewrite.pattern.WeightedComparator;
import com.ocpsoft.rewrite.spi.LogAdapterFactory;
import com.ocpsoft.rewrite.util.Iterators;

/**
 * Utility class to create {@link Log} instances.
 * 
 * @author Christian Kaltepoth <christian@kaltepoth.de>
 */
public class LogFactory
{

   /**
    * The LogAdapterFactory to use. Only {@link #getAdapterFactory()} should use
    * this field. It is declared as volatile so that the double-checked locking
    * implemented in {@link #getAdapterFactory()} works correctly.
    */
   private static volatile LogAdapterFactory _adapterFactory = null;

   /**
    * Create a {@link Log} instance for a specific class
    * 
    * @param clazz
    *           The class to create the log for
    * @return The {@link Log} instance
    */
   public static Log getLog(Class<?> clazz)
   {
      return getLog(clazz.getName());
   }

   /**
    * Create a {@link Log} instance for a specific logger name
    * 
    * @param logger
    *           the logger name
    * @return The {@link Log} instance
    */
   public static Log getLog(String logger)
   {
      LogAdapterFactory adapterFactory = getAdapterFactory();
      return adapterFactory.createLogAdapter(logger);
   }

   /**
    * This method provides access to the {@link LogAdapterFactory}. It will
    * obtain the {@link LogAdapterFactory} lazily using
    * {@link #createAdapterFactory()}.
    */
   private static LogAdapterFactory getAdapterFactory()
   {
      // double-checked locking
      if (_adapterFactory == null)
      {
         synchronized (LogFactory.class)
         {
            if (_adapterFactory == null)
            {
               _adapterFactory = createAdapterFactory();
            }
         }
      }
      return _adapterFactory;
   }

   /**
    * Called one by {@link #getAdapterFactory()} to obtain the
    * {@link LogAdapterFactory} with the highest priority.
    */
   private static LogAdapterFactory createAdapterFactory()
   {

      // use the ServiceLoader to get all LogAdapterFactories
      List<LogAdapterFactory> factories = Iterators.asUniqueList(ServiceLoader.load(LogAdapterFactory.class));
      if (factories.isEmpty())
      {
         throw new IllegalStateException("Log logging implementations found!");
      }

      // sort by priority
      Collections.sort(factories, new WeightedComparator());

      // we will use the factory with the highest priority
      return factories.get(0);

   }

}
