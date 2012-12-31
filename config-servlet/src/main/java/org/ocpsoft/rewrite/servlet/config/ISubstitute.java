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
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.config.OperationBuilder;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.param.ParameterBuilder;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.ParameterizedPatternParser;
import org.ocpsoft.rewrite.param.ParameterizedPatternParserParameter;
import org.ocpsoft.rewrite.param.RegexConstraint;
import org.ocpsoft.rewrite.servlet.config.ISubstitute.SubstituteParameter;

/**
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface ISubstitute extends Parameterized<ISubstitute, SubstituteParameter, String>, OperationBuilder
{
   public ParameterizedPatternParser getTargetExpression();

   public interface ISubstituteParameter extends ISubstitute, Bindable<SubstituteParameter>, Parameter<SubstituteParameter, String>
   {
      ISubstituteParameter matches(String string);
   }

   public class SubstituteParameter extends ParameterBuilder<SubstituteParameter, String> implements ISubstituteParameter
   {
      private final ISubstitute parent;
      private final ParameterizedPatternParserParameter parameter;

      public SubstituteParameter(ISubstitute path, ParameterizedPatternParserParameter parameter)
      {
         super(parameter);
         this.parent = path;
         this.parameter = parameter;
      }

      @Override
      public ISubstituteParameter matches(String regex)
      {
         parameter.constrainedBy(new RegexConstraint(regex));
         return this;
      }

      @Override
      public String getName()
      {
         return parameter.getName();
      }

      @Override
      public SubstituteParameter where(String param)
      {
         return parent.where(param);
      }

      @Override
      public void perform(Rewrite event, EvaluationContext context)
      {
         parent.perform(event, context);
      }

      @Override
      public ParameterizedPatternParser getTargetExpression()
      {
         return parent.getTargetExpression();
      }

      @Override
      public OperationBuilder and(Operation other)
      {
         return parent.and(other);
      }
   }
}
