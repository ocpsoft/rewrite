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
package org.ocpsoft.rewrite.param;

import java.util.List;

import org.ocpsoft.rewrite.bind.Binding;

/**
 * A an immutable implementation of {@link Parameter}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ImmutableParameter implements Parameter<ImmutableParameter>
{
   private Parameter<?> wrapped;

   public ImmutableParameter(Parameter<?> wrapped)
   {
      this.wrapped = wrapped;
   }

   @Override
   public String getName()
   {
      return wrapped.getName();
   }

   @Override
   public List<Binding> getBindings()
   {
      return wrapped.getBindings();
   }

   @Override
   public Converter<?> getConverter()
   {
      return wrapped.getConverter();
   }

   @Override
   public Validator<?> getValidator()
   {
      return wrapped.getValidator();
   }

   @Override
   public List<Constraint<String>> getConstraints()
   {
      return wrapped.getConstraints();
   }

   @Override
   public List<Transposition<String>> getTranspositions()
   {
      return wrapped.getTranspositions();
   }

}
