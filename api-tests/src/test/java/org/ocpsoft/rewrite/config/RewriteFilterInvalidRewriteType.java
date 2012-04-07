package org.ocpsoft.rewrite.config;

import org.ocpsoft.rewrite.context.Context;
import org.ocpsoft.rewrite.event.Rewrite;

public class RewriteFilterInvalidRewriteType implements Rewrite
{
   @Override
   public Context getRewriteContext()
   {
      throw new IllegalStateException("Fail if called.");
   }
}
