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

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

public class ForwardEncodingProvider extends HttpConfigurationProvider
{

   @Override
   public Configuration getConfiguration(final ServletContext context)
   {
      return ConfigurationBuilder.begin()

               .addRule()
               .when(Direction.isInbound().and(Path.matches("/forward/{param}")))
               .perform(Forward.to("/direct/{param}"))
               .where("param").matches(".*")

               .addRule()
               .when(Direction.isInbound().and(Path.matches("/direct/debug/{name}")))
               .perform(new HttpOperation() {
                  @Override
                  public void performHttp(HttpServletRewrite event, EvaluationContext context)
                  {

                     String requestURI = event.getRequest().getRequestURI();
                     Response.write("getRequestURI: [" + requestURI + "]").perform(event, context);

                     String inboundPath = event.getInboundAddress().getPath();
                     Response.write("inboundAddressPath: [" + inboundPath + "]").perform(event, context);

                     Response.complete().perform(event, context);

                  }
               });

   }

   @Override
   public int priority()
   {
      return 0;
   }

}
