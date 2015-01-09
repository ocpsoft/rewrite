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
package org.ocpsoft.rewrite.spi;

import java.util.Set;

import org.ocpsoft.common.pattern.Weighted;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.Parameter;

/**
 * SPI for providing pre-configured global {@link Parameter} types.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface GlobalParameterProvider extends Weighted
{
   /**
    * Return the {@link Set} of {@link Parameter} types to be added to the global {@link Configuration}.
    */
   public Set<Parameter<?>> getParameters();

   /**
    * Validate the given {@link Parameter}. Only {@link Parameter} instances that were provided by this object should be
    * passed to this method.
    */
   public boolean isValid(Rewrite event, EvaluationContext context, Parameter<?> param, String value);

   /**
    * Return <code>true</code> if the given {@link Parameter} supports value submission. Only {@link Parameter}
    * instances that were provided by this object should be passed to this method.
    */
   public boolean supportsSubmission(Rewrite event, EvaluationContext context, Parameter<?> param);
}
