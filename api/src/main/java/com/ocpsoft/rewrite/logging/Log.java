package com.ocpsoft.rewrite.logging;

/**
 * Class to create log messages.
 * 
 * @author Christian Kaltepoth <christian@kaltepoth.de>
 */
public abstract class Log
{

   public enum Level {
      TRACE, DEBUG, INFO, WARN, ERROR
   }

   protected abstract void log(Level level, String msg, Throwable t);

   protected abstract boolean isEnabled(Level level);

   public boolean isTraceEnabled()
   {
      return isEnabled(Level.TRACE);
   }

   public void trace(String msg)
   {
      log(Level.TRACE, msg, null);
   }

   public void trace(String msg, Object arg)
   {
      log(Level.TRACE, format(msg, new Object[] { arg }), null);
   }

   public void trace(String msg, Object arg1, Object arg2)
   {
      log(Level.TRACE, format(msg, new Object[] { arg1, arg2 }), null);
   }

   public void trace(String msg, Object[] argArray)
   {
      log(Level.TRACE, format(msg, argArray), null);
   }

   public void trace(String msg, Throwable t)
   {
      log(Level.TRACE, msg, t);
   }

   public boolean isDebugEnabled()
   {
      return isEnabled(Level.DEBUG);
   }

   public void debug(String msg)
   {
      log(Level.DEBUG, msg, null);
   }

   public void debug(String msg, Object arg)
   {
      log(Level.DEBUG, format(msg, new Object[] { arg }), null);
   }

   public void debug(String msg, Object arg1, Object arg2)
   {
      log(Level.DEBUG, format(msg, new Object[] { arg1, arg2 }), null);
   }

   public void debug(String msg, Object[] argArray)
   {
      log(Level.DEBUG, format(msg, argArray), null);
   }

   public void debug(String msg, Throwable t)
   {
      log(Level.DEBUG, msg, t);
   }

   public boolean isInfoEnabled()
   {
      return isEnabled(Level.INFO);
   }

   public void info(String msg)
   {
      log(Level.INFO, msg, null);
   }

   public void info(String msg, Object arg)
   {
      log(Level.INFO, format(msg, new Object[] { arg }), null);
   }

   public void info(String msg, Object arg1, Object arg2)
   {
      log(Level.INFO, format(msg, new Object[] { arg1, arg2 }), null);
   }

   public void info(String msg, Object[] argArray)
   {
      log(Level.INFO, format(msg, argArray), null);
   }

   public void info(String msg, Throwable t)
   {
      log(Level.INFO, msg, t);
   }

   public boolean isWarnEnabled()
   {
      return isEnabled(Level.WARN);
   }

   public void warn(String msg)
   {
      log(Level.WARN, msg, null);
   }

   public void warn(String msg, Object arg)
   {
      log(Level.WARN, format(msg, new Object[] { arg }), null);
   }

   public void warn(String msg, Object arg1, Object arg2)
   {
      log(Level.WARN, format(msg, new Object[] { arg1, arg2 }), null);
   }

   public void warn(String msg, Object[] argArray)
   {
      log(Level.WARN, format(msg, argArray), null);
   }

   public void warn(String msg, Throwable t)
   {
      log(Level.WARN, msg, t);
   }

   public boolean isErrorEnabled()
   {
      return isEnabled(Level.ERROR);
   }

   public void error(String msg)
   {
      log(Level.ERROR, msg, null);
   }

   public void error(String msg, Object arg)
   {
      log(Level.ERROR, format(msg, new Object[] { arg }), null);
   }

   public void error(String msg, Object arg1, Object arg2)
   {
      log(Level.ERROR, format(msg, new Object[] { arg1, arg2 }), null);
   }

   public void error(String msg, Object[] argArray)
   {
      log(Level.ERROR, format(msg, argArray), null);
   }

   public void error(String msg, Throwable t)
   {
      log(Level.ERROR, msg, t);
   }

   protected String format(String msg, Object[] args)
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

}
