package org.ocpsoft.rewrite.faces;

import jakarta.faces.context.FacesContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;

import org.ocpsoft.rewrite.faces.spi.FacesActionUrlProvider;

public class DefaultActionUrlProvider implements FacesActionUrlProvider
{

   @Override
   public String getActionURL(FacesContext context, String viewId)
   {
      HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

      DispatcherType type = request.getDispatcherType();

      if (type == null || type != DispatcherType.ERROR) {
         return FacesRewriteLifecycleListener.getOriginalRequestURL(request);
      }

      return null;
   }

   @Override
   public int priority()
   {
      return 0;
   }
}
