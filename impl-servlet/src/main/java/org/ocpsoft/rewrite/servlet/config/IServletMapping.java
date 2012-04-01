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
package org.ocpsoft.rewrite.servlet.config;

import org.ocpsoft.rewrite.bind.Bindable;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.ParameterizedPattern;
import org.ocpsoft.rewrite.bind.RegexCapture;
import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.config.ConditionBuilder;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.Constraint;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.param.ParameterBuilder;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.Transform;
import org.ocpsoft.rewrite.servlet.config.IServletMapping.ServletMappingParameter;

/**
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface IServletMapping extends Parameterized<IServletMapping, ServletMappingParameter, String>,
ConditionBuilder
{
   public ParameterizedPattern getResourceExpression();

   public interface IServletMappingParameter extends IServletMapping, Bindable<ServletMappingParameter>, Parameter<ServletMappingParameter, String>
   {
      IServletMappingParameter matches(String string);
   }

   public class ServletMappingParameter extends ParameterBuilder<ServletMappingParameter, String> implements IServletMappingParameter
   {
      private final IServletMapping parent;
      private final RegexCapture parameter;

      public ServletMappingParameter(IServletMapping path, RegexCapture parameter)
      {
         this.parent = path;
         this.parameter = parameter;
      }

      @Override
      public ServletMappingParameter constrainedBy(Constraint<String> constraint)
      {
         parameter.constrainedBy(constraint);
         return this;
      }

      @Override
      public ServletMappingParameter transformedBy(Transform<String> transform)
      {
         parameter.transformedBy(transform);
         return this;
      }

      @Override
      public ServletMappingParameter bindsTo(Binding binding)
      {
         parameter.bindsTo(binding);
         return this;
      }

      @Override
      public ServletMappingParameter where(String param)
      {
         return parent.where(param);
      }

      @Override
      public ServletMappingParameter where(String param, Binding binding)
      {
         return parent.where(param, binding);
      }

      @Override
      public IServletMappingParameter matches(String string)
      {
         parameter.matches(string);
         return this;
      }

      @Override
      public String getName()
      {
         return parameter.getName();
      }

      @Override
      public ParameterizedPattern getResourceExpression()
      {
         return parent.getResourceExpression();
      }

      @Override
      public boolean evaluate(Rewrite event, EvaluationContext context)
      {
         return parent.evaluate(event, context);
      }

      @Override
      public ConditionBuilder and(Condition condition)
      {
         return parent.and(condition);
      }

      @Override
      public ConditionBuilder andNot(Condition condition)
      {
         return parent.andNot(condition);
      }

      @Override
      public ConditionBuilder or(Condition condition)
      {
         return parent.or(condition);
      }

      @Override
      public ConditionBuilder orNot(Condition condition)
      {
         return parent.orNot(condition);
      }
   }
}
