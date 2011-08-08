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
package com.ocpsoft.rewrite.servlet.config;

import com.ocpsoft.rewrite.EvaluationContext;
import com.ocpsoft.rewrite.config.Operation;
import com.ocpsoft.rewrite.config.OperationBuilder;
import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.servlet.config.parameters.MethodBinding;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class Invoke extends OperationBuilder
{
   private final MethodBinding binding;

   public Invoke(final MethodBinding property)
   {
      this.binding = property;
   }

   @Override
   public void perform(final Rewrite event, final EvaluationContext context)
   {
      Object result = binding.invokeMethod(event, context);
      if (result instanceof Operation)
      {
         ((Operation) result).perform(event, context);
      }
      // TODO interpret string return value here as SPI
   }

   public static OperationBuilder method(final MethodBinding property)
   {
      return new Invoke(property);
   }

}
