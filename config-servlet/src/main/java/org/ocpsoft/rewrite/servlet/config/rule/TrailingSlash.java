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

import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.InboundRewrite;
import org.ocpsoft.rewrite.event.OutboundRewrite;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.config.Redirect;
import org.ocpsoft.rewrite.servlet.config.Substitute;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.urlbuilder.Address;

/**
 * A {@link Rule} that creates a bi-directional rewrite rule handling appending or removal of the trailing slash
 * character in {@link Address} paths.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class TrailingSlash implements Rule
{
   private String id;

   /**
    * Create a new {@link TrailingSlash} instance that will remove trailing slashes from all matching request and
    * rendered output {@link Address} instances, if necessary.
    */
   public static TrailingSlash remove()
   {
      return new RemoveTrailingSlash() {
         @Override
         public String toString()
         {
            return "TrailingSlash.remove()";
         }
      };
   }

   /**
    * Create a new {@link TrailingSlash} instance that will append trailing slashes from all matching request and
    * rendered output {@link Address} instances, if necessary.
    */
   public static TrailingSlash append()
   {
      return new AppendTrailingSlash() {
         @Override
         public String toString()
         {
            return "TrailingSlash.append()";
         }
      };
   }

   /**
    * Set the ID of this {@link TrailingSlash} instance.
    */
   public TrailingSlash withId(final String id)
   {
      this.id = id;
      return this;
   }

   private abstract static class AppendTrailingSlash extends TrailingSlash
   {
      @Override
      public boolean evaluate(final Rewrite event, final EvaluationContext context)
      {
         if (event instanceof HttpServletRewrite)
         {
            return (!((HttpServletRewrite) event).getAddress().getPath().endsWith("/"));
         }
         return false;
      }

      @Override
      public void perform(final Rewrite event, final EvaluationContext context)
      {
         String url = ((HttpServletRewrite) event).getAddress().getPath() + "/";
         if (event instanceof InboundRewrite)
         {
            Redirect.permanent(url).perform(event, context);
         }

         else if (event instanceof OutboundRewrite)
         {
            Substitute.with(url).perform(event, context);
         }
      }
   }

   private abstract static class RemoveTrailingSlash extends TrailingSlash
   {
      @Override
      public boolean evaluate(final Rewrite event, final EvaluationContext context)
      {
         if (event instanceof HttpServletRewrite)
         {
            return (((HttpServletRewrite) event).getAddress().getPath().endsWith("/"));
         }
         return false;
      }

      @Override
      public void perform(final Rewrite event, final EvaluationContext context)
      {
         String path = ((HttpServletRewrite) event).getAddress().getPath();
         String url = path.substring(0, path.length() - 1);
         if (event instanceof InboundRewrite)
         {
            Redirect.permanent(url).perform(event, context);
         }

         else if (event instanceof OutboundRewrite)
         {
            Substitute.with(url).perform(event, context);
         }
      }
   }

   @Override
   public String getId()
   {
      return id;
   }

   public abstract String toString();
}
