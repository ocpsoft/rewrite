package org.ocpsoft.rewrite.param;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.ocpsoft.rewrite.bind.Bindable;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * 
 * A {@link Parameterized} regular expression {@link Pattern}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface ParameterizedPattern extends Parameterized<ParameterizedPattern, PatternParameter, String>
{
   /**
    * Use this expression to build a {@link String} from the given pattern. Extract needed values from registered
    * {@link Binding} instances.
    */
   String build(Rewrite event, EvaluationContext context, Map<String, ? extends Bindable<?>> parameters);

   /**
    * Use this expression to build a {@link String} from the given pattern and values.
    */
   String buildUnsafe(Map<String, List<Object>> parameters);

   /**
    * Use this expression's pattern to build a {@link String} from the given values. Enforces that the number of values
    * passed must equal the number of expression parameters.
    */
   String buildUnsafe(Object... values);

   /**
    * Get the {@link PatternParameter} with the given name. Return null if no such parameter exists in this
    * {@link ParameterizedPatternImpl}.
    */
   PatternParameter getParameter(String string);

   /**
    * Get an unmodifiable map of all {@link Parameter} instances detected during expression parsing.
    */
   Map<String, PatternParameter> getParameterMap();

   /**
    * Get a {@link List} of all defined {@link Parameter} names.
    */
   List<String> getParameterNames();

   /**
    * Return true if this expression matches the given {@link String}.
    */
   boolean matches(Rewrite rewrite, EvaluationContext context, String value);

   /**
    * Parses the given string if it matches this expression. Returns a {@link org.ocpsoft.rewrite.param.Parameter}-value
    * map of parsed values. This method does not apply any {@link Transform} instances that may be registered.
    */
   Map<PatternParameter, String[]> parse(String value);

   /**
    * Parses the given string if it matches this expression. Returns a {@link org.ocpsoft.rewrite.param.Parameter}-value
    * map of parsed values.
    */
   Map<PatternParameter, String[]> parse(Rewrite rewrite, EvaluationContext context, String value);
}
