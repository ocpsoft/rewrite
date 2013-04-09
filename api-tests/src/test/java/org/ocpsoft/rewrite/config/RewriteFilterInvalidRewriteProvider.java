package org.ocpsoft.rewrite.config;

import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.spi.RewriteProvider;

public class RewriteFilterInvalidRewriteProvider implements RewriteProvider<Integer, RewriteFilterInvalidRewriteType>
{
   @Override
   public int priority()
   {
      return 0;
   }

   @Override
   public boolean handles(Rewrite payload)
   {
      return false;
   }

   @Override
   public void rewrite(RewriteFilterInvalidRewriteType event)
   {
      throw new IllegalStateException("Fail if called.");
   }

   @Override
   public void init(Integer context)
   {
      throw new IllegalStateException("Fail if called.");
   }

   @Override
   public void shutdown(Integer context)
   {
      throw new IllegalStateException("Fail if called.");
   }

}
