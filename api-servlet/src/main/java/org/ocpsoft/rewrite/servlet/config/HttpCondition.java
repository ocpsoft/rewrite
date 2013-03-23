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

import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.config.DefaultConditionBuilder;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * A condition that only applies to {@link HttpServletRewrite} events.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class HttpCondition extends DefaultConditionBuilder
{
   /**
    * Evaluate this {@link Condition} against the given
    * {@link org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite} event. If this condition does not apply to the
    * given event, it must return <code>false</code>. If the condition applies and is satisfied, return
    * <code>true</code>.
    */
   public abstract boolean evaluateHttp(final HttpServletRewrite event, EvaluationContext context);

   @Override
   public final boolean evaluate(final Rewrite event, final EvaluationContext context)
   {
      if (event instanceof HttpServletRewrite)
      {
         return evaluateHttp((HttpServletRewrite) event, context);
      }
      return false;
   }

}