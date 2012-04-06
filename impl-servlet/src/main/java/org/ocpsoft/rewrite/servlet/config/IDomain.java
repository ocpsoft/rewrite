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
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.param.ParameterBuilder;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.servlet.config.IDomain.DomainParameter;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface IDomain extends Parameterized<IDomain, DomainParameter, String>, ConditionBuilder
{
   /**
    * Bind named parameters to the request parameter map.
    */
   public IDomain withRequestBinding();

   public ParameterizedPattern getDomainExpression();

   public interface IDomainParameter extends IDomain, Bindable<DomainParameter>, Parameter<DomainParameter, String>
   {
      IDomainParameter matches(String string);
   }

   public class DomainParameter extends ParameterBuilder<DomainParameter, String> implements IDomainParameter
   {
      private final IDomain parent;
      private final RegexCapture capture;

      public DomainParameter(IDomain path, RegexCapture capture)
      {
         super(capture);
         this.parent = path;
         this.capture = capture;
      }

      @Override
      public IDomainParameter matches(String string)
      {
         capture.matches(string);
         return this;
      }

      @Override
      public String getName()
      {
         return capture.getName();
      }

      @Override
      public IDomain withRequestBinding()
      {
         return parent.withRequestBinding();
      }

      @Override
      public DomainParameter where(String param)
      {
         return parent.where(param);
      }

      @Override
      public DomainParameter where(String param, Binding binding)
      {
         return parent.where(param, binding);
      }

      @Override
      public boolean evaluate(Rewrite event, EvaluationContext context)
      {
         return parent.evaluate(event, context);
      }

      @Override
      public ParameterizedPattern getDomainExpression()
      {
         return parent.getDomainExpression();
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
