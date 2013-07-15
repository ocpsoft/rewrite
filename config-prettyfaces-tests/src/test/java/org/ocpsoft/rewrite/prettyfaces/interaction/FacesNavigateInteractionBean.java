package org.ocpsoft.rewrite.prettyfaces.interaction;

import org.ocpsoft.rewrite.faces.navigate.Navigate;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named("facesInteraction")
@RequestScoped
public class FacesNavigateInteractionBean
{

   public Navigate navigate()
   {
      return Navigate.to("/page.jsf");
   }
}
