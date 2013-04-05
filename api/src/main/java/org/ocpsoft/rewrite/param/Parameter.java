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

import java.util.List;

import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.config.Rule;

/**
 * Provides metadata for handling parameter behavior in {@link Parameterized} {@link Condition}, {@link Operation}, and
 * {@link Rule} instances.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface Parameter<IMPLTYPE extends Parameter<IMPLTYPE>>
{
   /**
    * Get the name of this {@link Parameter}
    */
   public String getName();

   /**
    * Retrieve all {@link Binding} instances to which this {@link Parameter} is bound.
    */
   List<Binding> getBindings();

   /**
    * Get the {@link Converter} with which this {@link Parameter} will be converted.
    */
   public Converter<?> getConverter();

   /**
    * Get the {@link Validator} with which this {@link Parameter} will be validated.
    */
   public Validator<?> getValidator();

   /**
    * Get the underlying {@link List} of all {@link Constraint} objects currently registered to this {@link Parameter}.
    */
   List<Constraint<String>> getConstraints();

   /**
    * Get the underlying {@link List} of all {@link Transposition} objects currently registered to this {@link Parameter}.
    */
   List<Transposition<String>> getTranspositions();
}
