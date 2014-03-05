/*
 * Copyright 2011 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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
package org.ocpsoft.rewrite.param;

import org.ocpsoft.rewrite.bind.Binding;

/**
 * The set of mutators for configuring {@link Parameter} instances.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ParameterConfiguration<IMPLTYPE extends ParameterConfiguration<IMPLTYPE>>
{
   /**
    * Add a {@link Binding} to this {@link ParameterConfiguration}.
    */
   public IMPLTYPE bindsTo(Binding binding);

   /**
    * Set the {@link Converter} with which this {@link ParameterConfiguration} value will be converted.
    */
   public IMPLTYPE convertedBy(final Converter<?> converter);

   /**
    * Set the {@link Validator} with which this {@link ParameterConfiguration} value will be validated.
    */
   public IMPLTYPE validatedBy(final Validator<?> validator);

   /**
    * Add a constraint to which this {@link ParameterConfiguration} must match.
    */
   public IMPLTYPE constrainedBy(Constraint<String> pattern);

   /**
    * Add a {@link Transposition} to this {@link ParameterConfiguration}; it will executed in the order in which it was
    * added.
    */
   public IMPLTYPE transposedBy(Transposition<String> transform);

   /**
    * Add a {@link ParameterConfigurator} with which this {@link ParameterConfiguration} will be configured.
    */
   public IMPLTYPE configuredBy(ParameterConfigurator configurator);
}
