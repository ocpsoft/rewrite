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
package org.ocpsoft.rewrite.annotation.handler;

import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.annotation.Matches;
import org.ocpsoft.rewrite.annotation.api.FieldContext;
import org.ocpsoft.rewrite.annotation.api.HandlerChain;
import org.ocpsoft.rewrite.annotation.spi.FieldAnnotationHandler;
import org.ocpsoft.rewrite.param.ParameterBuilder;
import org.ocpsoft.rewrite.param.RegexConstraint;

public class MatchesHandler extends FieldAnnotationHandler<Matches>
{
   private final Logger log = Logger.getLogger(MatchesHandler.class);

   @Override
   public Class<Matches> handles()
   {
      return Matches.class;
   }

   @Override
   public int priority()
   {
      return HandlerWeights.WEIGHT_TYPE_ENRICHING;
   }

   @Override
   public void process(FieldContext context, Matches annotation, HandlerChain chain)
   {

      // obtain the parameter for the current field
      ParameterBuilder<?> parameterBuilder =
               (ParameterBuilder<?>) context.get(ParameterBuilder.class);
      if (parameterBuilder == null) {
         throw new IllegalStateException("Cound not find any binding for field: " +
                  context.getJavaField().getName());
      }

      // add a corresponding RegexConstraint
      String expr = annotation.value();
      parameterBuilder.constrainedBy(new RegexConstraint(expr));

      if (log.isTraceEnabled()) {
         log.trace("Parameter [{}] has been constrained by [{}]", parameterBuilder.getName(), expr);
      }

      // proceed with the chain
      chain.proceed();

   }

}
