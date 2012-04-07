package org.ocpsoft.rewrite.util;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TimerTest
{

   @Test
   public void testStart() throws InterruptedException
   {
      Timer timer = Timer.getTimer();
      assertEquals(0, timer.getElapsedMilliseconds());
      assertEquals(0, timer.getLapMilliseconds());
      timer.start();
      Thread.sleep(1);
      assertTrue(timer.getElapsedMilliseconds() > 0);
   }

   @Test(expected = IllegalStateException.class)
   public void testStartThrowsExceptionIfNotReset() throws InterruptedException
   {
      Timer timer = Timer.getTimer();
      timer.start();
      timer.start();
   }

   @Test
   public void testStartSucceedsAfterReset() throws InterruptedException
   {
      Timer timer = Timer.getTimer();
      timer.start();
      timer.reset();
      timer.start();
   }

   @Test
   public void testLap() throws InterruptedException
   {
      Timer timer = Timer.getTimer();
      timer.start();
      Thread.sleep(1);
      timer.lap();
      Thread.sleep(1);
      assertTrue(timer.getLapMilliseconds() > 0);
      assertTrue(timer.getElapsedMilliseconds() > timer.getLapMilliseconds());
   }

   @Test(expected = IllegalStateException.class)
   public void testLapThrowsExceptionIfTimerNotStarted()
   {
      Timer timer = Timer.getTimer();
      timer.lap();
   }

   @Test
   public void testReset() throws InterruptedException
   {
      Timer timer = Timer.getTimer();
      timer.start();
      timer.lap();
      Thread.sleep(1);
      timer.reset();
      assertEquals(0, timer.getElapsedMilliseconds());
      assertEquals(0, timer.getLapMilliseconds());
   }

   @Test
   public void testGetElapsedMilliseconds() throws InterruptedException
   {
      Timer timer = Timer.getTimer();
      assertEquals(0, timer.getElapsedMilliseconds());
      timer.start();
      timer.lap();
      Thread.sleep(1);
      assertTrue(timer.getLapMilliseconds() > 0);
   }

   @Test
   public void testGetLapMilliseconds() throws InterruptedException
   {
      Timer timer = Timer.getTimer();
      assertEquals(0, timer.getElapsedMilliseconds());
      timer.start();
      timer.lap();
      Thread.sleep(1);
      assertTrue(timer.getElapsedMilliseconds() > 0);
   }

}