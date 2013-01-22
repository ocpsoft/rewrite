package org.ocpsoft.rewrite.config;

import java.util.List;

import org.ocpsoft.rewrite.bind.Bindable;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.Convertable;
import org.ocpsoft.rewrite.bind.Converter;
import org.ocpsoft.rewrite.bind.Validatable;
import org.ocpsoft.rewrite.bind.Validator;
import org.ocpsoft.rewrite.param.Constrainable;
import org.ocpsoft.rewrite.param.Constraint;
import org.ocpsoft.rewrite.param.Transform;
import org.ocpsoft.rewrite.param.Transformable;

public class ConfigurationRuleParameterBuilder
         implements
         Bindable<ConfigurationRuleParameterBuilder>,
         Convertable<ConfigurationRuleParameterBuilder>,
         Validatable<ConfigurationRuleParameterBuilder>,
         Constrainable<ConfigurationRuleParameterBuilder, String>,
         Transformable<ConfigurationRuleParameterBuilder, String>,

         ConfigurationRuleParameter,
         ConfigurationRuleParameterMatches,
         ConfigurationRuleParameterPerform,
         ConfigurationRuleParameterWhere,
         ConfigurationRuleParameterOtherwise
{

   private final ConfigurationRuleBuilder parent;
   private final String parameter;

   public ConfigurationRuleParameterBuilder(ConfigurationRuleBuilder parent,
            String parameter)
   {
      this.parent = parent;
      this.parameter = parameter;
   }

   @Override
   public ConfigurationRuleParameter where(String parameter)
   {
      return parent.where(parameter);
   }

   @Override
   public ConfigurationRuleBuilderCustom addRule()
   {
      return parent.addRule();
   }

   @Override
   public ConfigurationRuleBuilder addRule(Rule rule)
   {
      return parent.addRule(rule);
   }

   @Override
   public List<Rule> getRules()
   {
      return parent.getRules();
   }

   @Override
   public ConfigurationRuleParameterBuilder matches(String pattern)
   {
      // TODO Auto-generated method stub
      return this;
   }

   @Override
   public ConfigurationRuleParameterBuilder transformedBy(Transform<String> transform)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ConfigurationRuleParameterBuilder constrainedBy(Constraint<String> pattern)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public <X extends Validator<?>> ConfigurationRuleParameterBuilder validatedBy(Class<X> type)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ConfigurationRuleParameterBuilder validatedBy(Validator<?> validator)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public <X extends Converter<?>> ConfigurationRuleParameterBuilder convertedBy(Class<X> type)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ConfigurationRuleParameterBuilder convertedBy(Converter<?> converter)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ConfigurationRuleParameterBuilder bindsTo(Binding binding)
   {
      // TODO Auto-generated method stub
      return null;
   }

}
