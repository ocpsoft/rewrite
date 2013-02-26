package org.ocpsoft.rewrite.faces.annotation.handler;

import org.ocpsoft.rewrite.bind.Converter;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

class DeferredConverter implements Converter<Object>
{
   private Converter<?> deferred;

   public DeferredConverter(Converter<?> validator)
   {
      this.deferred = validator;
   }

   @Override
   public Object convert(Rewrite event, EvaluationContext context, Object value)
   {
      return value;
   }

   public Converter<?> getDeferred()
   {
      return deferred;
   }
}
