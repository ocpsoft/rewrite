package org.ocpsoft.rewrite.param;

import org.ocpsoft.rewrite.bind.Binding;

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
   public boolean submit(Parameter<?> param, String value);
}
