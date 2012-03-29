package org.ocpsoft.rewrite.bind;

import java.util.ArrayList;
import java.util.List;

import org.ocpsoft.rewrite.bind.ParameterizedPattern.RegexParameter;
import org.ocpsoft.rewrite.param.OperationParameterBuilder;
import org.ocpsoft.rewrite.param.ParameterizedOperation;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class RegexOperationParameterBuilder extends OperationParameterBuilder<RegexOperationParameterBuilder, String>
{
   private final List<RegexParameter> parameters;

   public RegexOperationParameterBuilder(
            ParameterizedOperation<OperationParameterBuilder<RegexOperationParameterBuilder, String>, String> parent,
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

   public RegexOperationParameterBuilder matches(String pattern)
   {
      for (RegexParameter parameter : parameters) {
         parameter.matches(pattern);
      }
      return this;
   }

}
