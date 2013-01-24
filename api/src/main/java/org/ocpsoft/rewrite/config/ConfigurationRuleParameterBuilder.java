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
            final String parameter)
   {
      this.parent = parent;
      this.parameter = parameter;

      enrichParameters(new ParameterEnricher() {
         @Override
         public void enrich(Parameter<?, String> param)
         {
            param.bindsTo(Evaluation.property(parameter));
         }
      });

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
      return constrainedBy(new RegexConstraint(pattern));
   }

   @Override
   public ConfigurationRuleParameterBuilder transformedBy(final Transform<String> transform)
   {
      enrichParameters(new ParameterEnricher() {
         @Override
         public void enrich(Parameter<?, String> parameter)
         {
            parameter.transformedBy(transform);
         }
      });
      return this;
   }

   @Override
   public ConfigurationRuleParameterBuilder constrainedBy(final Constraint<String> constraint)
   {
      enrichParameters(new ParameterEnricher() {
         @Override
         public void enrich(Parameter<?, String> parameter)
         {
            parameter.constrainedBy(constraint);
         }
      });
      return this;
   }

   @Override
   public <X extends Validator<?>> ConfigurationRuleParameterBuilder validatedBy(final Class<X> type)
   {
      enrichParameters(new ParameterEnricher() {
         @Override
         public void enrich(Parameter<?, String> parameter)
         {
            parameter.validatedBy(type);
         }
      });
      return this;
   }

   @Override
   public ConfigurationRuleParameterBuilder validatedBy(final Validator<?> validator)
   {
      enrichParameters(new ParameterEnricher() {
         @Override
         public void enrich(Parameter<?, String> parameter)
         {
            parameter.validatedBy(validator);
         }
      });
      return this;
   }

   @Override
   public <X extends Converter<?>> ConfigurationRuleParameterBuilder convertedBy(final Class<X> type)
   {
      enrichParameters(new ParameterEnricher() {
         @Override
         public void enrich(Parameter<?, String> parameter)
         {
            parameter.convertedBy(type);
         }
      });
      return this;
   }

   @Override
   public ConfigurationRuleParameterBuilder convertedBy(final Converter<?> converter)
   {
      enrichParameters(new ParameterEnricher() {
         @Override
         public void enrich(Parameter<?, String> parameter)
         {
            parameter.convertedBy(converter);
         }
      });
      return this;
   }

   @Override
   public ConfigurationRuleParameterBuilder bindsTo(final Binding binding)
   {
      enrichParameters(new ParameterEnricher() {
         @Override
         public void enrich(Parameter<?, String> parameter)
         {
            parameter.bindsTo(binding);
         }
      });
      return this;
   }

   private void enrichParameters(ParameterEnricher enricher)
   {

      RuleBuilder ruleBuilder = parent.getRuleBuilder();

      // enrich conditions
      Visitor<Condition> conditionVisitor = new ConditionParameterEnricher(parameter, enricher);
      new ConditionVisit(ruleBuilder.getCondition()).accept(conditionVisitor);

      // enrich operations
      Visitor<Operation> operationVisitor = new OperationParameterEnricher(parameter, enricher);
      new OperationVisit(ruleBuilder.getOperation()).accept(operationVisitor);

   }

}
