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
package com.ocpsoft.rewrite.servlet.config;

import java.util.List;

import com.ocpsoft.rewrite.EvaluationContext;
import com.ocpsoft.rewrite.config.ConditionBuilder;
import com.ocpsoft.rewrite.config.Rule;
import com.ocpsoft.rewrite.event.InboundRewrite;
import com.ocpsoft.rewrite.event.OutboundRewrite;
import com.ocpsoft.rewrite.event.Rewrite;

/**
 * {@link Rule} that creates a bi-directional rewrite rule between an externally facing URL and an internal server
 * resource URL
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class UrlMapping implements Rule
{
   private final String pattern;
   private String resource;

   public UrlMapping(final String pattern)
   {
      this.pattern = pattern;
   }

   public static UrlMapping pattern(final String pattern)
   {
      return new UrlMapping(pattern);
   }

   public UrlMapping resource(final String resource)
   {
      this.resource = resource;
      return this;
   }

   @Override
   public boolean evaluate(final Rewrite event, final EvaluationContext context)
   {
      Path path = Path.matches(pattern);
      if (event instanceof InboundRewrite)
      {
         path.withRequestBinding();
         return path.evaluate(event, context);
      }

      else if (event instanceof OutboundRewrite)
      {
         List<String> parameterNames = path.getPathExpression().getParameterNames();
         ConditionBuilder outbound = Path.matches(resource);
         for (String name : parameterNames) {
            outbound = outbound.and(QueryString.parameterExists(name));
         }
         return outbound.evaluate(event, context);
      }

      return false;
   }

   @Override
   public void perform(final Rewrite event, final EvaluationContext context)
   {
      if (event instanceof InboundRewrite)
      {
         Forward.to(resource).perform(event, context);
      }

      else if (event instanceof OutboundRewrite)
      {
         Substitute.with(pattern).perform(event, context);
      }
   }
}
