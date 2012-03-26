package org.ocpsoft.rewrite.bind;

import org.ocpsoft.rewrite.param.OperationParameterBuilder;
import org.ocpsoft.rewrite.param.ParameterizedOperation;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class RegexOperationParameterBuilder extends OperationParameterBuilder<RegexOperationParameterBuilder, String>
{
   private final RegexParameter parameter;

   public RegexOperationParameterBuilder(
            ParameterizedOperation<OperationParameterBuilder<RegexOperationParameterBuilder, String>, String> parent,
            RegexParameter parameter)
   {
      super(parent, parameter);
      this.parameter = parameter;
   }

   public RegexOperationParameterBuilder matches(String pattern)
   {
      parameter.matches(pattern);
      return this;
   }

}
