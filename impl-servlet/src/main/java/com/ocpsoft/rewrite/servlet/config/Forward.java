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

import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import com.ocpsoft.common.util.Assert;
import com.ocpsoft.rewrite.bind.Binding;
import com.ocpsoft.rewrite.bind.Evaluation;
import com.ocpsoft.rewrite.bind.ParameterizedPattern;
import com.ocpsoft.rewrite.bind.RegexOperationParameterBuilder;
import com.ocpsoft.rewrite.bind.parse.CaptureType;
import com.ocpsoft.rewrite.config.Operation;
import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.param.OperationParameterBuilder;
import com.ocpsoft.rewrite.param.Parameter;
import com.ocpsoft.rewrite.param.ParameterizedOperation;
import com.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * An {@link Operation} that performs forwards via {@link HttpInboundServletRewrite#forward(String)}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Forward extends HttpOperation implements
         ParameterizedOperation<OperationParameterBuilder<RegexOperationParameterBuilder, String>, String>
{
   private final ParameterizedPattern location;

   private Forward(final String location)
   {
      Assert.notNull(location, "Location must not be null.");
      this.location = new ParameterizedPattern(CaptureType.BRACE, "[^/]+", location);

      for (Parameter<String> parameter : this.location.getParameters().values()) {
         parameter.bindsTo(Evaluation.property(parameter.getName()));
      }
   }

   /**
    * Forward the current request to the given location within the servlet container. This does not change the browser
    * {@link URL}, all processing is handled within the current {@link HttpServletRequest}.
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
    * {@link EvaluationContext}.
    * <p>
    * See also {@link #where(String)}
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
         String target = location.build(event, context);
         ((HttpInboundServletRewrite) event).forward(target);
      }
   }

   @Override
   public RegexOperationParameterBuilder where(final String param)
   {
      return new RegexOperationParameterBuilder(this, location.getParameter(param));
   }

   @Override
   public RegexOperationParameterBuilder where(final String param, final String pattern)
   {
      return where(param).matches(pattern);
   }

   @Override
   public RegexOperationParameterBuilder where(final String param, final String pattern,
            final Binding binding)
   {
      return where(param, pattern).bindsTo(binding);
   }

   @Override
   public RegexOperationParameterBuilder where(final String param, final Binding binding)
   {
      return where(param).bindsTo(binding);
   }

   @Override
   public String toString()
   {
      return location.toString();
   }
}
