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

import java.util.Collection;

import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.rewrite.annotation.RewriteConfiguration;
import org.ocpsoft.rewrite.annotation.api.ClassContext;
import org.ocpsoft.rewrite.annotation.api.HandlerChain;
import org.ocpsoft.rewrite.annotation.spi.AnnotationHandler;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.config.ConfigurationRuleBuilder;
import org.ocpsoft.rewrite.config.RelocatableRule;
import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.exception.RewriteException;

public class RewriteConfigurationHandler implements AnnotationHandler<RewriteConfiguration>
{

   @Override
   public Class<RewriteConfiguration> handles()
   {
      return RewriteConfiguration.class;
   }

   @Override
   public int priority()
   {
      return HandlerWeights.WEIGHT_TYPE_STRUCTURAL;
   }

   @Override
   @SuppressWarnings({ "unchecked", "rawtypes" })
   public void process(ClassContext context, RewriteConfiguration annotation, HandlerChain chain)
   {
      Class<?> type = context.getJavaClass();
      if (ConfigurationProvider.class.isAssignableFrom(type) && !type.isInterface() && !type.isAnnotation()
               && !type.isArray())
      {
         Class<ConfigurationProvider<?>> providerType = (Class<ConfigurationProvider<?>>) type;
         Collection<ConfigurationProvider<?>> enriched = ServiceLoader.loadEnriched(providerType);
         if (enriched.size() == 1)
         {
            ConfigurationProvider provider = enriched.iterator().next();
            Object configurationContext = context.get(type);
            if (provider.handles(configurationContext))
            {
               Configuration config = provider.getConfiguration(configurationContext);
               if (config != null)
               {
                  for (Rule rule : config.getRules())
                  {
                     ConfigurationRuleBuilder ruleBuilder = context.getConfigurationBuilder().addRule(rule);
                     if (rule instanceof RelocatableRule && ((RelocatableRule) rule).isRelocated())
                        ruleBuilder.withPriority(((RelocatableRule) rule).priority());
                     else
                        ruleBuilder.withPriority(provider.priority());
                  }
               }
            }
            else
               throw new RewriteException("@" + RewriteConfiguration.class.getSimpleName() + " type [" + type.getName()
                        + "] cannot handle context of type [" + configurationContext + "]");
         }
         else if (enriched.size() < 1)
            throw new RewriteException("No service of type [" + type.getName()
                     + "] was found while loading configuration.");
         else if (enriched.size() > 1)
            throw new RewriteException("More than one service of type [" + type.getName()
                     + "] was found while loading configuration.");
      }
      else
      {
         throw new RewriteException("Class [" + type.getName() + "] annotated with @"
                  + RewriteConfiguration.class.getSimpleName()
                  + " must implement [" + ConfigurationProvider.class.getName() + "]");
      }
      chain.proceed();
   }
}
