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
   private ParameterBuilder<?> param;

   public ConfigurationRuleParameterBuilder(ConfigurationRuleBuilder parent,
            final ParameterBuilder<?> param)
   {
      super(param.getName());
      this.parent = parent;
      this.param = param;

      this.bindsTo(Evaluation.property(param.getName()));
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
      param.constrainedBy(new RegexConstraint(pattern));
      return this;
   }

   @Override
   public ConfigurationRuleParameterBuilder transformedBy(final Transform<String> transform)
   {
      param.transformedBy(transform);
      return this;
   }

   @Override
   public ConfigurationRuleParameterBuilder constrainedBy(final Constraint<String> constraint)
   {
      param.constrainedBy(constraint);
      return this;
   }

   @Override
   public ConfigurationRuleParameterBuilder validatedBy(final Validator<?> validator)
   {
      param.validatedBy(validator);
      return this;
   }

   @Override
   public ConfigurationRuleParameterBuilder convertedBy(final Converter<?> converter)
   {
      param.convertedBy(converter);
      return this;
   }

   @Override
   public ConfigurationRuleParameterBuilder bindsTo(final Binding binding)
   {
      param.bindsTo(binding);
      return this;
   }

}
