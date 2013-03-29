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

import java.io.IOException;

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.HttpOperation;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.Response;
import org.ocpsoft.rewrite.servlet.config.SendStatus;
import org.ocpsoft.rewrite.servlet.config.rule.Join;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.servlet.impl.HttpRewriteWrappedResponse;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ResponseContentInterceptorTestProvider extends HttpConfigurationProvider
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
               .addRule()
               .when(Path.matches("/index.html"))
               .perform(Response.withOutputInterceptedBy(new ResponseToLowercase(),
                        new ResponseToLowercaseWord()))

               /*
                * Test unbuffered. Use a Join to perform a forward so we know buffering would have been activated.
                */
               .addRule(Join.path("/unbuffered").to("/unbuffered.html"))
               .addRule().when(Path.matches("/unbuffered.html"))
               .perform(new HttpOperation() {
                  @Override
                  public void performHttp(HttpServletRewrite event, EvaluationContext context)
                  {
                     if (HttpRewriteWrappedResponse.getCurrentInstance(event.getRequest())
                              .isResponseContentIntercepted())
                     {
                        throw new IllegalStateException("Buffering should not be active.");
                     }
                     else
                     {
                        Response.setStatus(201).perform(event, context);
                     }
                  }
               })

               /*
                * Test buffer failure constraints.
                */
               .addRule(Join.path("/bufferforward").to("/forward.html"))
               .addRule().when(Path.matches("/forward.html"))
               .perform(new HttpOperation() {
                  @Override
                  public void performHttp(HttpServletRewrite event, EvaluationContext context)
                  {
                     Response.withOutputInterceptedBy(new ResponseToLowercase()).perform(event, context);
                     Response.setStatus(202).perform(event, context);
                  }
               })

               .addRule().when(Path.matches("/bufferfail"))
               .perform(new HttpOperation() {
                  @Override
                  public void performHttp(HttpServletRewrite event, EvaluationContext context)
                  {
                     try {
                        event.getResponse().getOutputStream(); // cause buffers to lock
                        Response.withOutputInterceptedBy(new ResponseToLowercase()).perform(event, context);
                     }
                     catch (IllegalStateException e) {
                        SendStatus.error(503).perform(event, context);
                     }
                     catch (IOException e)
                     {
                        throw new RuntimeException(e);
                     }
                  }
               });

      return config;
   }
}
