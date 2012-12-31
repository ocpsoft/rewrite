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
import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.param.ParameterBuilder;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.ParameterizedPatternParser;
import org.ocpsoft.rewrite.servlet.config.IPath.PathParameter;
import org.ocpsoft.rewrite.servlet.config.rule.IJoin.JoinParameter;

/**
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface IJoin extends Parameterized<IJoin, JoinParameter, String>, Rule
{
   public ParameterizedPatternParser getPathExpression();

   public ParameterizedPatternParser getResourcexpression();

   /**
    * Define additional {@link Operation} instances to be performed when this rule matches successfully.
    */
   public IJoin perform(Operation operation);

   /**
    * Define additional {@link Condition} instances that must be satisfied in order for this rule to match successfully.
    */
   public IJoin when(Condition condition);

   /**
    * Allow the target of this {@link Join} to invoke another {@link Join} rule. By default, once a {@link Join} has
    * been invoked, no more {@link Join} rules will be processed.
    */
   public IJoin withChaining();

   /**
    * Set the ID of this {@link Join}.
    */
   public IJoin withId(String id);

   /**
    * Redirect inbound requests for the internal resource to the outward facing URL instead.
    */
   public IJoin withInboundCorrection();

   public interface IJoinParameter extends IJoin, Bindable<JoinParameter>, Parameter<JoinParameter, String>
   {
      /**
       * Specify the pattern to which this Parameter must match.
       */
      IJoinParameter matches(String string);
   }

   public class JoinParameter extends ParameterBuilder<JoinParameter, String> implements IJoinParameter
   {
      private final IJoin parent;
      private final List<PathParameter> captures;

      public JoinParameter(IJoin join, PathParameter... captures)
      {
         super((Object[]) captures);
         this.parent = join;
         this.captures = Arrays.asList(captures);
      }

      @Override
      public IJoinParameter matches(String string)
      {
         for (PathParameter capture : captures) {
            if (capture != null)
               capture.matches(string);
         }
         return this;
      }

      @Override
      public String getName()
      {
         for (PathParameter capture : captures) {
            if (capture != null)
               return capture.getName();
         }
         return null;
      }

      @Override
      public JoinParameter where(String param)
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
      public ParameterizedPatternParser getResourcexpression()
      {
         return parent.getResourcexpression();
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

      @Override
      public IJoin perform(Operation operation)
      {
         return parent.perform(operation);
      }

      @Override
      public IJoin when(Condition condition)
      {
         return parent.when(condition);
      }

      @Override
      public IJoin withId(String id)
      {
         return parent.withId(id);
      }

      @Override
      public IJoin withChaining()
      {
         return parent.withChaining();
      }
   }
}
