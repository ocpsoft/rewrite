package org.ocpsoft.rewrite.param;

/**
 * An object which can be constrained.
 * 
 * @param <IMPLTYPE> The type implementing {@link Constrainable}.
 * @param <VALUETYPE> The type of the value to be constrained.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface Constrainable<IMPLTYPE, VALUETYPE>
{
   /**
    * Add a constraint to which this object {@link IMPLTYPE} must match.
    */
   public IMPLTYPE constrainedBy(Constraint<VALUETYPE> pattern);

}
