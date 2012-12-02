package org.ocpsoft.rewrite.param;

import java.util.List;
import java.util.Map;

import org.ocpsoft.rewrite.bind.Bindable;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * {@link Parameter} for {@link ParameterizedPattern}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ParameterizedPatternBuilderParameter extends ParameterizedPatternParameter<ParameterizedPatternBuilderParameter, ParameterizedPatternBuilder>
         implements ParameterizedPatternBuilder
{
   public ParameterizedPatternBuilderParameter(ParameterizedPatternBuilder parent, String name)
   {
      super(parent, name);
   }

   @Override
   public String build(Rewrite event, EvaluationContext context, Map<String, ? extends Bindable<?>> parameters)
   {
      return parent.build(event, context, parameters);
   }

   @Override
   public String build(Map<String, List<String>> parameters)
   {
      return parent.build(parameters);
   }

   @Override
   public String build(Object... values)
   {
      return parent.build(values);
   }

   @Override
   public String toString()
   {
      return "ParameterizedPatternBuilderParameter [name=" + getName() + ", pattern=" + getPattern() + "]";
   }

   @Override
   public ParameterizedPatternParser getParser()
   {
      return parent.getParser();
   }

}
