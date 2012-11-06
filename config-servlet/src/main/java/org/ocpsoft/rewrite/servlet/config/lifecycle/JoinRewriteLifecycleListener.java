package org.ocpsoft.rewrite.servlet.config.lifecycle;

import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.config.rule.Join;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.servlet.spi.RewriteLifecycleListener;

public class JoinRewriteLifecycleListener implements RewriteLifecycleListener<HttpServletRewrite>
{
   private static final String JOIN_DISABLED_KEY = Join.class.getName() + "_DISABLED";
   private static final String JOIN_DISABLED_RESET_NEXT_KEY = Join.class.getName() + "_DISABLED_RESET_NEXT";

   @Override
   public boolean handles(Rewrite payload)
   {
      return payload instanceof HttpServletRewrite;
   }

   @Override
   public int priority()
   {
      return Integer.MAX_VALUE;
   }

   @Override
   public void beforeInboundRewrite(HttpServletRewrite event)
   {}

   @Override
   public void afterInboundLifecycle(HttpServletRewrite event)
   {}

   @Override
   public void beforeInboundLifecycle(HttpServletRewrite event)
   {}

   @Override
   public void afterInboundRewrite(HttpServletRewrite event)
   {
      if (Boolean.TRUE.equals(event.getRewriteContext().get(JOIN_DISABLED_RESET_NEXT_KEY)))
      {
         event.getRewriteContext().put(JOIN_DISABLED_KEY, false);
         event.getRewriteContext().put(JOIN_DISABLED_RESET_NEXT_KEY, false);
      }
      else if (Boolean.TRUE.equals(event.getRewriteContext().get(JOIN_DISABLED_KEY)))
      {
         event.getRewriteContext().put(JOIN_DISABLED_RESET_NEXT_KEY, true);
      }
   }

   @Override
   public void beforeOutboundRewrite(HttpServletRewrite event)
   {}

   @Override
   public void afterOutboundRewrite(HttpServletRewrite event)
   {}

}
