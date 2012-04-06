package org.ocpsoft.rewrite.param;

import java.util.List;

/**
 * An object which can be constrained.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @param <C> The type containing the value to be constrained.
 * @param <T> The type of the value to be constrained.
 */
public interface Constrainable<C, T>
{
   /**
    * Add a constraint to which this object {@link C} must match.
    */
   public C constrainedBy(Constraint<T> pattern);

   /**
    * Get the underlying {@link List} of all {@link Constraint} objects currently registered to this {@link C}
    */
   List<Constraint<T>> getConstraints();

}
