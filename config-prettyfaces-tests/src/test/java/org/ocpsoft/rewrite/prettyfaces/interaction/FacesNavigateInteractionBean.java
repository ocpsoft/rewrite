package org.ocpsoft.rewrite.prettyfaces.interaction;

import org.ocpsoft.rewrite.faces.navigate.Navigate;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

@Named("facesInteraction")
@RequestScoped
public class FacesNavigateInteractionBean
{

   public Navigate navigate()
   {
      return Navigate.to("/page.jsf");
   }
}
