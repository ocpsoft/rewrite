package org.ocpsoft.rewrite.param;

import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * Used to submit {@link Parameter} values in {@link String} form. These values will subsequently be passed through
 * {@link Constraint}, {@link Transform} and {@link Binding} processing.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ParameterValueStore
{
   /**
    * Submit the value for the given bindings.
    */
   boolean submit(Rewrite event, EvaluationContext context, Parameter<?> param, String value);
}
