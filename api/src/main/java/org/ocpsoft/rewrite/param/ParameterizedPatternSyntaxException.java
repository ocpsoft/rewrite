package org.ocpsoft.rewrite.param;

/**
 * Thrown when a syntax error is found in a {@link ParameterizedPattern}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ParameterizedPatternSyntaxException extends IllegalArgumentException
{
   private static final long serialVersionUID = -7847918960698384107L;
   private final String message;
   private final String pattern;
   private final int index;

   /**
    * Create a new {@link ParameterizedPatternSyntaxException}.
    * 
    * @param message The error message
    * @param pattern The pattern that contains errors
    * @param index The location of the error, or <tt>-1</tt> if location is unknown
    */
   public ParameterizedPatternSyntaxException(String message, String pattern, int index)
   {
      super(message);
      this.message = message;
      this.pattern = pattern;
      this.index = index;
   }

   /**
    * Returns the index at which the error is located in the pattern, or <tt>-1</tt> if the location is not known.
    */
   public int getIndex()
   {
      return index;
   }

   /**
    * Returns the pattern containing errors.
    */
   public String getPattern()
   {
      return pattern;
   }

   /**
    * Returns a string containing the description of the syntax error and its location, the erroneous parameterized
    * pattern string, and a visual location of the error index within the pattern (if possible.)
    */
   @Override
   public String getMessage()
   {
      String nl = System.getProperty("line.separator");
      StringBuffer sb = new StringBuffer();
      sb.append(message);
      if (index >= 0) {
         sb.append(" near index ");
         sb.append(index);
      }
      sb.append(nl);
      sb.append(pattern);
      if (index >= 0) {
         sb.append(nl);
         for (int i = 0; i < index; i++)
            sb.append(' ');
         sb.append('^');
      }
      return sb.toString();
   }
}
