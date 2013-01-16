package org.ocpsoft.rewrite.faces;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.common.pattern.WeightedComparator;
import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.common.util.Iterators;
import org.ocpsoft.rewrite.servlet.DispatcherType;
import org.ocpsoft.rewrite.servlet.http.HttpRewriteLifecycleListener;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.servlet.spi.DispatcherTypeProvider;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class FacesRewriteLifecycleListener extends HttpRewriteLifecycleListener
{
   private static final String ORIGINAL_URL = FacesRewriteLifecycleListener.class.getName()
            + "_originalRequestURL";

   private List<DispatcherTypeProvider> dispatcherTypeProviders = null;

   @Override
   public void beforeInboundLifecycle(final HttpServletRewrite event)
   {
      if (DispatcherType.REQUEST.equals(getDispatcherType(event)))
      {
         event.getRequest().setAttribute(ORIGINAL_URL, event.getAddress().getPathAndQuery());
      }
   }

   /**
    * Determines the {@link DispatcherType} of the current request using the {@link DispatcherTypeProvider} SPI.
    */
   private DispatcherType getDispatcherType(final HttpServletRewrite event)
   {
      for (DispatcherTypeProvider provider : getDispatcherTypeProviders()) {
         DispatcherType dispatcherType = provider.getDispatcherType(event.getRequest(), event.getServletContext());
         if (dispatcherType != null) {
            return dispatcherType;
         }
      }
      throw new IllegalStateException("Unable to determine dispatcher type of current request");
   }

   /**
    * Returns the list of {@link DispatcherTypeProvider} implementations.
    */
   private List<DispatcherTypeProvider> getDispatcherTypeProviders()
   {
      if (dispatcherTypeProviders == null) {
         dispatcherTypeProviders = Iterators.asList(
                  ServiceLoader.loadTypesafe(DispatcherTypeProvider.class).iterator());
         Collections.sort(dispatcherTypeProviders, new WeightedComparator());
      }
      return dispatcherTypeProviders;
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
