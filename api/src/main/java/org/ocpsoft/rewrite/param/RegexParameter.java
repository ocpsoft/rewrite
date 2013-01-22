package org.ocpsoft.rewrite.param;

/**
 * {@link Parameter} for {@link ParameterizedPattern}.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface RegexParameter<PARAMTYPE extends RegexParameter<PARAMTYPE>> extends Parameter<PARAMTYPE, String>
{
   /**
    * Get the pattern to which this {@link RegexParameter} must match;
    */
   public String getPattern();

   /**
    * Set the pattern to which this {@link RegexParameter} must match.
    */
   public PARAMTYPE matches(String pattern);
}
