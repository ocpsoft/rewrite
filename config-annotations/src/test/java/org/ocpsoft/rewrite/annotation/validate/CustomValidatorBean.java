package org.ocpsoft.rewrite.annotation.validate;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.Validate;

@Named
@RequestScoped
@Join(path = "/validate/{value}/", to = "/validate.jsp")
public class CustomValidatorBean
{

   @Parameter
   @Validate(with = EvenLengthValidator.class)
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
