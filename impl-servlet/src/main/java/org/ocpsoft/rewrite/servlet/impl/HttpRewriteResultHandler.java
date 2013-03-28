package org.ocpsoft.rewrite.servlet.impl;

import javax.servlet.http.HttpServletResponse;

import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.exception.RewriteException;
import org.ocpsoft.rewrite.servlet.event.BaseRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import org.ocpsoft.rewrite.spi.RewriteResultHandler;

public class HttpRewriteResultHandler implements RewriteResultHandler
{
   private static final Logger log = Logger.getLogger(HttpRewriteResultHandler.class);

   @Override
   public int priority()
   {
      return 0;
   }

   @Override
   public boolean handles(Rewrite payload)
   {
      return payload instanceof HttpInboundServletRewrite;
   }

   @Override
   public void handleResult(Rewrite event)
   {
      try {
         HttpInboundServletRewrite servletRewrite = (HttpInboundServletRewrite) event;
         String dispatchResource = servletRewrite.getDispatchResource();

         if (servletRewrite.getFlow().is(BaseRewrite.ServletRewriteFlow.ABORT_REQUEST))
         {
            if (servletRewrite.getFlow().is(BaseRewrite.ServletRewriteFlow.FORWARD))
            {
               log.debug("Issuing internal FORWARD to [{}].", dispatchResource);
               servletRewrite.getRequest().getRequestDispatcher(dispatchResource)
                        .forward(servletRewrite.getRequest(), servletRewrite.getResponse());
            }
            else if (servletRewrite.getFlow().is(BaseRewrite.ServletRewriteFlow.REDIRECT_PERMANENT))
            {
               log.debug("Issuing 301 permanent REDIRECT to [{}].", dispatchResource);
               HttpServletResponse response = (HttpServletResponse) servletRewrite.getResponse();
               response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
               response.setHeader("Location", dispatchResource);
               response.flushBuffer();
            }
            else if (servletRewrite.getFlow().is(BaseRewrite.ServletRewriteFlow.REDIRECT_TEMPORARY))
            {
               log.debug("Issuing 302 temporary REDIRECT to [{}].", dispatchResource);
               HttpServletResponse response = (HttpServletResponse) servletRewrite.getResponse();
               response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
               response.setHeader("Location", dispatchResource);
               response.flushBuffer();
            }
            else
            {
               log.debug("ABORT requested. Terminating request NOW.");
            }
         }
         else if (servletRewrite.getFlow().is(BaseRewrite.ServletRewriteFlow.INCLUDE))
         {
            log.debug("Issuing internal INCLUDE to [{}].", dispatchResource);
            servletRewrite.getRequest().getRequestDispatcher(dispatchResource)
                     .include(servletRewrite.getRequest(), servletRewrite.getResponse());
         }
      }
      catch (Exception e) {
         throw new RewriteException("Error handling Rewrite result", e);
      }
   }
}
