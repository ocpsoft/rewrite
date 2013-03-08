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
    * Submit the given {@link Parameter} and value. Return false if the value does not match configured
    * {@link Constraint} instances, or does not match an already submitted value.
    */
   boolean submit(Rewrite event, EvaluationContext context, Parameter<?> param, String value);

   /**
    * Retrieve the value for the given {@link Parameter}
    */
   String retrieve(Parameter<?> parameter);
}
