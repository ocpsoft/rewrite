package org.ocpsoft.rewrite.config;

import java.util.List;

import org.ocpsoft.rewrite.bind.Bindable;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.Convertable;
import org.ocpsoft.rewrite.bind.Converter;
import org.ocpsoft.rewrite.bind.Evaluation;
import org.ocpsoft.rewrite.bind.Validatable;
import org.ocpsoft.rewrite.bind.Validator;
import org.ocpsoft.rewrite.param.Constrainable;
import org.ocpsoft.rewrite.param.Constraint;
import org.ocpsoft.rewrite.param.ParameterBuilder;
import org.ocpsoft.rewrite.param.RegexConstraint;
import org.ocpsoft.rewrite.param.Transform;
import org.ocpsoft.rewrite.param.Transformable;

public class ConfigurationRuleParameterBuilder extends ParameterBuilder<ConfigurationRuleParameterBuilder>
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

   public ConfigurationRuleParameterBuilder(ConfigurationRuleBuilder parent,
            final String name)
   {
      super(name);
      this.parent = parent;

      this.bindsTo(Evaluation.property(name));
   }

   @Override
   public ConfigurationRuleParameterBuilder where(String parameter)
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
   public ConfigurationRuleParameterBuilder matches(final String pattern)
   {
      return super.constrainedBy(new RegexConstraint(pattern));
   }

   @Override
   public ConfigurationRuleParameterBuilder transformedBy(final Transform<String> transform)
   {
      return super.transformedBy(transform);
   }

   @Override
   public ConfigurationRuleParameterBuilder constrainedBy(final Constraint<String> constraint)
   {
      return super.constrainedBy(constraint);
   }

   @Override
   public <X extends Validator<?>> ConfigurationRuleParameterBuilder validatedBy(final Class<X> type)
   {
      return super.validatedBy(type);
   }

   @Override
   public ConfigurationRuleParameterBuilder validatedBy(final Validator<?> validator)
   {
      return super.validatedBy(validator);
   }

   @Override
   public <X extends Converter<?>> ConfigurationRuleParameterBuilder convertedBy(final Class<X> type)
   {
      return super.convertedBy(type);
   }

   @Override
   public ConfigurationRuleParameterBuilder convertedBy(final Converter<?> converter)
   {
      return super.convertedBy(converter);
   }

   @Override
   public ConfigurationRuleParameterBuilder bindsTo(final Binding binding)
   {
      return super.bindsTo(binding);
   }

}
