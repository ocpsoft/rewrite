package org.ocpsoft.rewrite.param;

import java.util.regex.Pattern;

/**
 *
 * A {@link Parameterized} regular expression {@link Pattern}.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ParameterizedPattern<IMPLTYPE extends ParameterizedPattern<IMPLTYPE>>
         extends Parameterized<ParameterizedPatternParameter, String>
{
   /**
    * Get the pattern for which this {@link ParameterizedPattern} represents.
    */
   String getPattern();
}
