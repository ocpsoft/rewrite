package org.ocpsoft.rewrite.param;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 
 * A {@link Parameterized} regular expression {@link Pattern}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface ParameterizedPattern<IMPLTYPE extends ParameterizedPattern<IMPLTYPE, PARAMTYPE>, PARAMTYPE extends Parameter<PARAMTYPE, String>>
         extends Parameterized<IMPLTYPE, PARAMTYPE, String>
{
   /**
    * Get the {@link ParameterizedPatternParameter} with the given name. Return null if no such parameter exists in this
    * {@link ParameterizedPattern}.
    */
   PARAMTYPE getParameter(String string);

   /**
    * Get an unmodifiable map of all {@link Parameter} instances detected during expression parsing.
    */
   Map<String, PARAMTYPE> getParameterMap();

   /**
    * Get a {@link List} of all defined {@link Parameter} names.
    */
   List<String> getParameterNames();

   /**
    * Get the pattern for which this {@link ParameterizedPattern} represents.
    */
   String getPattern();
}
