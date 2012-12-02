package org.ocpsoft.rewrite.param;

import java.util.Map;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * {@link Parameter} for {@link ParameterizedPattern}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ParameterizedPatternParserParameter extends ParameterizedPatternParameter<ParameterizedPatternParserParameter, ParameterizedPatternParser>
         implements ParameterizedPatternParser
{
   public ParameterizedPatternParserParameter(ParameterizedPatternParser parent, String name)
   {
      super(parent, name);
   }

   @Override
   public Map<ParameterizedPatternParserParameter, String[]> parse(String value)
   {
      return parent.parse(value);
   }

   @Override
   public Map<ParameterizedPatternParserParameter, String[]> parse(Rewrite rewrite, EvaluationContext context,
            String value)
   {
      return parent.parse(rewrite, context, value);
   }

   @Override
   public String toString()
   {
      return "ParameterizedPatternBuilderParameter [name=" + getName() + ", pattern=" + getPattern() + "]";
   }

   @Override
   public ParameterizedPatternBuilder getBuilder()
   {
      return parent.getBuilder();
   }

   @Override
   public boolean matches(Rewrite rewrite, EvaluationContext context, String value)
   {
      return parent.matches(rewrite, context, value);
   }

}
