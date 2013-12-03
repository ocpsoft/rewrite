/*
 * Copyright 2012 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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

import java.io.IOException;

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.config.rule.Join;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

public class CommittedResponseProvider extends HttpConfigurationProvider
{

   @Override
   public Configuration getConfiguration(final ServletContext context)
   {
      return ConfigurationBuilder.begin()

               /*
                * This rule simulates a filter that has been configured to execute BEFORE the RewriteFilter. 
                * Lets assume this filter intercepts the response and sends a redirect, but still calls
                * `chain.doFilter(...)`.
                */
               .addRule()
               .when(Direction.isInbound().and(Path.matches("/path{*}")))
               .perform(new HttpOperation() {
                  @Override
                  public void performHttp(HttpServletRewrite event, EvaluationContext context)
                  {
                     try {
                        event.getResponse().sendRedirect(event.getContextPath() + "/redirected.txt");
                     }
                     catch (IOException e) {
                        throw new IllegalStateException(e);
                     }
                  }
               })

               /*
                * This is the first _real_ Rewrite rule that matches and which forwards 
                * the request to some other resource.
                */
               .addRule(Join.path("/path").to("/incorrect.txt"))

               /*
                * This prevents Rewrite from attempting to pass a committed response to the underlying application. 
                */
               .addRule()
               .when(Direction.isInbound().and(Path.matches("/path-handled")).and(Response.isCommitted()))
               .perform(Lifecycle.abort())

      ;

   }

   @Override
   public int priority()
   {
      return 0;
   }

}
