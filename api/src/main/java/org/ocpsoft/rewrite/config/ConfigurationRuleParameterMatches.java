package org.ocpsoft.rewrite.config;

import org.ocpsoft.rewrite.bind.Bindable;
import org.ocpsoft.rewrite.bind.Convertable;
import org.ocpsoft.rewrite.bind.Validatable;
import org.ocpsoft.rewrite.param.Constrainable;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.param.Transformable;

public interface ConfigurationRuleParameterMatches extends ConfigurationBuilderRoot,
         Bindable<ConfigurationRuleParameterBuilder>,
         Convertable<ConfigurationRuleParameterBuilder>,
         Validatable<ConfigurationRuleParameterBuilder>,
         Constrainable<ConfigurationRuleParameterBuilder, String>,
         Transformable<ConfigurationRuleParameterBuilder, String>
{
   /**
    * Configure the {@link Parameter} with the given name.
    */
   ConfigurationRuleParameter where(String string);
}
