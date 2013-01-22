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

import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.ParameterizedPatternParser;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.Substitute;

/**
 * {@link org.ocpsoft.rewrite.config.Rule} that creates a bi-directional rewrite rule between an externally facing URL
 * and an internal server resource URL
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class CDN implements Rule, CDNRelocate
{
   private String id;

   private Substitute location;
   private final Path resource;

   protected CDN(final String pattern)
   {
      this.resource = Path.matches(pattern);
   }

   /**
    * The outward facing URL path to which this {@link CDN} will apply.
    */
   public static CDNRelocate relocate(final String pattern)
   {
      return new CDN(pattern);
   }

   @Override
   public CDN to(final String location)
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
         location.perform(event, context);
   }

   @Override
   public void otherwise(Rewrite event, EvaluationContext context)
   {}

   @Override
   public String getId()
   {
      return id;
   }

   /**
    * Set the id of this {@link Rule}
    */
   public void setId(String id)
   {
      this.id = id;
   }

   @Override
   public String toString()
   {
      return "Join [resource=" + resource + ", to=" + location + ", id=" + id + "]";
   }

   public ParameterizedPatternParser getLocationExpression()
   {
      return location.getTargetExpression();
   }

   public ParameterizedPatternParser getResourcExpression()
   {
      return resource.getPathExpression();
   }

}
