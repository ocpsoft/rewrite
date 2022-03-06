package org.ocpsoft.rewrite.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TimerTest
{

   @Test
   public void testStart() throws InterruptedException
   {
      Timer timer = Timer.getTimer();
      assertThat(timer.getElapsedMilliseconds()).isEqualTo(0);
      assertThat(timer.getLapMilliseconds()).isEqualTo(0);
      timer.start();
      Thread.sleep(1);
      assertThat(timer.getElapsedMilliseconds() > 0).isTrue();
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
      assertThat(timer.getLapMilliseconds() > 0).isTrue();
      assertThat(timer.getElapsedMilliseconds() > timer.getLapMilliseconds()).isTrue();
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
      assertThat(timer.getElapsedMilliseconds()).isEqualTo(0);
      assertThat(timer.getLapMilliseconds()).isEqualTo(0);
   }

   @Test
   public void testGetElapsedMilliseconds() throws InterruptedException
   {
      Timer timer = Timer.getTimer();
      assertThat(timer.getElapsedMilliseconds()).isEqualTo(0);
      timer.start();
      timer.lap();
      Thread.sleep(1);
      assertThat(timer.getLapMilliseconds() > 0).isTrue();
   }

   @Test
   public void testGetLapMilliseconds() throws InterruptedException
   {
      Timer timer = Timer.getTimer();
      assertThat(timer.getElapsedMilliseconds()).isEqualTo(0);
      timer.start();
      timer.lap();
      Thread.sleep(1);
      assertThat(timer.getElapsedMilliseconds() > 0).isTrue();
   }

}