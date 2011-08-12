package com.ocpsoft.rewrite.logging.slf4j;

import com.ocpsoft.rewrite.logging.Logger;
import com.ocpsoft.rewrite.spi.LogAdapterFactory;

/**
 * Implementation of {@link LogAdapterFactory} that creates log adapters
 * delegating log events to SLF4J.
 * 
 * @author Christian Kaltepoth <christian@kaltepoth.de>
 */
public class SLF4JLogAdapterFactory implements LogAdapterFactory
{

   @Override
   public int priority()
   {
      return 5;
   }

   @Override
   public Logger createLogAdapter(String name)
   {
      return new SLF4JLogAdapter(name);
   }

}
