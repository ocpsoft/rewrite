package org.ocpsoft.rewrite.config.typesafe;

public class DummyObject
{
   public static boolean invoked = false;
   public static Boolean bool;
   public static DummyObjectPayload payload;
   public static Object number;

   public void doSomething()
   {
      invoked = true;
   }

   public void doSomething(double number)
   {
      invoked = true;
   }

   public void doSomething(long num)
   {
      invoked = true;
      number = num;
   }

   public void doSomething(boolean perform, Integer i)
   {
      invoked = true;
      bool = perform;
      number = i;
   }

   public void doSomething(DummyObjectPayload p)
   {
      invoked = true;
      payload = p;
   }

   public boolean isInvoked()
   {
      return invoked;
   }
}
