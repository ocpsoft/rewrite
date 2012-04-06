package org.ocpsoft.rewrite.bind;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

public interface Validates
{
   /**
    * Return true if the given value passes all validations.
    */
   boolean validates(Rewrite event, EvaluationContext context, Object value);
}
