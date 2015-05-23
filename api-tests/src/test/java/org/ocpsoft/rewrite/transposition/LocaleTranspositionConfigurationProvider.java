/*
 * Copyright 2014 Université de Montréal
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
package org.ocpsoft.rewrite.transposition;

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.HttpOperation;
import org.ocpsoft.rewrite.servlet.config.SendStatus;
import org.ocpsoft.rewrite.servlet.config.rule.Join;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * 
 * @author Christian Gendreau
 * 
 */
public class LocaleTranspositionConfigurationProvider extends HttpConfigurationProvider
{
   @Override
   public int priority()
   {
      return 0;
   }

   @Override
   public Configuration getConfiguration(final ServletContext context)
   {
      Configuration config = ConfigurationBuilder.begin()
               .addRule(Join.path("/{lang}/{path}.xhtml").to("/{path}.xhtml"))
               .where("path").transposedBy(LocaleTransposition.bundle("bundle", "lang"))

               .addRule(Join.path("/{lang}/{path}").to("/{path}"))
               .where("path").configuredBy(LocaleTransposition.bundle("bundle", "lang"))

               .addRule(Join.path("/{lang}/{path}/transposition_only").to("/{path}"))
               .where("path").transposedBy(LocaleTransposition.bundle("bundle", "lang"))

               .addRule(Join.path("/{lang}/{path}/transposition_failed_1").to("/{path}"))
               .where("path").transposedBy(LocaleTransposition.bundle("bundle", "lang").onTranspositionFailed(
                        new HttpOperation()
                        {
                           @Override
                           public void performHttp(HttpServletRewrite event, EvaluationContext context)
                           {
                              SendStatus.code(201).performHttp(event, context);
                           }
                        }))

               .addRule(Join.path("/{lang}/{path}/transposition_failed_2").to("/{path}"))
               .where("path").transposedBy(LocaleTransposition.bundle("bundle", "lang").onTranspositionFailed(
                        new HttpOperation()
                        {
                           @Override
                           public void performHttp(HttpServletRewrite event, EvaluationContext context)
                           {
                              SendStatus.code(202).performHttp(event, context);
                           }
                        }));
      return config;
   }
}