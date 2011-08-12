package com.ocpsoft.rewrite.logging;

import java.util.logging.LogRecord;

/**
 * Implementation of a log adapter that delegates to the JDK 1.4 logging API.
 * 
 * @author Christian Kaltepoth <christian@kaltepoth.de>
 */
public class JDKLogAdapter extends Logger
{

   private final java.util.logging.Logger delegate;

   public JDKLogAdapter(String name)
   {
      delegate = java.util.logging.Logger.getLogger(name);
   }

   @Override
   protected void log(Level level, String msg, Throwable t)
   {
      LogRecord r = new LogRecord(getJdkLogLevel(level), msg);
      r.setThrown(t);
      delegate.log(r);
   }

   @Override
   protected boolean isEnabled(Level level)
   {
      return delegate.isLoggable(getJdkLogLevel(level));
   }

   /**
    * Translates the log level to JDK {@link java.util.logging.Level} class.
    */
   protected final java.util.logging.Level getJdkLogLevel(Level level)
   {
      switch (level)
         {
         case TRACE:
            return java.util.logging.Level.FINER;
         case DEBUG:
            return java.util.logging.Level.FINE;
         case INFO:
            return java.util.logging.Level.INFO;
         case WARN:
            return java.util.logging.Level.WARNING;
         case ERROR:
            return java.util.logging.Level.SEVERE;
         }
      throw new IllegalArgumentException("Unsupported log level: " + level);
   }

}
