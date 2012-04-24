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

import java.util.Arrays;
import java.util.List;

import org.ocpsoft.rewrite.bind.Bindable;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.ParameterizedPattern;
import org.ocpsoft.rewrite.bind.RegexCapture;
import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.config.ConditionBuilder;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.param.ParameterBuilder;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.servlet.config.rule.ICDN.CDNParameter;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ICDN extends Parameterized<ICDN, CDNParameter, String>, Rule, ConditionBuilder
{
   /**
    * The new resource (real or virtual) to be served, either from a local context or remote CDN.
    */
   public ICDN to(String location);

   public ParameterizedPattern getLocationExpression();

   public ParameterizedPattern getResourcExpression();

   /**
    * Define additional {@link Operation} instances to be performed when this rule matches successfully.
    */
   public ICDN perform(Operation operation);

   /**
    * Define additional {@link Condition} instances that must be satisfied in order for this rule to match successfully.
    */
   public ICDN when(Condition condition);

   /**
    * Set the ID of this {@link Join}.
    */
   public ICDN withId(String id);

   public interface ICDNParameter extends ICDN, Bindable<CDNParameter>, Parameter<CDNParameter, String>
   {
      /**
       * Specify the pattern to which this Parameter must match.
       */
      ICDNParameter matches(String string);
   }

   public class CDNParameter extends ParameterBuilder<CDNParameter, String> implements ICDNParameter
   {
      private final ICDN parent;
      private final List<RegexCapture> captures;

      public CDNParameter(ICDN join, RegexCapture... captures)
      {
         super((Object[]) captures);
         this.parent = join;
         this.captures = Arrays.asList(captures);
      }

      @Override
      public ICDNParameter matches(String string)
      {
         for (RegexCapture capture : captures) {
            if (capture != null)
               capture.matches(string);
         }
         return this;
      }

      @Override
      public String getName()
      {
         for (RegexCapture capture : captures) {
            if (capture != null)
               return capture.getName();
         }
         return null;
      }

      @Override
      public CDNParameter where(String param)
      {
         return parent.where(param);
      }

      @Override
      public CDNParameter where(String param, Binding binding)
      {
         return parent.where(param, binding);
      }

      @Override
      public boolean evaluate(Rewrite event, EvaluationContext context)
      {
         return parent.evaluate(event, context);
      }

      @Override
      public ParameterizedPattern getLocationExpression()
      {
         return parent.getLocationExpression();
      }

      @Override
      public ParameterizedPattern getResourcExpression()
      {
         return parent.getResourcExpression();
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
      public ICDN to(String location)
      {
         return parent.to(location);
      }

      @Override
      public ICDN perform(Operation operation)
      {
         return parent.perform(operation);
      }

      @Override
      public ICDN when(Condition condition)
      {
         return parent.when(condition);
      }

      @Override
      public ICDN withId(String id)
      {
         return parent.withId(id);
      }
   }
}
