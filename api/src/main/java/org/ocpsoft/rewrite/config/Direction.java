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
package org.ocpsoft.rewrite.config;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.InboundRewrite;
import org.ocpsoft.rewrite.event.OutboundRewrite;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * Responsible for evaluating {@link Rewrite} events on whether they are {@link InboundRewrite} or
 * {@link OutboundRewrite} events.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class Direction extends DefaultConditionBuilder
{
   /**
    * Returns a new {@link Condition} that returns true when operating on an {@link InboundRewrite} event.
    */
   public static Direction isInbound()
   {
      return new Inbound();
   }

   /**
    * Returns a new {@link Condition} that returns true when operating on an {@link OutboundRewrite} event.
    */
   public static Direction isOutbound()
   {
      return new Outbound();
   }

   private static class Inbound extends Direction
   {
      @Override
      public boolean evaluate(final Rewrite event, final EvaluationContext context)
      {
         return event instanceof InboundRewrite;
      }

      @Override
      public String toString()
      {
         return "Direction.isInbound()";
      }
   }

   private static class Outbound extends Direction
   {
      @Override
      public boolean evaluate(final Rewrite event, final EvaluationContext context)
      {
         return event instanceof OutboundRewrite;
      }

      @Override
      public String toString()
      {
         return "Direction.isOutbound()";
      }
   }
}
