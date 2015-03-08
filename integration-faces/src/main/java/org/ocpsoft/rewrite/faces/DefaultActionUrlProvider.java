package org.ocpsoft.rewrite.faces;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.rewrite.faces.spi.FacesActionUrlProvider;
import org.ocpsoft.rewrite.servlet.DispatcherType;
import org.ocpsoft.rewrite.servlet.spi.DispatcherTypeProvider;

public class DefaultActionUrlProvider implements FacesActionUrlProvider
{
   @SuppressWarnings("unchecked")
   private final Iterable<DispatcherTypeProvider> providers = ServiceLoader.load(DispatcherTypeProvider.class);

   @Override
   public String getActionURL(FacesContext context, String viewId)
   {
      HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

      for (DispatcherTypeProvider provider : providers)
      {
         DispatcherType type = provider.getDispatcherType(request, (ServletContext) FacesContext
                  .getCurrentInstance().getExternalContext().getContext());

         if (type == null || type != DispatcherType.ERROR) {
            return FacesRewriteLifecycleListener.getOriginalRequestURL(request);
         }
      }
      return null;
   }

   @Override
   public int priority()
   {
      return 0;
   }
}
