package org.ocpsoft.rewrite.param;

import org.ocpsoft.rewrite.bind.Converter;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * Used to verify values before they are converted via the {@link Converter} API.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface Constraint<T>
{
   /**
    * Return <code>true</code> if this {@link Constraint} is satisfied by the given value; otherwise, return
    * <code>false</code>.
    */
   boolean isSatisfiedBy(Rewrite event, EvaluationContext context, String value);
}
