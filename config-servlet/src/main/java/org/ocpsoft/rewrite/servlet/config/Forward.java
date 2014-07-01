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

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.config.ConfigurationRuleParameterBuilder;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.ParameterizedPattern;
import org.ocpsoft.rewrite.param.ParameterizedPatternBuilder;
import org.ocpsoft.rewrite.param.RegexParameterizedPatternBuilder;
import org.ocpsoft.rewrite.servlet.event.InboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.util.ParseTools.CaptureType;
import org.ocpsoft.rewrite.util.Transpositions;
import org.ocpsoft.urlbuilder.Address;

/**
 * An {@link Operation} that forwards an inbound request to a configured internal resource {@link Address} via
 * {@link InboundServletRewrite#forward(String)}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Forward extends HttpOperation implements Parameterized
{
   private final ParameterizedPatternBuilder location;

   private Forward(final String location)
   {
      Assert.notNull(location, "Location must not be null.");
      this.location = new RegexParameterizedPatternBuilder(CaptureType.BRACE, "[^/]+", location);
   }

   /**
    * Create a new {@link Operation} that forwards the current request to the given location within the servlet
    * container. This does not change the browser {@link Address}, all processing is handled within the current
    * {@link HttpServletRequest}.
    * <p>
    * The given location may be parameterized:
    * <p>
    * <code>
    *    /example/{param}.html <br>
    *    /example/{value}/sub/{value2} <br>
    *    ... 
    * </code>
    * <p>
    * 
    * @param location {@link ParameterizedPattern} specifying the {@link Address} of the internal resource.
    * 
    * @see {@link ConfigurationRuleParameterBuilder#where(String)}
    *      {@link HttpServletRequest#getRequestDispatcher(String)}
    *      {@link RequestDispatcher#forward(javax.servlet.ServletRequest, javax.servlet.ServletResponse)}
    */
   public static Forward to(final String location)
   {
      return new Forward(location);
   }

   @Override
   public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      if (event instanceof HttpInboundServletRewrite)
      {
         String target = location.build(event, context, Transpositions.identity());
         ((HttpInboundServletRewrite) event).forward(target);
      }
   }

   @Override
   public String toString()
   {
      return "Forward.to(\"" + location.getPattern() + "\")";
   }

   public ParameterizedPatternBuilder getTargetExpression()
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
