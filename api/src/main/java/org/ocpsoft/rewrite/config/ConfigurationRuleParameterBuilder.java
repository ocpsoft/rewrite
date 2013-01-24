package org.ocpsoft.rewrite.config;

import java.util.List;

import org.ocpsoft.rewrite.bind.Bindable;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.Convertable;
import org.ocpsoft.rewrite.bind.Converter;
import org.ocpsoft.rewrite.bind.Validatable;
import org.ocpsoft.rewrite.bind.Validator;
import org.ocpsoft.rewrite.config.ConditionParameterEnricher.Enricher;
import org.ocpsoft.rewrite.param.Constrainable;
import org.ocpsoft.rewrite.param.Constraint;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.param.RegexConstraint;
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
   public ConfigurationRuleParameterBuilder matches(final String pattern)
   {
      return constrainedBy(new RegexConstraint(pattern));
   }

   @Override
   public ConfigurationRuleParameterBuilder transformedBy(final Transform<String> transform)
   {
      Visitor<Condition> visitor = new ConditionParameterEnricher(parameter, new Enricher() {
         @Override
         public void enrich(Parameter<?, String> parameter)
         {
            parameter.transformedBy(transform);
         }
      });
      new ConditionVisit(parent.getRuleBuilder().getCondition()).accept(visitor);
      return this;
   }

   @Override
   public ConfigurationRuleParameterBuilder constrainedBy(final Constraint<String> constraint)
   {
      Visitor<Condition> visitor = new ConditionParameterEnricher(parameter, new Enricher() {
         @Override
         public void enrich(Parameter<?, String> parameter)
         {
            parameter.constrainedBy(constraint);
         }
      });
      new ConditionVisit(parent.getRuleBuilder().getCondition()).accept(visitor);
      return this;
   }

   @Override
   public <X extends Validator<?>> ConfigurationRuleParameterBuilder validatedBy(final Class<X> type)
   {
      Visitor<Condition> visitor = new ConditionParameterEnricher(parameter, new Enricher() {
         @Override
         public void enrich(Parameter<?, String> parameter)
         {
            parameter.validatedBy(type);
         }
      });
      new ConditionVisit(parent.getRuleBuilder().getCondition()).accept(visitor);
      return this;
   }

   @Override
   public ConfigurationRuleParameterBuilder validatedBy(final Validator<?> validator)
   {
      Visitor<Condition> visitor = new ConditionParameterEnricher(parameter, new Enricher() {
         @Override
         public void enrich(Parameter<?, String> parameter)
         {
            parameter.validatedBy(validator);
         }
      });
      new ConditionVisit(parent.getRuleBuilder().getCondition()).accept(visitor);
      return this;
   }

   @Override
   public <X extends Converter<?>> ConfigurationRuleParameterBuilder convertedBy(final Class<X> type)
   {
      Visitor<Condition> visitor = new ConditionParameterEnricher(parameter, new Enricher() {
         @Override
         public void enrich(Parameter<?, String> parameter)
         {
            parameter.convertedBy(type);
         }
      });
      new ConditionVisit(parent.getRuleBuilder().getCondition()).accept(visitor);
      return this;
   }

   @Override
   public ConfigurationRuleParameterBuilder convertedBy(final Converter<?> converter)
   {
      Visitor<Condition> visitor = new ConditionParameterEnricher(parameter, new Enricher() {
         @Override
         public void enrich(Parameter<?, String> parameter)
         {
            parameter.convertedBy(converter);
         }
      });
      new ConditionVisit(parent.getRuleBuilder().getCondition()).accept(visitor);
      return this;
   }

   @Override
   public ConfigurationRuleParameterBuilder bindsTo(final Binding binding)
   {
      Visitor<Condition> visitor = new ConditionParameterEnricher(parameter, new Enricher() {
         @Override
         public void enrich(Parameter<?, String> parameter)
         {
            parameter.bindsTo(binding);
         }
      });
      new ConditionVisit(parent.getRuleBuilder().getCondition()).accept(visitor);
      return this;
   }

}
