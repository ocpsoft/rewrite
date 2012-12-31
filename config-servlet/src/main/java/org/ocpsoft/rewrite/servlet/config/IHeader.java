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

import java.util.Arrays;
import java.util.List;

import org.ocpsoft.rewrite.bind.Bindable;
import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.config.ConditionBuilder;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.Constraint;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.param.ParameterBuilder;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.ParameterizedPatternParserParameter;
import org.ocpsoft.rewrite.param.Transform;
import org.ocpsoft.rewrite.servlet.config.IHeader.HeaderParameter;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface IHeader extends Parameterized<IHeader, HeaderParameter, String>, ConditionBuilder
{
   public interface IHeaderParameter extends IHeader, Bindable<HeaderParameter>, Parameter<HeaderParameter, String>
   {
      IHeaderParameter matches(String string);
   }

   public class HeaderParameter extends ParameterBuilder<HeaderParameter, String> implements IHeaderParameter
   {
      private final IHeader parent;
      private final List<ParameterizedPatternParserParameter> parameters;

      public HeaderParameter(IHeader path, ParameterizedPatternParserParameter... captures)
      {
         super((Object[]) captures);
         this.parent = path;
         this.parameters = Arrays.asList(captures);
      }

      @Override
      public HeaderParameter constrainedBy(Constraint<String> constraint)
      {
         for (ParameterizedPatternParserParameter parameter : parameters) {
            if (parameter != null)
               parameter.constrainedBy(constraint);
         }
         return this;
      }

      @Override
      public HeaderParameter transformedBy(Transform<String> transform)
      {
         for (ParameterizedPatternParserParameter parameter : parameters) {
            if (parameter != null)
               parameter.transformedBy(transform);
         }
         return this;
      }

      @Override
      public HeaderParameter where(String param)
      {
         return parent.where(param);
      }

      @Override
      public IHeaderParameter matches(String string)
      {
         for (ParameterizedPatternParserParameter parameter : parameters) {
            if (parameter != null)
               parameter.matches(string);
         }
         return this;
      }

      @Override
      public String getName()
      {
         for (ParameterizedPatternParserParameter parameter : parameters) {
            if (parameter != null)
               return parameter.getName();
         }
         return null;
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
