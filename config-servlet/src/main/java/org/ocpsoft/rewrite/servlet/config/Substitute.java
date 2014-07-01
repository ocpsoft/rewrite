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
package org.ocpsoft.rewrite.servlet.config;

import java.util.Set;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.config.ConfigurationRuleParameterBuilder;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.InboundRewrite;
import org.ocpsoft.rewrite.event.OutboundRewrite;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.ParameterizedPattern;
import org.ocpsoft.rewrite.param.ParameterizedPatternBuilder;
import org.ocpsoft.rewrite.param.ParameterizedPatternParser;
import org.ocpsoft.rewrite.param.RegexParameterizedPatternParser;
import org.ocpsoft.rewrite.servlet.event.InboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.util.Transpositions;
import org.ocpsoft.urlbuilder.Address;
import org.ocpsoft.urlbuilder.AddressBuilder;

/**
 * An {@link Operation} responsible for substituting an inbound or outbound {@link Address} with a replacement. For
 * {@link InboundRewrite} events, this calls {@link InboundServletRewrite#forward(String)}, and for
 * {@link OutboundRewrite} events, this calls {@link HttpOutboundServletRewrite#setOutboundAddress(String)}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class Substitute extends HttpOperation implements Parameterized
{
   private final ParameterizedPatternParser location;

   private Substitute(final String location)
   {
      Assert.notNull(location, "Location must not be null.");
      this.location = new RegexParameterizedPatternParser("[^/]+", location);
   }

   /**
    * Substitute the current {@link Address} with the given location.
    * <p>
    * The given location may be parameterized:
    * <p>
    * INBOUND:<br/>
    * <code>
    *    /example/{param}.html <br>
    *    /css/{value}.css <br>
    *    ... 
    * </code>
    * <p>
    * OUTBOUND:<br/>
    * <code>
    *    www.example.com/path/file.html <br>
    *    www.example.com/path/{resource}.html <br>
    *    ... 
    * </code>
    * <p>
    * 
    * @param location {@link ParameterizedPattern} specifying the new {@link Address}.
    * 
    * @see {@link ConfigurationRuleParameterBuilder#where(String)}
    */
   public static Substitute with(final String location)
   {
      return new Substitute(location) {
         @Override
         public String toString()
         {
            return "Substitute.with(\"" + location + "\")";
         }
      };
   }

   @Override
   public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      if (event instanceof HttpInboundServletRewrite)
      {
         String target = location.getBuilder().build(event, context, Transpositions.encodePath());
         ((HttpInboundServletRewrite) event).forward(target);
      }
      else if (event instanceof HttpOutboundServletRewrite)
      {
         ParameterizedPatternBuilder builder = location.getBuilder();

         String target = builder.build(event, context, Transpositions.encodePath());
         if (((HttpOutboundServletRewrite) event).getOutboundAddress().getPath().startsWith(event.getContextPath())
                  && target.startsWith("/")
                  && !target.startsWith("//")
                  && !target.startsWith(event.getContextPath()))
         {
            target = event.getContextPath() + target;
         }
         ((HttpOutboundServletRewrite) event).setOutboundAddress(AddressBuilder.create(target));
      }
   }

   /**
    * Get the underlying {@link ParameterizedPatternParser} for this {@link Substitute}.
    */
   public ParameterizedPatternParser getExpression()
   {
      return location;
   }

   @Override
   public Set<String> getRequiredParameterNames()
   {
      return location.getRequiredParameterNames();
   }

   @Override
   public void setParameterStore(ParameterStore store)
   {
      location.setParameterStore(store);
   }

}
