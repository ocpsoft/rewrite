package org.ocpsoft.rewrite.config.typesafe;

public class ReflectionInstanceFactory implements InstanceFactory
{

   @Override
   public int priority()
   {
      return 0;
   }

   @Override
   public Object getInstance(Class<?> type)
   {
      Object result = null;
      try {
         result = type.newInstance();
      }
      catch (Exception e) {}
      return result;
   }

}
