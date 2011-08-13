package com.ocpsoft.rewrite.logging;

import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

import com.ocpsoft.rewrite.pattern.WeightedComparator;
import com.ocpsoft.rewrite.spi.LogAdapterFactory;
import com.ocpsoft.rewrite.util.Iterators;

/**
 * Class to create log messages.
 * 
 * @author Christian Kaltepoth <christian@kaltepoth.de>
 */
public abstract class Logger
{

   public enum Level
   {
      TRACE, DEBUG, INFO, WARN, ERROR
   }

   protected abstract void log(Level level, String msg, Throwable t);

   protected abstract boolean isEnabled(Level level);

   public boolean isTraceEnabled()
   {
      return isEnabled(Level.TRACE);
   }

   public void trace(final String msg)
   {
      log(Level.TRACE, msg, null);
   }

   public void trace(final String msg, final Object arg)
   {
      log(Level.TRACE, format(msg, new Object[] { arg }), null);
   }

   public void trace(final String msg, final Object arg1, final Object arg2)
   {
      log(Level.TRACE, format(msg, new Object[] { arg1, arg2 }), null);
   }

   public void trace(final String msg, final Object[] argArray)
   {
      log(Level.TRACE, format(msg, argArray), null);
   }

   public void trace(final String msg, final Throwable t)
   {
      log(Level.TRACE, msg, t);
   }

   public boolean isDebugEnabled()
   {
      return isEnabled(Level.DEBUG);
   }

   public void debug(final String msg)
   {
      log(Level.DEBUG, msg, null);
   }

   public void debug(final String msg, final Object arg)
   {
      log(Level.DEBUG, format(msg, new Object[] { arg }), null);
   }

   public void debug(final String msg, final Object arg1, final Object arg2)
   {
      log(Level.DEBUG, format(msg, new Object[] { arg1, arg2 }), null);
   }

   public void debug(final String msg, final Object[] argArray)
   {
      log(Level.DEBUG, format(msg, argArray), null);
   }

   public void debug(final String msg, final Throwable t)
   {
      log(Level.DEBUG, msg, t);
   }

   public boolean isInfoEnabled()
   {
      return isEnabled(Level.INFO);
   }

   public void info(final String msg)
   {
      log(Level.INFO, msg, null);
   }

   public void info(final String msg, final Object arg)
   {
      log(Level.INFO, format(msg, new Object[] { arg }), null);
   }

   public void info(final String msg, final Object arg1, final Object arg2)
   {
      log(Level.INFO, format(msg, new Object[] { arg1, arg2 }), null);
   }

   public void info(final String msg, final Object[] argArray)
   {
      log(Level.INFO, format(msg, argArray), null);
   }

   public void info(final String msg, final Throwable t)
   {
      log(Level.INFO, msg, t);
   }

   public boolean isWarnEnabled()
   {
      return isEnabled(Level.WARN);
   }

   public void warn(final String msg)
   {
      log(Level.WARN, msg, null);
   }

   public void warn(final String msg, final Object arg)
   {
      log(Level.WARN, format(msg, new Object[] { arg }), null);
   }

   public void warn(final String msg, final Object arg1, final Object arg2)
   {
      log(Level.WARN, format(msg, new Object[] { arg1, arg2 }), null);
   }

   public void warn(final String msg, final Object[] argArray)
   {
      log(Level.WARN, format(msg, argArray), null);
   }

   public void warn(final String msg, final Throwable t)
   {
      log(Level.WARN, msg, t);
   }

   public boolean isErrorEnabled()
   {
      return isEnabled(Level.ERROR);
   }

   public void error(final String msg)
   {
      log(Level.ERROR, msg, null);
   }

   public void error(final String msg, final Object arg)
   {
      log(Level.ERROR, format(msg, new Object[] { arg }), null);
   }

   public void error(final String msg, final Object arg1, final Object arg2)
   {
      log(Level.ERROR, format(msg, new Object[] { arg1, arg2 }), null);
   }

   public void error(final String msg, final Object[] argArray)
   {
      log(Level.ERROR, format(msg, argArray), null);
   }

   public void error(final String msg, final Throwable t)
   {
      log(Level.ERROR, msg, t);
   }

   protected String format(final String msg, final Object[] args)
   {

      StringBuilder builder = new StringBuilder(msg);
      for (Object o : args)
      {
         int i = builder.indexOf("{}");
         if (i == -1)
         {
            break;
         }
         builder.replace(i, i + 2, (o != null ? o.toString() : "null"));

      }
      return builder.toString();
   }

   /**
    * The LogAdapterFactory to use. Only {@link #getAdapterFactory()} should use this field. It is declared as volatile
    * so that the double-checked locking implemented in {@link #getAdapterFactory()} works correctly.
    */
   private static volatile LogAdapterFactory _adapterFactory = null;

   /**
    * Create a {@link Logger} instance for a specific class
    * 
    * @param clazz The class to create the log for
    * @return The {@link Logger} instance
    */
   public static Logger getLogger(final Class<?> clazz)
   {
      return getLogger(clazz.getName());
   }

   /**
    * Create a {@link Logger} instance for a specific logger name
    * 
    * @param logger the logger name
    * @return The {@link Logger} instance
    */
   public static Logger getLogger(final String logger)
   {
      LogAdapterFactory adapterFactory = getAdapterFactory();
      return adapterFactory.createLogAdapter(logger);
   }

   /**
    * This method provides access to the {@link LogAdapterFactory}. It will obtain the {@link LogAdapterFactory} lazily
    * using {@link #createAdapterFactory()}.
    */
   private static LogAdapterFactory getAdapterFactory()
   {
      // double-checked locking
      if (_adapterFactory == null)
      {
         synchronized (Logger.class)
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
    * Called one by {@link #getAdapterFactory()} to obtain the {@link LogAdapterFactory} with the highest priority.
    */
   private static LogAdapterFactory createAdapterFactory()
   {
      List<LogAdapterFactory> factories = Iterators.asUniqueList(ServiceLoader.load(LogAdapterFactory.class));
      if (factories.isEmpty())
      {
         throw new IllegalStateException("Log logging implementations found!");
      }

      /*
       * Sort factories by priority.
       */
      Collections.sort(factories, new WeightedComparator());

      /*
       * Use the factory with the highest priority.
       */
      return factories.get(0);

   }

}
