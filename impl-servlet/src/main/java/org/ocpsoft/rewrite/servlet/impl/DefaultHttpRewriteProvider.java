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
package org.ocpsoft.rewrite.servlet.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.ServletContext;

import org.ocpsoft.common.services.NonEnriching;
import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.common.util.Iterators;
import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.bind.Bindings;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationLoader;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.context.RewriteState;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.ParameterValueStore;
import org.ocpsoft.rewrite.param.DefaultParameterValueStore;
import org.ocpsoft.rewrite.servlet.event.BaseRewrite.Flow;
import org.ocpsoft.rewrite.servlet.http.HttpRewriteProvider;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.spi.RuleCacheProvider;
import org.ocpsoft.rewrite.util.ServiceLogger;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public class DefaultHttpRewriteProvider extends HttpRewriteProvider implements NonEnriching
{
   private static Logger log = Logger.getLogger(DefaultHttpRewriteProvider.class);
   private volatile ConfigurationLoader loader;
   private volatile List<RuleCacheProvider> ruleCacheProviders;

   @Override
   @SuppressWarnings("unchecked")
   public void init(ServletContext context)
   {
      if (loader == null)
         synchronized (this) {
            if (loader == null)
               loader = ConfigurationLoader.create(context);
         }

      if (ruleCacheProviders == null)
         synchronized (this) {
            ruleCacheProviders = Iterators
                     .asList(ServiceLoader.load(RuleCacheProvider.class));

            ServiceLogger.logLoadedServices(log, RuleCacheProvider.class, ruleCacheProviders);
         }

      loader.loadConfiguration(context);

   }

   @Override
   public void rewriteHttp(final HttpServletRewrite event)
   {
      ServletContext servletContext = event.getServletContext();
      if (loader == null)
         synchronized (servletContext) {
            if (loader == null)
               loader = ConfigurationLoader.create(servletContext);
         }

      Configuration compiledConfiguration = loader.loadConfiguration(servletContext);
      List<Rule> rules = compiledConfiguration.getRules();

      final EvaluationContextImpl context = new EvaluationContextImpl();

      Object cacheKey = null;
      for (int i = 0; i < ruleCacheProviders.size(); i++) {
         RuleCacheProvider provider = ruleCacheProviders.get(i);

         cacheKey = provider.createKey(event, context);
         final List<Rule> list = provider.get(cacheKey);
         if (list != null && !list.isEmpty())
         {
            log.debug("Using cached ruleset for event [" + event + "] from provider [" + provider + "].");
            for (int j = 0; j < rules.size(); j++)
            {
               Rule rule = rules.get(j);

               context.clear();
               DefaultParameterValueStore values = new DefaultParameterValueStore();
               context.put(ParameterValueStore.class, values);
               context.setState(RewriteState.EVALUATING);

               if (rule.evaluate(event, context))
               {
                  if (handleBindings(event, context, values))
                  {
                     context.setState(RewriteState.PERFORMING);
                     log.debug("Rule [" + rule + "] matched and will be performed.");

                     List<Operation> preOperations = context.getPreOperations();
                     for (int k = 0; k < preOperations.size(); k++) {
                        preOperations.get(k).perform(event, context);
                     }

                     if (event.getFlow().is(Flow.HANDLED))
                     {
                        return;
                     }

                     rule.perform(event, context);

                     if (event.getFlow().is(Flow.HANDLED))
                     {
                        return;
                     }

                     List<Operation> postOperations = context.getPostOperations();
                     for (int k = 0; k < postOperations.size(); k++) {
                        postOperations.get(k).perform(event, context);
                     }

                     if (event.getFlow().is(Flow.HANDLED))
                     {
                        return;
                     }
                  }
               }
               else
               {
                  break;
               }
            }
         }
      }

      /*
       * Highly optimized loop - for performance reasons. Think before you change this!
       */
      List<Rule> cacheable = new ArrayList<Rule>();
      for (int i = 0; i < rules.size(); i++)
      {
         Rule rule = rules.get(i);

         context.clear();
         DefaultParameterValueStore values = new DefaultParameterValueStore();
         context.put(ParameterValueStore.class, values);

         context.setState(RewriteState.EVALUATING);
         if (rule.evaluate(event, context))
         {
            if (handleBindings(event, context, values))
            {
               context.setState(RewriteState.PERFORMING);
               log.debug("Rule [" + rule + "] matched and will be performed.");
               cacheable.add(rule);
               List<Operation> preOperations = context.getPreOperations();
               for (int k = 0; k < preOperations.size(); k++) {
                  preOperations.get(k).perform(event, context);
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

      if (!cacheable.isEmpty())
         for (int i = 0; i < ruleCacheProviders.size(); i++) {
            ruleCacheProviders.get(i).put(cacheKey, cacheable);
         }
   }

   private boolean handleBindings(final HttpServletRewrite event, final EvaluationContextImpl context,
            DefaultParameterValueStore values)
   {
      boolean result = true;
      ParameterStore store = (ParameterStore) context.get(ParameterStore.class);

      for (Entry<String, Parameter<?>> entry : store) {
         Parameter<?> parameter = entry.getValue();
         String value = values.get(parameter);

         if (!Bindings.enqueueSubmission(event, context, parameter, value))
         {
            result = false;
            break;
         }
      }
      return result;
   }

   @Override
   public int priority()
   {
      return 0;
   }
}
