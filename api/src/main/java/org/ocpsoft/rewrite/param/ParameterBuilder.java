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

import java.util.ArrayList;
import java.util.List;

import org.ocpsoft.rewrite.bind.Binding;

/**
 * An base implementation of {@link Parameter}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class ParameterBuilder<IMPLTYPE extends ParameterBuilder<IMPLTYPE>> implements
         ConfigurableParameter<IMPLTYPE>
{
   private final List<Binding> bindings = new ArrayList<Binding>();
   private final List<Transposition<String>> transpositions = new ArrayList<Transposition<String>>();
   private final List<Constraint<String>> constraints = new ArrayList<Constraint<String>>();
   private Converter<?> converter = null;
   private Validator<?> validator = null;
   private String name;

   /**
    * Create a new {@link ParameterBuilder} instance with the given name.
    */
   protected ParameterBuilder(String name)
   {
      this.name = name;
   }

   @Override
   @SuppressWarnings("unchecked")
   public IMPLTYPE configuredBy(ParameterConfigurator configurator)
   {
      if (configurator instanceof Binding)
         bindsTo((Binding) configurator);
      if (configurator instanceof Constraint<?>)
         constrainedBy((Constraint<String>) configurator);
      if (configurator instanceof Converter<?>)
         convertedBy((Converter<?>) configurator);
      if (configurator instanceof Validator<?>)
         validatedBy((Validator<?>) configurator);
      if (configurator instanceof Transposition<?>)
         transposedBy((Transposition<String>) configurator);

      return (IMPLTYPE) this;
   }

   @Override
   @SuppressWarnings("unchecked")
   public IMPLTYPE bindsTo(final Binding binding)
   {
      /*
       * Bindings must be added to the front of the list, since we want the
       * ability to override the default binding if necessary.
       */
      this.bindings.add(0, binding);
      return (IMPLTYPE) this;
   }

   @Override
   public List<Binding> getBindings()
   {
      return bindings;
   }

   @Override
   public String getName()
   {
      return name;
   }

   @Override
   @SuppressWarnings("unchecked")
   public IMPLTYPE convertedBy(Converter<?> converter)
   {
      this.converter = converter;
      return (IMPLTYPE) this;
   }

   @Override
   public Converter<?> getConverter()
   {
      return converter;
   }

   @Override
   @SuppressWarnings("unchecked")
   public IMPLTYPE validatedBy(Validator<?> validator)
   {
      this.validator = validator;
      return (IMPLTYPE) this;
   }

   @Override
   public Validator<?> getValidator()
   {
      return validator;
   }

   @Override
   @SuppressWarnings("unchecked")
   public IMPLTYPE constrainedBy(Constraint<String> constraint)
   {
      this.constraints.add(constraint);
      return (IMPLTYPE) this;
   }

   @Override
   public List<Constraint<String>> getConstraints()
   {
      return constraints;
   }

   @Override
   @SuppressWarnings("unchecked")
   public IMPLTYPE transposedBy(Transposition<String> transform)
   {
      this.transpositions.add(transform);
      return (IMPLTYPE) this;
   }

   @Override
   public List<Transposition<String>> getTranspositions()
   {
      return transpositions;
   }

   @Override
   public String toString()
   {
      return "ParameterBuilder [name=\"" + name + "\" -> transpositions=" + transpositions + ", constraints="
               + constraints
               + ", bindings=" + getBindings() + ", converter=" + converter + ", validator=" + validator + "]";
   }

}
