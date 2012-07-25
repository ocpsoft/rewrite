package org.ocpsoft.rewrite.param;

import java.util.List;
import java.util.Map;

import org.ocpsoft.rewrite.bind.Bindable;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * {@link Parameter} for {@link ParameterizedPattern}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class PatternParameter extends ParameterBuilder<PatternParameter, String> implements ParameterizedPattern
{
   private ParameterizedPattern parent;
   private String pattern;
   private String name;

   public PatternParameter(ParameterizedPattern parent, String name)
   {
      this.parent = parent;
      this.name = name;
   }

   /**
    * Set the pattern to which this {@link PatternParameter} must match.
    */
   public PatternParameter matches(String pattern)
   {
      this.pattern = pattern;
      return this;
   }

   /**
    * Get the pattern to which this {@link PatternParameter} must mach.
    */
   public String getPattern()
   {
      return pattern;
   }

   @Override
   public PatternParameter where(String param)
   {
      return parent.where(param);
   }

   @Override
   public PatternParameter where(String param, Binding binding)
   {
      return parent.where(param, binding);
   }

   @Override
   public String build(Rewrite event, EvaluationContext context, Map<String, ? extends Bindable<?>> parameters)
   {
      return parent.build(event, context, parameters);
   }

   @Override
   public String buildUnsafe(Map<String, List<Object>> parameters)
   {
      return parent.buildUnsafe(parameters);
   }

   @Override
   public String buildUnsafe(Object... values)
   {
      return parent.buildUnsafe(values);
   }

   @Override
   public PatternParameter getParameter(String string)
   {
      return parent.getParameter(string);
   }

   @Override
   public Map<String, PatternParameter> getParameterMap()
   {
      return parent.getParameterMap();
   }

   @Override
   public List<String> getParameterNames()
   {
      return parent.getParameterNames();
   }

   @Override
   public boolean matches(Rewrite rewrite, EvaluationContext context, String value)
   {
      return parent.matches(rewrite, context, value);
   }

   @Override
   public Map<PatternParameter, String[]> parse(Rewrite rewrite, EvaluationContext context, String value)
   {
      return parent.parse(rewrite, context, value);
   }

   @Override
   public String getName()
   {
      return name;
   }

   @Override
   public String toString()
   {
      return "PatternParameter [name=" + name + ", pattern=" + pattern + "]";
   }
   
}
