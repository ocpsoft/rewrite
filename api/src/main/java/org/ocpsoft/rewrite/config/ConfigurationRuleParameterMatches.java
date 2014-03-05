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

import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.param.Constraint;
import org.ocpsoft.rewrite.param.Converter;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.param.ParameterConfigurator;
import org.ocpsoft.rewrite.param.Transposition;
import org.ocpsoft.rewrite.param.Validator;

/**
 * A {@link ConfigurationRuleParameter} with a matches clause.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface ConfigurationRuleParameterMatches extends ConfigurationBuilderRoot, ConfigurationRuleBuilderOtherwise
{
   /**
    * Configure the {@link Parameter} with the given name.
    */
   @Override
   ConfigurationRuleParameterWhere where(String string);

   /**
    * Add a {@link Binding} to this {@link Parameter}.
    */
   ConfigurationRuleParameterMatches bindsTo(Binding binding);

   /**
    * Set the {@link Converter} with which this {@link Parameter} value will be converted.
    */
   public ConfigurationRuleParameterMatches convertedBy(final Converter<?> converter);

   /**
    * Set the {@link Validator} with which this {@link Parameter} value will be validated.
    */
   public ConfigurationRuleParameterMatches validatedBy(final Validator<?> validator);

   /**
    * Add a {@link Constraint} to which this object {@link IMPLTYPE} must match.
    */
   public ConfigurationRuleParameterMatches constrainedBy(Constraint<String> pattern);

   /**
    * Add a {@link Transposition} to this {@link IMPLTYPE}; it will executed in the order in which it was added.
    */
   public ConfigurationRuleParameterMatches transposedBy(Transposition<String> transposition);

   /**
    * Add a {@link ParameterConfigurator} with which this {@link Parameter} will be configured.
    */
   public ConfigurationRuleParameterMatches configuredBy(ParameterConfigurator configurator);
}
