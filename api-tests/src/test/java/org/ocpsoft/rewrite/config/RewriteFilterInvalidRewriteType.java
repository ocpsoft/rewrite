package org.ocpsoft.rewrite.config;

import org.ocpsoft.rewrite.AbstractRewrite;
import org.ocpsoft.rewrite.context.Context;
import org.ocpsoft.rewrite.event.Flow;
import org.ocpsoft.rewrite.event.Rewrite;

public class RewriteFilterInvalidRewriteType extends AbstractRewrite implements Rewrite
{
   @Override
   public Context getRewriteContext()
   {
      throw new IllegalStateException("Fail if called.");
   }

   @Override
   public Flow getFlow()
   {
      throw new IllegalStateException("Fail if called.");
   }
}
