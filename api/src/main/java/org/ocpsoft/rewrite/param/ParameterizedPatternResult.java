package org.ocpsoft.rewrite.param;

import java.util.Map;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * Stores the result of {@link ParameterizedPatternParser#matches(Rewrite, EvaluationContext, String)}. Provides methods
 * for controlling parameter value submission.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ParameterizedPatternResult
{
   /**
    * Returns <code>true</code> if the {@link ParameterizedPattern} matched the input value, <code>false</code> if not.
    */
   boolean matches();

   /**
    * Validate all {@link Parameter} values defined within the original {@link ParameterizedPattern}. Return
    * <code>true</code> if all values matched configured {@link Constraint} instances, and matched an already submitted
    * parameter value; otherwise, return <code>false</code>.
    */
   boolean isValid(Rewrite event, EvaluationContext context);

   /**
    * Submit all {@link Parameter} values to the {@link ParameterValueStore}, only if the values are match a prior value
    * or have not yet had a value set. Returns <code>true</code> if submission was successful, <code>false</code> if
    * not.
    */
   boolean submit(Rewrite event, EvaluationContext context);

   /**
    * Get the {@link Map} of {@link Parameter} instances that were referenced in the {@link ParameterizedPattern}.
    */
   Map<Parameter<?>, String> getParameters(EvaluationContext context);
}
