package org.ocpsoft.rewrite.prettyfaces.interaction;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

@Named
@RequestScoped
public class InteractionDynaViewBean
{

   private String value;

   public String viewId()
   {
      return "/" + value + ".jsf";
   }

   public String getValue()
   {
      return value;
   }

   public void setValue(String value)
   {
      this.value = value;
   }

}
