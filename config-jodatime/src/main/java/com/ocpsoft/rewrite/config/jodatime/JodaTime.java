/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
