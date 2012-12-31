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
import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.config.ConditionBuilder;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.param.ParameterBuilder;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.ParameterizedPatternParser;
import org.ocpsoft.rewrite.param.ParameterizedPatternParserParameter;
import org.ocpsoft.rewrite.servlet.config.IURL.URLParameter;

/**
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface IURL extends Parameterized<IURL, URLParameter, String>, ConditionBuilder
{
   public IURL withRequestBinding();

   public ParameterizedPatternParser getPathExpression();

   public interface IURLParameter extends IURL, Bindable<URLParameter>, Parameter<URLParameter, String>
   {
      IURLParameter matches(String string);
   }

   public class URLParameter extends ParameterBuilder<URLParameter, String> implements IURLParameter
   {
      private final IURL parent;
      private final ParameterizedPatternParserParameter parameter;

      public URLParameter(IURL path, ParameterizedPatternParserParameter capture)
      {
         super(capture);
         this.parent = path;
         this.parameter = capture;
      }

      @Override
      public IURLParameter matches(String string)
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
      public IURL withRequestBinding()
      {
         return parent.withRequestBinding();
      }

      @Override
      public URLParameter where(String param)
      {
         return parent.where(param);
      }

      @Override
      public boolean evaluate(Rewrite event, EvaluationContext context)
      {
         return parent.evaluate(event, context);
      }

      @Override
      public ParameterizedPatternParser getPathExpression()
      {
         return parent.getPathExpression();
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
