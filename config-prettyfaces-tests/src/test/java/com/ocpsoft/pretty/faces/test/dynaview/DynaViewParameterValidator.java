package com.ocpsoft.pretty.faces.test.dynaview;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator(value = "DynaViewParameterValidator")
public class DynaViewParameterValidator implements Validator
{

   @Override
   public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException
   {
      if (value != null && value.toString().equals("invalid")) {
         throw new ValidatorException(new FacesMessage("Invalid"));
      }
   }

}
