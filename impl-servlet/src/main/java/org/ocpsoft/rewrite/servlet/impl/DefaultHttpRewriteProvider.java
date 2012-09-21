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
package org.ocpsoft.rewrite.servlet.impl;

import java.util.List;

import javax.servlet.ServletContext;

import org.ocpsoft.common.services.NonEnriching;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationLoader;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.servlet.event.BaseRewrite.Flow;
import org.ocpsoft.rewrite.servlet.http.HttpRewriteProvider;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class DefaultHttpRewriteProvider extends HttpRewriteProvider implements NonEnriching
{
   private volatile ConfigurationLoader loader;

   @Override
   public void init(ServletContext context)
   {
      if (loader == null)
         synchronized (this) {
            if (loader == null)
               loader = ConfigurationLoader.create(context);
         }

      loader.loadConfiguration(context);
   }

   @Override
   public void rewriteHttp(final HttpServletRewrite event)
   {
      ServletContext servletContext = event.getRequest().getServletContext();
      if (loader == null)
         synchronized (servletContext) {
            if (loader == null)
               loader = ConfigurationLoader.create(servletContext);
         }

      Configuration compiledConfiguration = loader.loadConfiguration(servletContext);
      List<Rule> rules = compiledConfiguration.getRules();

      /*
       * Highly optimized loop - for performance reasons. Think before you change this!
       */
      EvaluationContextImpl context = new EvaluationContextImpl();
      for (int i = 0; i < rules.size(); i++)
      {
         context.clear();
         Rule rule = rules.get(i);
         if (rule.evaluate(event, context))
         {

            List<Operation> preOperations = context.getPreOperations();
            for (int j = 0; j < preOperations.size(); j++) {
               preOperations.get(j).perform(event, context);
            }

            if (event.getFlow().is(Flow.HANDLED))
            {
               break;
            }

            rule.perform(event, context);

            if (event.getFlow().is(Flow.HANDLED))
            {
               break;
            }

            List<Operation> postOperations = context.getPostOperations();
            for (int k = 0; k < postOperations.size(); k++) {
               postOperations.get(k).perform(event, context);
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
