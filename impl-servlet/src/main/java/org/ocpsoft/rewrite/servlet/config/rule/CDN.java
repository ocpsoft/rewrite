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

import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.ParameterizedPatternImpl;
import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.config.ConditionBuilder;
import org.ocpsoft.rewrite.config.DefaultConditionBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.config.IPath;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.Substitute;

/**
 * {@link org.ocpsoft.rewrite.config.Rule} that creates a bi-directional rewrite rule between an externally facing URL
 * and an internal server resource URL
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class CDN implements ICDN
{
   private String id;

   private Substitute location;
   private final IPath resource;

   private Operation operation;
   private Condition condition;

   protected CDN(final String pattern)
   {
      this.resource = Path.matches(pattern);
   }

   /**
    * The outward facing URL path to which this {@link CDN} will apply.
    */
   public static ICDN relocate(final String pattern)
   {
      return new CDN(pattern);
   }

   @Override
   public ICDN to(final String location)
   {
      this.location = Substitute.with(location);
      return this;
   }

   @Override
   public boolean evaluate(final Rewrite event, final EvaluationContext context)
   {
      if (Direction.isOutbound().evaluate(event, context))
         return resource.evaluate(event, context);
      return false;
   }

   @Override
   public void perform(final Rewrite event, final EvaluationContext context)
   {
      if (Direction.isOutbound().evaluate(event, context))
         location.and(operation).perform(event, context);
   }

   @Override
   public CDNParameter where(final String parameter)
   {
      return new CDNParameter(this, resource.getPathExpression().getParameter(parameter));
   }

   @Override
   public CDNParameter where(final String param, final Binding binding)
   {
      return where(param).bindsTo(binding);
   }

   @Override
   public String getId()
   {
      return id;
   }

   @Override
   public ICDN when(final Condition condition)
   {
      this.condition = condition;
      return this;
   }

   @Override
   public ICDN perform(final Operation operation)
   {
      this.operation = operation;
      return this;
   }

   @Override
   public ICDN withId(final String id)
   {
      this.id = id;
      return this;
   }

   @Override
   public String toString()
   {
      return "Join [resource=" + resource + ", to=" + location + ", id=" + id + "]";
   }

   @Override
   public ConditionBuilder and(Condition condition)
   {
      return DefaultConditionBuilder.wrap(this.condition).and(condition);
   }

   @Override
   public ConditionBuilder andNot(Condition condition)
   {
      return DefaultConditionBuilder.wrap(this.condition).andNot(condition);
   }

   @Override
   public ConditionBuilder or(Condition condition)
   {
      return DefaultConditionBuilder.wrap(this.condition).or(condition);
   }

   @Override
   public ConditionBuilder orNot(Condition condition)
   {
      return DefaultConditionBuilder.wrap(this.condition).orNot(condition);
   }

   @Override
   public ParameterizedPatternImpl getLocationExpression()
   {
      return location.getTargetExpression();
   }

   @Override
   public ParameterizedPatternImpl getResourcExpression()
   {
      return resource.getPathExpression();
   }

}
