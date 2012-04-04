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
package org.ocpsoft.rewrite.servlet.config.rule;

import org.ocpsoft.rewrite.bind.Bindable;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.ParameterizedPattern;
import org.ocpsoft.rewrite.bind.RegexCapture;
import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.config.ConditionBuilder;
import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.Constraint;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.param.ParameterBuilder;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.RegexConstraint;
import org.ocpsoft.rewrite.param.Transform;
import org.ocpsoft.rewrite.servlet.config.rule.IJoin.JoinParameter;

/**
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface IJoin extends Parameterized<IJoin, JoinParameter, String>, Rule, ConditionBuilder
{
   public IJoin withInboundCorrection();

   public IJoin withRequestBinding();

   public ParameterizedPattern getPathExpression();

   public ParameterizedPattern getResourcexpression();

   public interface IJoinParameter extends IJoin, Bindable<JoinParameter>, Parameter<JoinParameter, String>
   {
      IJoinParameter matches(String string);
   }

   public class JoinParameter extends ParameterBuilder<JoinParameter, String> implements IJoinParameter
   {
      private final IJoin parent;
      private final RegexCapture parameter;

      public JoinParameter(IJoin path, RegexCapture parameter)
      {
         this.parent = path;
         this.parameter = parameter;
      }

      @Override
      public JoinParameter constrainedBy(Constraint<String> constraint)
      {
         parameter.constrainedBy(constraint);
         return this;
      }

      @Override
      public JoinParameter transformedBy(Transform<String> transform)
      {
         parameter.transformedBy(transform);
         return this;
      }

      @Override
      public JoinParameter bindsTo(Binding binding)
      {
         parameter.bindsTo(binding);
         return this;
      }

      @Override
      public IJoin withRequestBinding()
      {
         return parent.withRequestBinding();
      }

      @Override
      public JoinParameter where(String param)
      {
         return parent.where(param);
      }

      @Override
      public JoinParameter where(String param, Binding binding)
      {
         return parent.where(param, binding);
      }

      @Override
      public IJoinParameter matches(String string)
      {
         parameter.constrainedBy(new RegexConstraint(string));
         return this;
      }

      @Override
      public String getName()
      {
         return parameter.getName();
      }

      @Override
      public boolean evaluate(Rewrite event, EvaluationContext context)
      {
         return parent.evaluate(event, context);
      }

      @Override
      public ParameterizedPattern getPathExpression()
      {
         return parent.getPathExpression();
      }

      @Override
      public ParameterizedPattern getResourcexpression()
      {
         return parent.getResourcexpression();
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

      @Override
      public String getId()
      {
         return parent.getId();
      }

      @Override
      public void perform(Rewrite event, EvaluationContext context)
      {
         parent.perform(event, context);
      }

      @Override
      public IJoin withInboundCorrection()
      {
         return parent.withInboundCorrection();
      }
   }
}
