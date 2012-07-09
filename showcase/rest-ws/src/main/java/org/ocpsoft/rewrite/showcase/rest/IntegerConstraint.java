package org.ocpsoft.rewrite.showcase.rest;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.Constraint;

public final class IntegerConstraint implements Constraint<String>
{
   @Override
   public boolean isSatisfiedBy(Rewrite event, EvaluationContext context, String value)
   {
      try {
         Integer.valueOf(value);
      }
      catch (NumberFormatException e) {
         return false;
      }
      return true;
   }
}