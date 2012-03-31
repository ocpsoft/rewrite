package org.ocpsoft.rewrite.config;

/**
 * Defines a contract where a given object can be relocated to a new position in a data-structure.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface Relocatable
{
   /**
    * Returns true if this object should be relocated, or has been relocated.
    */
   public boolean isRelocated();
}
