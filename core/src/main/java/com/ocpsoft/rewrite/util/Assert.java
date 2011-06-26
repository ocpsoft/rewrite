package com.ocpsoft.rewrite.util;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class Assert
{
   public static void notNull(final Object object, final String message) throws IllegalStateException
   {
      if (object == null)
      {
         throw new IllegalStateException(message);
      }
   }
}