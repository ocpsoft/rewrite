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
package com.ocpsoft.rewrite.spi;

import com.ocpsoft.common.pattern.Specialized;
import com.ocpsoft.common.pattern.Weighted;
import com.ocpsoft.rewrite.bind.Binding;
import com.ocpsoft.rewrite.config.Invoke;
import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.event.Rewrite;

/**
 * Handles return values from {@link Binding} results obtained during an {@link Invoke} operation.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface InvocationResultHandler extends Weighted, Specialized<Object>
{
   /**
    * Handle return value from {@link Binding} result obtained during an {@link Invoke} operation.
    */
   void handle(Rewrite event, EvaluationContext context, Object result);
}
