package org.ocpsoft.rewrite.annotation.convert;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Convert;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;

@Named
@RequestScoped
@Join(path = "/convert/{value}/", to = "/convert.jsp")
public class CustomConverterBean
{

   @Parameter
   @Convert(with = LowercaseConverter.class)
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
