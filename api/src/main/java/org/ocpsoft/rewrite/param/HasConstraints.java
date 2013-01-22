package org.ocpsoft.rewrite.param;

import java.util.List;

/**
 * An object which can be constrained.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @param <VALUETYPE> The type of the value to be constrained.
 */
public interface HasConstraints<VALUETYPE>
{
   /**
    * Get the underlying {@link List} of all {@link Constraint} objects currently registered to this instance
    */
   List<Constraint<VALUETYPE>> getConstraints();

}
