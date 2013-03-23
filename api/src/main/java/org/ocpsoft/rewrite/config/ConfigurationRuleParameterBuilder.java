/*
 * Copyright 2013 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ocpsoft.rewrite.config;

import java.util.List;

import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.Converter;
import org.ocpsoft.rewrite.bind.Evaluation;
import org.ocpsoft.rewrite.bind.Validator;
import org.ocpsoft.rewrite.param.Constraint;
import org.ocpsoft.rewrite.param.ParameterBuilder;
import org.ocpsoft.rewrite.param.RegexConstraint;
import org.ocpsoft.rewrite.param.Transform;

/**
 * A {@link ParameterBuilder} for a {@link ConfigurationRuleBuilder}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ConfigurationRuleParameterBuilder extends ParameterBuilder<ConfigurationRuleParameterBuilder>
         implements

         ConfigurationRuleParameter,
         ConfigurationRuleParameterMatches,
         ConfigurationRuleParameterWhere,
         ConfigurationRuleBuilderOtherwise
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

   @Override
   public ConfigurationRuleBuilderWithId withId(String id)
   {
      return parent.withId(id);
   }

   @Override
   public ConfigurationRuleBuilderWithPriority withPriority(int priority)
   {
      return parent.withPriority(priority);
   }

}
