package org.ocpsoft.rewrite.prettyfaces.errorpage;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

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
