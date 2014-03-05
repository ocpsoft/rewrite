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
package org.ocpsoft.rewrite.param;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * Used to verify or restrict values before they are converted via the {@link Converter} API.
 * 
 * @see {@link Parameter} {@link Transposition} {@link Validator} {@link Converter}
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface Constraint<T> extends ParameterConfigurator
{
   /**
    * Return <code>true</code> if this {@link Constraint} is satisfied by the given value; otherwise, return
    * <code>false</code>.
    */
   boolean isSatisfiedBy(Rewrite event, EvaluationContext context, T value);
}
