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

import org.ocpsoft.rewrite.bind.Evaluation;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.Constraint;
import org.ocpsoft.rewrite.param.Transposition;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class RequestConstraintTransformTestProvider extends HttpConfigurationProvider
{
   private final Constraint<String> uppercaseOnly = new Constraint<String>() {
      @Override
      public boolean isSatisfiedBy(Rewrite event, EvaluationContext context, String value)
      {
         return value.matches("[A-Z]+");
      }
   };

   private final Transposition<String> toLowercase = new Transposition<String>() {
      @Override
      public String transpose(Rewrite event, EvaluationContext context, String value)
      {
         return value.toLowerCase();
      }
   };

   @Override
   public int priority()
   {
      return 0;
   }

   @Override
   public Configuration getConfiguration(final ServletContext context)
   {
      Configuration config = ConfigurationBuilder
               .begin()

               .addRule()
               .when(Direction.isOutbound().and(Path.matches("/outbound/{3}")))
               .perform(Substitute.with("/outbound/{3}"))
               .where("3").transposedBy(toLowercase)

               .addRule()
               .when(Path.matches("/constraint/{1}/{2}"))
               .perform(new HttpOperation() {

                  @Override
                  public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
                  {
                     String one = ((String) Evaluation.property("1").retrieve(event, context));
                     String two = ((String) Evaluation.property("2").retrieve(event, context));
                     String three = event.getResponse().encodeRedirectURL(event.getContextPath() + "/outbound/THREE");

                     Response.addHeader("one", one).perform(event, context);
                     Response.addHeader("two", two).perform(event, context);
                     Response.addHeader("three", three).perform(event, context);

                     SendStatus.code(211).perform(event, context);
                  }
               })
               .where("1").constrainedBy(uppercaseOnly).where("2")
               .constrainedBy(uppercaseOnly).transposedBy(toLowercase);

      return config;
   }
}
