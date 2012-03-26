package org.ocpsoft.rewrite.param;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * Used to perform operations on values (Cannot be used to change the type of the value, for that, see the
 * {@link org.ocpsoft.rewrite.bind.Converter} API.)
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface Transform<S>
{
   /**
    * Perform an operation on the given value.
    */
   S transform(Rewrite event, EvaluationContext context, S value);
}
