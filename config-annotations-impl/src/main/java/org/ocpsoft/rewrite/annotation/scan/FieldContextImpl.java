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
package org.ocpsoft.rewrite.annotation.scan;

import org.ocpsoft.rewrite.annotation.api.ClassContext;
import org.ocpsoft.rewrite.annotation.api.FieldContext;
import org.ocpsoft.rewrite.bind.BindingBuilder;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.RuleBuilder;
import org.ocpsoft.rewrite.context.ContextBase;

/**
 * Default implementation of {@link FieldContext}
 * 
 * @author Christian Kaltepoth
 */
@SuppressWarnings("rawtypes")
public class FieldContextImpl extends ContextBase implements FieldContext
{

   private final ClassContext classContext;

   private BindingBuilder bindingBuilder;

   public FieldContextImpl(ClassContext classContext)
   {
      this.classContext = classContext;
   }

   @Override
   public ConfigurationBuilder getConfigurationBuilder()
   {
      return classContext.getConfigurationBuilder();
   }

   @Override
   public RuleBuilder getRuleBuilder()
   {
      return classContext.getRuleBuilder();
   }

   @Override
   public void setBindingBuilder(BindingBuilder bindingBuilder)
   {
      this.bindingBuilder = bindingBuilder;
   }

   @Override
   public BindingBuilder getBindingBuilder()
   {
      return bindingBuilder;
   }

}
