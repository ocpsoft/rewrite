package org.ocpsoft.rewrite.annotation.param;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Matches;
import org.ocpsoft.rewrite.annotation.ParameterBinding;

@Named
@RequestScoped
@Join(path = "/param/{value}/", to = "/param.jsp")
public class ParameterMatchesBean
{

   @ParameterBinding
   @Matches("\\w{4}")
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
