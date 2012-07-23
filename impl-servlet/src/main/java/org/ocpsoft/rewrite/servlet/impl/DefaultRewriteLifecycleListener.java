package org.ocpsoft.rewrite.servlet.impl;

import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.servlet.spi.RewriteLifecycleListener;

public class DefaultRewriteLifecycleListener implements RewriteLifecycleListener<HttpServletRewrite>
{

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
   public void afterInboundLifecycle(HttpServletRewrite event)
   {
      HttpRewriteWrappedResponse.getInstance(event.getRequest()).flushBufferedStreams();
   }

   @Override
   public void beforeInboundLifecycle(HttpServletRewrite event)
   {}

   @Override
   public void beforeInboundRewrite(HttpServletRewrite event)
   {}

   @Override
   public void afterInboundRewrite(HttpServletRewrite event)
   {}

   @Override
   public void beforeOutboundRewrite(HttpServletRewrite event)
   {}

   @Override
   public void afterOutboundRewrite(HttpServletRewrite event)
   {}

}
