package org.ocpsoft.rewrite.annotation.convert;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.Converter;

public class LowercaseConverter implements Converter<String>
{

   @Override
   public String convert(Rewrite event, EvaluationContext context, Object value)
   {
      return value.toString().toLowerCase();
   }

}
