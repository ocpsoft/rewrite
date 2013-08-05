package org.ocpsoft.rewrite.faces.annotation.handler;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.Validator;

public class DeferredValidator implements Validator<Object>
{
   private Validator<?> deferred;

   public DeferredValidator(Validator<?> validator)
   {
      this.deferred = validator;
   }

   @Override
   public boolean isValid(Rewrite event, EvaluationContext context, Object value)
   {
      return true;
   }

   public Validator<?> getDeferred()
   {
      return deferred;
   }
}
