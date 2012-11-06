package com.ocpsoft.pretty.faces.test.interaction;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean
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
