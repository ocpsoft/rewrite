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
package org.ocpsoft.rewrite.config.jodatime;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.config.DefaultConditionBuilder;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * A {@link Condition} used to evaluate temporal statements using Joda Time
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class JodaTime extends DefaultConditionBuilder
{
   private final TimeCondition condition;
   private DateTimeZone zone;

   /*
    * Constructors
    */
   /**
    * Create a new {@link JodaTime} with the given {@link TimeCondition}
    */
   public JodaTime(final TimeCondition condition)
   {
      this.condition = condition;
   }

   /**
    * Create a new {@link JodaTime} with the given {@link TimeCondition} and provided {@link DateTimeZone}
    */
   public JodaTime(final TimeCondition condition, final DateTimeZone zone)
   {
      this.condition = condition;
      this.zone = zone;
   }

   /*
    * Configurators
    */
   /**
    * Create a new {@link JodaTime} condition that, for each {@link org.ocpsoft.rewrite.event.Rewrite}, evaluates the
    * given {@link TimeCondition} again the system's current {@link DateTime}
    */
   public static JodaTime matches(final TimeCondition condition)
   {
      return new JodaTime(condition) {
         @Override
         public String toString()
         {
            return "JodaTime.matches(" + condition + ")";
         }
      };
   }

   /**
    * Create a new {@link JodaTime} condition that, for each {@link org.ocpsoft.rewrite.event.Rewrite}, evaluates the
    * given {@link TimeCondition} again the system's current {@link DateTime} using the provided {@link DateTimeZone}
    */
   public static JodaTime matches(final TimeCondition condition, final DateTimeZone zone)
   {
      return new JodaTime(condition, zone) {
         @Override
         public String toString()
         {
            return "JodaTime.matches(" + condition + ", " + zone + ")";
         }
      };
   }

   /*
    * Evaluator
    */
   @Override
   public boolean evaluate(final Rewrite event, final EvaluationContext context)
   {
      if (zone != null)
         return condition.matches(new DateTime(zone));
      else
         return condition.matches(new DateTime());
   }

}
