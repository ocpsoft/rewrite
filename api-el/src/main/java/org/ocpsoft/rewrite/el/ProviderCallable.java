/*
 * Copyright 2013 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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
package org.ocpsoft.rewrite.el;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.el.spi.ExpressionLanguageProvider;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * An object that defines a specific behavior to be executed on an {@link ExpressionLanguageProvider}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @param <RESULTTYPE> The callable return value type.
 */
interface ProviderCallable<RESULTTYPE>
{
   /**
    * Execute the behavior.
    */
   RESULTTYPE call(Rewrite event, EvaluationContext context, ExpressionLanguageProvider provider) throws Exception;

   /**
    * Get the EL expression on which this provider was performing.
    */
   String getExpression();
}
