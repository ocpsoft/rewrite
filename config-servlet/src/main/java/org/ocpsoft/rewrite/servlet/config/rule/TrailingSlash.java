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

import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.InboundRewrite;
import org.ocpsoft.rewrite.event.OutboundRewrite;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.Redirect;
import org.ocpsoft.rewrite.servlet.config.Substitute;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * {@link org.ocpsoft.rewrite.config.Rule} that creates a bi-directional rewrite rule handling appending or removal of the trailing slash character
 * in paths.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class TrailingSlash implements Rule
{
   private String id;

   protected Operation operation;
   protected Condition condition;

   /**
    * Return a new {@link TrailingSlash} instance that will remove trailing slashes from all matching request and
    * rendered output URLs, if necessary.
    */
   public static TrailingSlash remove()
   {
      return new RemoveTrailingSlash();
   }

   /**
    * Return a new {@link TrailingSlash} instance that will append trailing slashes from all matching request and
    * rendered output URLs, if necessary.
    */
   public static TrailingSlash append()
   {
      return new AppendTrailingSlash();
   }

   private static class AppendTrailingSlash extends TrailingSlash
   {
      @Override
      public boolean evaluate(final Rewrite event, final EvaluationContext context)
      {
         if ((condition == null) || condition.evaluate(event, context))
         {
            if (Direction.isInbound().and(Path.matches("/{path}").where("path").matches(".*[^/]"))
                     .evaluate(event, context))
            {
               if (operation != null)
                  context.addPreOperation(operation);

               return true;
            }
            else if (Direction.isOutbound().and(Path.matches("/{path}").where("path").matches(".*[^/]"))
                     .evaluate(event, context))
            {
               if (operation != null)
                  context.addPreOperation(operation);

               return true;
            }
         }

         return false;
      }

      @Override
      public void perform(final Rewrite event, final EvaluationContext context)
      {
         if (event instanceof InboundRewrite)
         {
            Redirect.permanent(((HttpServletRewrite) event).getContextPath() + "/{path}/").perform(event, context);
         }

         else if (event instanceof OutboundRewrite)
         {
            Substitute.with(((HttpServletRewrite) event).getContextPath() + "/{path}/").perform(event, context);
         }
      }
   }

   private static class RemoveTrailingSlash extends TrailingSlash
   {
      @Override
      public boolean evaluate(final Rewrite event, final EvaluationContext context)
      {
         if ((condition == null) || condition.evaluate(event, context))
         {
            if (Direction.isInbound().and(Path.matches("/{path}/").where("path").matches(".*"))
                     .evaluate(event, context))
            {
               if (operation != null)
                  context.addPreOperation(operation);

               return true;
            }
            else if (Direction.isOutbound().and(Path.matches("/{path}/").where("path").matches(".*"))
                     .evaluate(event, context))
            {
               if (operation != null)
                  context.addPreOperation(operation);

               return true;
            }
         }

         return false;
      }

      @Override
      public void perform(final Rewrite event, final EvaluationContext context)
      {
         if (event instanceof InboundRewrite)
         {
            Redirect.permanent(((HttpServletRewrite) event).getContextPath() + "/{path}").perform(event, context);
         }

         else if (event instanceof OutboundRewrite)
         {
            Substitute.with(((HttpServletRewrite) event).getContextPath() + "/{path}").perform(event, context);
         }
      }
   }

   @Override
   public String getId()
   {
      return id;
   }

   public TrailingSlash when(final Condition condition)
   {
      this.condition = condition;
      return this;
   }

   public TrailingSlash performInbound(final Operation operation)
   {
      this.operation = operation;
      return this;
   }

   /**
    * Set the ID of this {@link TrailingSlash}.
    */
   public TrailingSlash withId(final String id)
   {
      this.id = id;
      return this;
   }
}
