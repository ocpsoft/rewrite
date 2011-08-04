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
package com.ocpsoft.rewrite.servlet.http.impl;

import com.ocpsoft.rewrite.config.Configuration;
import com.ocpsoft.rewrite.config.ConfigurationLoader;
import com.ocpsoft.rewrite.config.Operation;
import com.ocpsoft.rewrite.config.Rule;
import com.ocpsoft.rewrite.servlet.event.BaseRewrite.Flow;
import com.ocpsoft.rewrite.servlet.http.HttpRewriteProvider;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import com.ocpsoft.rewrite.servlet.impl.EvaluationContextImpl;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class DefaultHttpRewriteProvider extends HttpRewriteProvider
{
   @Override
   public void rewrite(final HttpServletRewrite event)
   {
      Configuration loader = ConfigurationLoader.loadConfiguration(event.getRequest().getServletContext());
      for (Rule rule : loader.getRules()) {
         EvaluationContextImpl context = new EvaluationContextImpl();
         if (rule.getCondition().evaluate(event, context))
         {
            for (Operation operation : context.getPreOperations()) {
               operation.perform(event, context);
            }

            rule.getOperation().perform(event, context);

            for (Operation operation : context.getPostOperations()) {
               operation.perform(event, context);
            }

            if (event.getFlow().is(Flow.HANDLED))
            {
               break;
            }
         }
      }
   }

   @Override
   public int priority()
   {
      return 0;
   }
}
