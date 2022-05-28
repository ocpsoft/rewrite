package org.ocpsoft.rewrite.prettyfaces.errorpage;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

@FacesValidator("ErrorPageValidator")
public class ErrorPageValidator implements Validator
{

   @Override
   public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException
   {
      if (value != null && value.toString().equals("invalid")) {
         throw new ValidatorException(new FacesMessage("Invalid value: " + value));
      }
   }

}
