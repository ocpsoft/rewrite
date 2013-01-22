package org.ocpsoft.rewrite.param;

import java.util.Map;
import java.util.regex.Pattern;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 *
 * A {@link Parameterized} regular expression {@link Pattern}.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public interface ParameterizedPatternParser extends ParameterizedPattern<ParameterizedPatternParser>
{
   /**
    * Return the {@link ParameterizedPatternBuilder} corresponding to the pattern with which this
    * {@link ParameterizedPatternParser} was constructed.
    */
   ParameterizedPatternBuilder getBuilder();

   /**
    * Return true if this expression matches the given {@link String}.
    */
   boolean matches(Rewrite rewrite, EvaluationContext context, String value);

   /**
    * Parses the given string if it matches this expression. Returns a {@link org.ocpsoft.rewrite.param.Parameter}-value
    * map of parsed values. This method does not apply any {@link Transform} instances that may be registered.
    */
   Map<ParameterizedPatternParameter, String[]> parse(String value);

   /**
    * Parses the given string if it matches this expression. Returns a {@link org.ocpsoft.rewrite.param.Parameter}-value
    * map of parsed values.
    */
   Map<ParameterizedPatternParameter, String[]> parse(Rewrite rewrite, EvaluationContext context, String value);
}
