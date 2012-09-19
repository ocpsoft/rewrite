package org.ocpsoft.rewrite.annotation.context;

import org.ocpsoft.rewrite.annotation.api.ClassContext;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.ConfigurationRuleBuilder;
import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.config.RuleBuilder;
import org.ocpsoft.rewrite.context.ContextBase;

public class ClassContextImpl extends ContextBase implements ClassContext
{
   private final ConfigurationBuilder config;

   private ConfigurationRuleBuilder configurationRuleBuilder;

   private final Class<?> javaClass;

   public ClassContextImpl(ConfigurationBuilder config, Class<?> clazz)
   {
      this.config = config;
      this.javaClass = clazz;
   }

   @Override
   public void setBaseRule(Rule rule)
   {
      configurationRuleBuilder = config.addRule(rule);
   }

   @Override
   public RuleBuilder getRuleBuilder()
   {
      if (configurationRuleBuilder == null) {
         configurationRuleBuilder = config.addRule();
      }
      return configurationRuleBuilder.getRuleBuilder();
   }

   @Override
   public ConfigurationBuilder getConfigurationBuilder()
   {
      return config;
   }

   @Override
   public Class<?> getJavaClass()
   {
      return javaClass;
   }

}
