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

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.Evaluation;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.ParameterizedPatternBuilder;
import org.ocpsoft.rewrite.param.ParameterizedPatternParser;
import org.ocpsoft.rewrite.param.ParameterizedPatternParserParameter;
import org.ocpsoft.rewrite.param.RegexParameterizedPatternParser;
import org.ocpsoft.rewrite.param.Transformations;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.urlbuilder.AddressBuilder;

/**
 * Responsible for substituting inbound/outbound URLs with a replacement. For {@link org.ocpsoft.rewrite.event.InboundRewrite} events, this
 * {@link Operation} calls {@link HttpInboundServletRewrite#forward(String)}, and for {@link org.ocpsoft.rewrite.event.OutboundRewrite} events,
 * this method calls {@link HttpOutboundServletRewrite#setOutboundAddress(String)}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Substitute extends HttpOperation implements ISubstitute
{
   private final ParameterizedPatternParser location;
   private final ParameterStore<SubstituteParameter> parameters = new ParameterStore<SubstituteParameter>();

   private Substitute(final String location)
   {
      Assert.notNull(location, "Location must not be null.");
      this.location = new RegexParameterizedPatternParser("[^/]+", location);

      for (ParameterizedPatternParserParameter parameter : this.location.getParameterMap().values()) {
         where(parameter.getName()).bindsTo(Evaluation.property(parameter.getName()));
      }
   }

   /**
    * Substitute the current URL with the given location.
    * <p>
    * The given location may be parameterized using the following format:
    * <p>
    * <code>
    *    /example/{param} <br>
    *    /example/{value}/sub/{value2} <br>
    *    ... and so on
    * </code>
    * <p>
    * Parameters may be bound. By default, matching parameter values are extracted from bindings in the
    * {@link org.ocpsoft.rewrite.context.EvaluationContext}.
    * <p>
    * See also {@link #where(String)}
    */
   public static Substitute with(final String location)
   {
      return new Substitute(location);
   }

   @Override
   public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      if (event instanceof HttpInboundServletRewrite)
      {
         String target = location.getBuilder().build(event, context, parameters);
         ((HttpInboundServletRewrite) event).forward(target);
      }
      else if (event instanceof HttpOutboundServletRewrite)
      {
         ParameterizedPatternBuilder builder = location.getBuilder();
         for(SubstituteParameter param : parameters.values()) {
            builder.where(param.getName()).transformedBy(Transformations.encodePath());
         }
         String target = builder.build(event, context, parameters);
         if (((HttpOutboundServletRewrite) event).getOutboundResource().getPath().startsWith(event.getContextPath())
                  && target.startsWith("/")
                  && !target.startsWith(event.getContextPath()))
         {
            target = event.getContextPath() + target;
         }
         ((HttpOutboundServletRewrite) event).setOutboundAddress(AddressBuilder.create(target));
      }
   }

   @Override
   public SubstituteParameter where(final String param)
   {
      return parameters.where(param, new SubstituteParameter(this, location.getParameter(param)));
   }

   @Override
   public SubstituteParameter where(final String param, final Binding binding)
   {
      return where(param).bindsTo(binding);
   }

   @Override
   public ParameterizedPatternParser getTargetExpression()
   {
      return location;
   }

}
