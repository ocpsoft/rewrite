package com.ocpsoft.rewrite.logging.slf4j;

import com.ocpsoft.rewrite.logging.Logger;

/**
 * Log adapter that delegates log events to SLF4J
 * 
 * @author Christian Kaltepoth <christian@kaltepoth.de>
 */
public class SLF4JLogAdapter extends Logger
{

   private final org.slf4j.Logger delegate;

   public SLF4JLogAdapter(String name)
   {
      delegate = org.slf4j.LoggerFactory.getLogger(name);
   }

   @Override
   protected void log(Level level, String msg, Throwable t)
   {
      switch (level)
         {
         case TRACE:
            if (t == null)
            {
               delegate.trace(msg);
            }
            else
            {
               delegate.trace(msg, t);
            }
            break;

         case DEBUG:
            if (t == null)
            {
               delegate.debug(msg);
            }
            else
            {
               delegate.debug(msg, t);
            }
            break;

         case INFO:
            if (t == null)
            {
               delegate.info(msg);
            }
            else
            {
               delegate.info(msg, t);
            }
            break;

         case WARN:
            if (t == null)
            {
               delegate.warn(msg);
            }
            else
            {
               delegate.warn(msg, t);
            }
            break;

         case ERROR:
            if (t == null)
            {
               delegate.error(msg);
            }
            else
            {
               delegate.error(msg, t);
            }
            break;

         default:
            throw new IllegalArgumentException("Cannot handle log leve: " + level);
         }
   }

   @Override
   protected boolean isEnabled(Level level)
   {
      switch (level)
         {
         case TRACE:
            return delegate.isTraceEnabled();
         case DEBUG:
            return delegate.isDebugEnabled();
         case INFO:
            return delegate.isInfoEnabled();
         case WARN:
            return delegate.isWarnEnabled();
         case ERROR:
            return delegate.isErrorEnabled();
         default:
            throw new IllegalArgumentException("Cannot handle log leve: " + level);
         }
   }

}
