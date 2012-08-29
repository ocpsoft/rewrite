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
package org.ocpsoft.rewrite.annotation.context;

import java.lang.reflect.Method;

import org.ocpsoft.rewrite.annotation.api.ClassContext;
import org.ocpsoft.rewrite.annotation.api.MethodContext;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.config.RuleBuilder;
import org.ocpsoft.rewrite.context.ContextBase;

/**
 * Default implementation of {@link MethodContext}
 * 
 * @author Christian Kaltepoth
 */
public class MethodContextImpl extends ContextBase implements MethodContext
{

   private final ClassContext classContext;
   
   private final Method method;

   public MethodContextImpl(ClassContext classContext, Method method)
   {
      this.classContext = classContext;
      this.method = method;
   }

   @Override
   public ClassContext getClassContext()
   {
      return classContext;
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
   public void setBaseRule(Rule rule)
   {
      classContext.setBaseRule(rule);
   }

   @Override
   public Method getJavaMethod()
   {
      return method;
   }

   @Override
   public Class<?> getJavaClass()
   {
      return classContext.getJavaClass();
   }

}
