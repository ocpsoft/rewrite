package org.ocpsoft.rewrite.param;

import java.util.List;

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
