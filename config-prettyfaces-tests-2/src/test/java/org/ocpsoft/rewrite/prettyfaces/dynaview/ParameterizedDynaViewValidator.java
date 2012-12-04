package org.ocpsoft.rewrite.prettyfaces.dynaview;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator(value = "ParameterizedDynaViewValidator")
public class ParameterizedDynaViewValidator implements Validator
{

   @Override
   public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException
   {
      if (value != null && value.toString().equals("invalid")) {
         throw new ValidatorException(new FacesMessage("Invalid"));
      }
   }

}
