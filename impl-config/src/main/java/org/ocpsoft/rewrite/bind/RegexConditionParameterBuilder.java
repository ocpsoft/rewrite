package org.ocpsoft.rewrite.bind;

import java.util.ArrayList;
import java.util.List;

import org.ocpsoft.rewrite.bind.ParameterizedPattern.RegexParameter;
import org.ocpsoft.rewrite.param.ConditionParameterBuilder;
import org.ocpsoft.rewrite.param.ParameterizedCondition;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class RegexConditionParameterBuilder extends ConditionParameterBuilder<RegexConditionParameterBuilder, String>
{
   private final List<RegexParameter> parameters;

   public RegexConditionParameterBuilder(
            ParameterizedCondition<ConditionParameterBuilder<RegexConditionParameterBuilder, String>, String> parent,
            RegexParameter... parameters)
   {
      super(parent, parameters);
      this.parameters = new ArrayList<RegexParameter>();
      for (RegexParameter regexParameter : parameters) {
         if (regexParameter != null)
         {
            this.parameters.add(regexParameter);
         }
      }
   }

   public RegexConditionParameterBuilder matches(String pattern)
   {

      for (RegexParameter parameter : parameters) {
         parameter.matches(pattern);
      }
      return this;
   }

}
