package org.ocpsoft.rewrite.servlet.config;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.Converter;

public class DomainConverter implements Converter<DomainConvertedType>
{
   @Override
   public DomainConvertedType convert(Rewrite event, EvaluationContext context, Object value)
   {
      return new DomainConvertedType(value.toString());
   }

}
