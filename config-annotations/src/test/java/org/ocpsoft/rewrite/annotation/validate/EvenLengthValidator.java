package org.ocpsoft.rewrite.annotation.validate;

import org.ocpsoft.rewrite.bind.Validator;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

public class EvenLengthValidator implements Validator<String>
{

   @Override
   public boolean validate(Rewrite event, EvaluationContext context, String value)
   {
      return value.trim().length() % 2 == 0;
   }

}
