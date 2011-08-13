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
package com.ocpsoft.rewrite.servlet.config.parameters.impl;

import com.ocpsoft.rewrite.bind.Binding;
import com.ocpsoft.rewrite.servlet.config.parameters.Parameterized;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ParameterBuilder implements Parameterized<ParameterBuilder>
{
   private final Parameterized<ParameterBuilder> parent;
   private final Parameter parameter;

   public ParameterBuilder(final Parameterized<ParameterBuilder> parent,
            final Parameter parameter)
   {
      this.parent = parent;
      this.parameter = parameter;
   }

   /*
    * ParameterBuilder 
    */
   public ParameterBuilder matches(final String pattern)
   {
      parameter.matches(pattern);
      return this;
   }

   public ParameterBuilder bindsTo(final Binding binding)
   {
      parameter.bindsTo(binding);
      return this;
   }

   /*
    * Parameterized<?>
    */
   public ParameterBuilder where(final String param)
   {
      return parent.where(param);
   }

   public ParameterBuilder where(final String param, final String pattern)
   {
      return parent.where(param, pattern);
   }

   public ParameterBuilder where(final String param, final String pattern, final Binding binding)
   {
      return parent.where(param, pattern, binding);
   }

   public ParameterBuilder where(final String param, final Binding binding)
   {
      return parent.where(param, binding);
   }
}
