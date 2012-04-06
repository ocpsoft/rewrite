package org.ocpsoft.rewrite.bind;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * An object that can be converted.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface Converts
{
   /**
    * Convert the given value into the expected type.
    */
   Object convert(Rewrite event, EvaluationContext context, Object value);
}
