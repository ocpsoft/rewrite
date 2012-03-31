package org.ocpsoft.rewrite.annotation.scan;

import org.ocpsoft.rewrite.annotation.api.ClassContext;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.RuleBuilder;
import org.ocpsoft.rewrite.context.ContextBase;

public class ClassContextImpl extends ContextBase implements ClassContext
{
   private final ConfigurationBuilder config;
   private final RuleBuilder rule;

   public ClassContextImpl(ConfigurationBuilder config, RuleBuilder rule)
   {
      this.config = config;
      this.rule = rule;
   }

   @Override
   public ConfigurationBuilder getConfigurationBuilder()
   {
      return config;
   }

   @Override
   public RuleBuilder getRuleBuilder()
   {
      return rule;
   }

}
