package org.ocpsoft.rewrite.annotation.validate;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.Validator;

public class EvenLengthValidator implements Validator<String>
{

   @Override
   public boolean isValid(Rewrite event, EvaluationContext context, String value)
   {
      return value.trim().length() % 2 == 0;
   }

}
