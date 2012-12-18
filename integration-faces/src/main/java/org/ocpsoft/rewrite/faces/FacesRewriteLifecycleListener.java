package org.ocpsoft.rewrite.faces;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.rewrite.servlet.http.HttpRewriteLifecycleListener;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class FacesRewriteLifecycleListener extends HttpRewriteLifecycleListener
{
   private static final String ORIGINAL_URL = FacesRewriteLifecycleListener.class.getName()
            + "_originalRequestURL";

   @Override
   public void beforeInboundLifecycle(final HttpServletRewrite event)
   {
      if (DispatcherType.REQUEST.equals(event.getRequest().getDispatcherType()))
      {
         event.getRequest().setAttribute(ORIGINAL_URL, event.getAddress().getPathAndQuery());
      }
   }

   public static String getOriginalRequestURL(final HttpServletRequest request)
   {
      return (String) request.getAttribute(ORIGINAL_URL);
   }

   @Override
   public void beforeInboundRewrite(final HttpServletRewrite event)
   {}

   @Override
   public void afterInboundRewrite(final HttpServletRewrite event)
   {}

   @Override
   public void beforeOutboundRewrite(final HttpServletRewrite event)
   {}

   @Override
   public void afterOutboundRewrite(final HttpServletRewrite event)
   {}

   @Override
   public void afterInboundLifecycle(final HttpServletRewrite event)
   {}

   @Override
   public int priority()
   {
      return 0;
   }

}
