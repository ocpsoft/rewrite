package org.ocpsoft.rewrite.annotation.param;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;

@Named
@RequestScoped
@Join(path = "/param/{value}/", to = "/param.jsp")
public class ParameterTestBean
{

   @Parameter
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
