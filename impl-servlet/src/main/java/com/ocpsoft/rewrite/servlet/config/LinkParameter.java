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

import com.ocpsoft.rewrite.bind.Binding;
import com.ocpsoft.rewrite.config.Operation;
import com.ocpsoft.rewrite.servlet.config.parameters.Parameter;
import com.ocpsoft.rewrite.servlet.config.parameters.Parameterized;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class LinkParameter implements Parameterized<LinkParameter>
{
   private final Join parent;
   private final Parameter parameter;

   public LinkParameter(final Join link, final Parameter parameter)
   {
      this.parent = link;
      this.parameter = parameter;
   }

   public LinkParameter matches(final String pattern)
   {
      parameter.matches(pattern);
      return this;
   }

   public LinkParameter bindsTo(final Binding binding)
   {
      parameter.bindsTo(binding);
      return this;
   }

   @Override
   public LinkParameter where(final String param)
   {
      return parent.where(param);
   }

   @Override
   public LinkParameter where(final String param, final String pattern)
   {
      return parent.where(param, pattern);
   }

   @Override
   public LinkParameter where(final String param, final String pattern, final Binding binding)
   {
      return parent.where(param, pattern, binding);
   }

   @Override
   public LinkParameter where(final String param, final Binding binding)
   {
      return parent.where(param, binding);
   }

   public Join and(final Operation operation)
   {
      return parent.and(operation);
   }

}
