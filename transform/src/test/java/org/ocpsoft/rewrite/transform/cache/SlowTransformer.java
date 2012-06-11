package org.ocpsoft.rewrite.transform.cache;

import org.ocpsoft.rewrite.transform.StringTransformer;

public class SlowTransformer extends StringTransformer
{

   public static final String RESULT = "Sorry, I'm soooo slow.";

   @Override
   public String transform(String input)
   {
      try {
         Thread.sleep(1000);
         return RESULT;
      }
      catch (InterruptedException e) {
         throw new IllegalArgumentException("I'm slow. So don't interrupt me!");
      }
   }

}
