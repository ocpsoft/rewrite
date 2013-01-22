package org.ocpsoft.rewrite.param;

/**
 * {@link Parameter} for {@link ParameterizedPattern}.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ParameterizedPatternParameter extends ParameterBuilder<ParameterizedPatternParameter, String>
         implements RegexParameter<ParameterizedPatternParameter>
{
   private String pattern;
   private final String name;

   public ParameterizedPatternParameter(String name)
   {
      this.name = name;
   }

   /**
    * Get the pattern to which this {@link ParameterizedPatternParameter} must mach.
    */
   @Override
   public String getPattern()
   {
      return pattern;
   }

   /**
    * Set the pattern to which this {@link ParameterizedPatternParameter} must match.
    */
   @Override
   public ParameterizedPatternParameter matches(String pattern)
   {
      this.pattern = pattern;
      return this;
   }

   @Override
   public String getName()
   {
      return name;
   }

   @Override
   public String toString()
   {
      return "ParameterizedPatternParameter [name=" + name + ", pattern=" + pattern + "]";
   }
}
