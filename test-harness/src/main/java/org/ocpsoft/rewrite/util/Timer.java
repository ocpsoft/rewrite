package org.ocpsoft.rewrite.util;
public class Timer
{
   private Long startTime = null;
   private Long lapTime = null;
   private Long stopTime = null;

   private Timer()
   {}

   public static Timer getTimer()
   {
      return new Timer();
   }

   public Timer start()
   {
      if (this.startTime != null)
      {
         throw new IllegalStateException("Must reset Timer before starting again");
      }
      this.startTime = System.currentTimeMillis();
      this.lapTime = this.startTime;
      return this;
   }

   public Timer stop()
   {
      this.stopTime = System.currentTimeMillis();
      return this;
   }

   public Timer lap()
   {
      if (this.startTime == null)
      {
         throw new IllegalStateException("Timer must be started before lapping.");
      }
      this.lapTime = this.getFinalTime();
      return this;
   }

   public Timer reset()
   {
      this.stopTime = null;
      this.startTime = null;
      this.lapTime = null;
      return this;
   }

   public long getElapsedMilliseconds()
   {
      if (this.startTime != null)
      {
         return this.getFinalTime() - this.startTime;
      }
      return 0;
   }

   public long getLapMilliseconds()
   {
      if (this.lapTime != null)
      {
         return this.getFinalTime() - this.lapTime;
      }
      return 0;
   }

   private long getFinalTime()
   {
      if (this.stopTime != null)
      {
         return this.stopTime;
      }
      return System.currentTimeMillis();
   }
}