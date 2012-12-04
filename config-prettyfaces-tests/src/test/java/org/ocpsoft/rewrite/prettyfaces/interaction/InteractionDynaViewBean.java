package org.ocpsoft.rewrite.prettyfaces.interaction;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

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
