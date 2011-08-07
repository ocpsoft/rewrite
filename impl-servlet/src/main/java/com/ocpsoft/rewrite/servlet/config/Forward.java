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

import com.ocpsoft.rewrite.EvaluationContext;
import com.ocpsoft.rewrite.servlet.config.parameters.Parameter;
import com.ocpsoft.rewrite.servlet.config.parameters.ParameterBinding;
import com.ocpsoft.rewrite.servlet.config.parameters.impl.CompiledPath;
import com.ocpsoft.rewrite.servlet.config.parameters.impl.ParameterizedHttpOperationBuilder;
import com.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import com.ocpsoft.rewrite.util.Assert;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Forward extends ParameterizedHttpOperation<ParameterizedHttpOperationBuilder>
{
   private final CompiledPath location;

   private Forward(final String location, final Parameter... params)
   {
      Assert.notNull(location, "Location must not be null.");
      this.location = new CompiledPath(this, location);

      if (params != null)
      {
         for (Parameter param : params) {
            if (this.location.getParameters().containsKey(param.getName()))
            {
               this.location.getParameters().put(param.getName(), param);
            }
         }
      }
   }

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
   public ParameterizedHttpOperationBuilder where(final String param)
   {
      return ParameterizedHttpOperationBuilder.create(this, location.getParameter(param));
   }

   @Override
   public ParameterizedHttpOperationBuilder where(final String param, final String pattern)
   {
      return where(param).matches(pattern);
   }

   @Override
   public ParameterizedHttpOperationBuilder where(final String param, final String pattern,
            final ParameterBinding binding)
   {
      return where(param, pattern).bindsTo(binding);
   }

   @Override
   public ParameterizedHttpOperationBuilder where(final String param, final ParameterBinding binding)
   {
      return where(param).bindsTo(binding);
   }
}
