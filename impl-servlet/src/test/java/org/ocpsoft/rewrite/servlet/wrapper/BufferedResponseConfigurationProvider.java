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
package org.ocpsoft.rewrite.servlet.wrapper;

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.HttpOperation;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.Response;
import org.ocpsoft.rewrite.servlet.config.rule.Join;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.servlet.impl.HttpRewriteWrappedResponse;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class BufferedResponseConfigurationProvider extends HttpConfigurationProvider
{
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

               /*
                * Test buffered
                */
               .defineRule()
               .when(Path.matches("/index.html"))
               .perform(Response.withOutputBufferedBy(new BufferedResponseToLowercase1(),
                        new BufferedResponseToLowercase2()))

               /*
                * Test unbuffered. Use a Join to perform a forward so we know buffering would have been activated.
                */
               .addRule(Join.path("/unbuffered").to("/other.html"))
               .defineRule().when(Path.matches("/other.html"))
               .perform(new HttpOperation() {
                  @Override
                  public void performHttp(HttpServletRewrite event, EvaluationContext context)
                  {
                     if (HttpRewriteWrappedResponse.getInstance(event.getRequest()).isBufferingActive())
                     {
                        throw new IllegalStateException("Buffering should not be active.");
                     }
                     else
                     {
                        Response.setCode(201).perform(event, context);
                     }
                  }
               });

      return config;
   }
}
