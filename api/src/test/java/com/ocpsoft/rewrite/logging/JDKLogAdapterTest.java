package com.ocpsoft.rewrite.logging;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JDKLogAdapterTest
{

   private TestHandler handler;
   private JDKLogAdapter log;
   private Logger jdkLogger;

   @Before
   public void setUp()
   {

      // create the log adapter
      String name = JDKLogAdapterTest.class.getName() + ".test1";
      log = new JDKLogAdapter(name);

      // register the DebugHandler
      handler = new TestHandler();
      jdkLogger = Logger.getLogger(name);
      jdkLogger.addHandler(handler);

   }

   @After
   public void tearDown()
   {
      jdkLogger.removeHandler(handler);
      jdkLogger = null;
      log = null;
      handler = null;
   }

   @Test
   public void testErrorLogMessage()
   {

      log.error("my error message!");

      // correct log message
      assertNotNull(handler.getLogRecord());
      assertEquals(Level.SEVERE, handler.getLogRecord().getLevel());
      assertEquals("my error message!", handler.getLogRecord().getMessage());

      // no exception
      assertNull(handler.getLogRecord().getThrown());

   }

   @Test
   public void testErrorLogMessageWithException()
   {

      log.error("my error message with exception!", new IllegalStateException("exception"));

      // correct log message
      assertNotNull(handler.getLogRecord());
      assertEquals(Level.SEVERE, handler.getLogRecord().getLevel());
      assertEquals("my error message with exception!", handler.getLogRecord().getMessage());

      // attached exception
      assertNotNull(handler.getLogRecord().getThrown());
      assertEquals(IllegalStateException.class, handler.getLogRecord().getThrown().getClass());
      assertEquals("exception", handler.getLogRecord().getThrown().getMessage());

   }

   private final class TestHandler extends Handler
   {
      private LogRecord logRecord;

      public void publish(LogRecord logRecord)
      {
         this.logRecord = logRecord;
      }

      public void flush()
      {
         // NOP
      }

      public void close() throws SecurityException
      {
         // NOP
      }

      public LogRecord getLogRecord()
      {
         return logRecord;
      }
   }

}
