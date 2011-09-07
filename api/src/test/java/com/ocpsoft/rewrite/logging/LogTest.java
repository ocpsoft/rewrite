package com.ocpsoft.rewrite.logging;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ocpsoft.logging.Logger;

public class LogTest
{

   private StringLog log;

   @Before
   public void setUp()
   {
      log = new StringLog();
   }

   @After
   public void tearDown()
   {
      log = null;
   }

   @Test
   public void testSimpleStringArgument()
   {
      log.info("Hallo {}!", "Christian");
      assertEquals("INFO - Hallo Christian!", log.getLogString());
   }

   @Test
   public void testSimpleBooleanArgument()
   {
      log.info("Result: {}", true);
      assertEquals("INFO - Result: true", log.getLogString());
   }

   @Test
   public void testSimpleLongArgument()
   {
      log.info("Result: {}", 1234567890l);
      assertEquals("INFO - Result: 1234567890", log.getLogString());
   }

   @Test
   public void testNullArgument()
   {
      log.info("Result: {}", (Object) null);
      assertEquals("INFO - Result: null", log.getLogString());
   }

   @Test
   public void testArgumentWithoutPlaceholder()
   {
      log.info("Nothing", 123);
      assertEquals("INFO - Nothing", log.getLogString());
   }

   @Test
   public void testPlaceholderButNoArgument()
   {
      log.info("Result: {}");
      assertEquals("INFO - Result: {}", log.getLogString());
   }

   /**
    * Simple class extending {@link Logger} that stores only the last log message as a string.
    */
   private static class StringLog extends Logger
   {

      private String logString;

      @Override
      protected void log(final Level level, final String msg, final Throwable t)
      {
         logString = level.name() + " - " + msg;
      }

      @Override
      protected boolean isEnabled(final Level level)
      {
         return true;
      }

      public String getLogString()
      {
         return logString;
      }

   }

}
