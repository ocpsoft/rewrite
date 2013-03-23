package org.ocpsoft.rewrite.config;

import org.ocpsoft.rewrite.bind.Bindable;
import org.ocpsoft.rewrite.param.Parameter;

public interface ConfigurationRuleParameterMatches extends ConfigurationBuilderRoot,
         Bindable<ConfigurationRuleParameterBuilder>
{
   /**
    * Configure the {@link Parameter} with the given name.
    */
   ConfigurationRuleParameter where(String string);
}
