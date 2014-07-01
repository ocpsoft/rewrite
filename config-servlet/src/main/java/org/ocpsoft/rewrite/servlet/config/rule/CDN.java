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

import java.util.LinkedHashSet;
import java.util.Set;

import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.ParameterizedPatternParser;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.Substitute;
import org.ocpsoft.urlbuilder.Address;

/**
 * A {@link Rule} that creates a bi-directional relationship between an externally facing {@link Address} and an
 * internal server resource {@link Address} for the purposes of using a Content Distribution Network.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class CDN implements Rule, CDNRelocate, Parameterized
{
   private String id;

   private Substitute location;
   private final Path resource;

   protected CDN(final String pattern)
   {
      this.resource = Path.matches(pattern);
   }

   /**
    * Create a {@link Rule} specifying the outward facing {@link Path} to which this {@link CDN} will apply. See
    * {@link Path} for additional configuration details.
    */
   public static CDNRelocate relocate(final String pattern)
   {
      return new CDN(pattern) {};
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
      return "CDN.relocate(\"" + resource.getExpression().getPattern() + "\").to(\""
               + location.getExpression().getPattern() + "\")";
   }

   public ParameterizedPatternParser getLocationExpression()
   {
      return location.getExpression();
   }

   public ParameterizedPatternParser getResourcExpression()
   {
      return resource.getExpression();
   }

   @Override
   public Set<String> getRequiredParameterNames()
   {
      Set<String> result = new LinkedHashSet<String>();
      result.addAll(resource.getRequiredParameterNames());
      result.addAll(location.getRequiredParameterNames());
      return result;
   }

   @Override
   public void setParameterStore(ParameterStore store)
   {
      resource.setParameterStore(store);
      location.setParameterStore(store);
   }

}
