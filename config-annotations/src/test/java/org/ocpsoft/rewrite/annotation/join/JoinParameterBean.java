package org.ocpsoft.rewrite.annotation.join;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;

@Named
@RequestScoped
@Join(path = "/join/{value}/", to = "/join-parameter.jsp")
public class JoinParameterBean
{

   private String value;

   public String getValue()
   {
      return value;
   }

   public void setValue(String value)
   {
      this.value = value;
   }

}
