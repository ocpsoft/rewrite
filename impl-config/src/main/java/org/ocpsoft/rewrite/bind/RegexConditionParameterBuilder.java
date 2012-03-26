package org.ocpsoft.rewrite.bind;

import org.ocpsoft.rewrite.param.ConditionParameterBuilder;
import org.ocpsoft.rewrite.param.ParameterizedCondition;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class RegexConditionParameterBuilder extends ConditionParameterBuilder<RegexConditionParameterBuilder, String>
{
   private final RegexParameter parameter;

   public RegexConditionParameterBuilder(
            ParameterizedCondition<ConditionParameterBuilder<RegexConditionParameterBuilder, String>, String> parent,
            RegexParameter parameter)
   {
      super(parent, parameter);
      this.parameter = parameter;
   }

   public RegexConditionParameterBuilder matches(String pattern)
   {
      parameter.matches(pattern);
      return this;
   }

}
