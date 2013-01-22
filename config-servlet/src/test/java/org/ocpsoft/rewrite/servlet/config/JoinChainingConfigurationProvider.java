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

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.config.rule.Join;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public class JoinChainingConfigurationProvider extends HttpConfigurationProvider
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

               // Chaining explicitly enabled
               .addRule(Join.path("/chain").to("/1").withChaining())
               .addRule(Join.path("/chain-nochain").to("/nochain").withChaining())

               // Chaining disabled
               .addRule(Join.path("/nochain-many").to("/nochain"))
               .addRule(Join.path("/nochain").to("/1"))
               .perform(new HttpOperation() {

                  @Override
                  public void performHttp(HttpServletRewrite event, EvaluationContext context)
                  {
                     Response.addHeader("No-Chain", "true").perform(event, context);
                  }
               })

               // Chaining re-enabled internally
               .addRule(Join.path("/chain-from-servlet").to("/servlet"))

               // Target
               .addRule(Join.path("/1").to("/target"))

               .addRule().when(Path.matches("/target"))
               .perform(new HttpOperation() {

                  @Override
                  public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
                  {
                     SendStatus.code(201).perform(event, context);
                  }
               });

      return config;
   }
}
