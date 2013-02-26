package org.ocpsoft.rewrite.faces.annotation.handler;

import org.ocpsoft.rewrite.bind.Validator;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

class DeferredValidator implements Validator<Object>
{
   private Validator<?> deferred;

   public DeferredValidator(Validator<?> validator)
   {
      this.deferred = validator;
   }

   @Override
   public boolean validate(Rewrite event, EvaluationContext context, Object value)
   {
      return true;
   }

   public Validator<?> getDeferred()
   {
      return deferred;
   }
}
