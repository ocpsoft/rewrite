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
package com.ocpsoft.rewrite.logging.jboss;

import com.ocpsoft.rewrite.logging.Logger;

/**
 * Log adapter that delegates log events to SLF4J
 * 
 * @author Christian Kaltepoth <christian@kaltepoth.de>
 */
public class JBossLoggingLogAdapter extends Logger
{

   private final org.jboss.logging.Logger delegate;

   public JBossLoggingLogAdapter(final String name)
   {
      delegate = org.jboss.logging.Logger.getLogger(name);
   }

   @Override
   protected void log(final Level level, final String msg, final Throwable t)
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
   protected boolean isEnabled(final Level level)
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
         return true;
      case ERROR:
         return true;
      default:
         throw new IllegalArgumentException("Cannot handle log leve: " + level);
      }
   }

}
