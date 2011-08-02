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
package com.ocpsoft.rewrite.config.jodatime;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.ocpsoft.rewrite.config.Condition;
import com.ocpsoft.rewrite.config.ConditionBuilder;
import com.ocpsoft.rewrite.event.Rewrite;

/**
 * A {@link Condition} used to evaluate temporal statements using Joda Time
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class JodaTime extends ConditionBuilder
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
    * Create a new {@link JodaTime} condition that, for each {@link Rewrite}, evaluates the given {@link TimeCondition}
    * again the system's current {@link DateTime}
    */
   public static JodaTime matches(final TimeCondition condition)
   {
      return new JodaTime(condition);
   }

   /**
    * Create a new {@link JodaTime} condition that, for each {@link Rewrite}, evaluates the given {@link TimeCondition}
    * again the system's current {@link DateTime} using the provided {@link DateTimeZone}
    */
   public static JodaTime matches(final TimeCondition condition, final DateTimeZone zone)
   {
      return new JodaTime(condition, zone);
   }

   /*
    * Evaluator
    */
   @Override
   public boolean evaluate(final Rewrite event)
   {
      if (zone != null)
         return condition.matches(new DateTime(zone));
      else
         return condition.matches(new DateTime());
   }

}
