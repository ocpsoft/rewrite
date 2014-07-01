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
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.mock.MockFailedBinding;
import org.ocpsoft.rewrite.servlet.config.bind.RequestBinding;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public class ParameterConfigurationProvider extends HttpConfigurationProvider
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

               /*
                * Handle a request with Path parameter binding
                */
               .addRule()
               .when(Direction.isInbound().and(Path.matches("/{user}/order/{oid}")))
               .perform(new HttpOperation() {
                  @Override
                  public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
                  {
                     Response.addHeader("User-Name", event.getRequest().getParameter("uname")).perform(event, context);
                     Response.addHeader("Order-ID", event.getRequest().getParameter("oid")).perform(event, context);
                  }
               }.and(SendStatus.code(200)))
               .where("user").matches("[a-zA-Z]+").bindsTo(RequestBinding.parameter("uname"))
               .where("oid").matches("[0-9]+").bindsTo(RequestBinding.parameter("oid"))

               /*
                * Forward a request to another resource
                */
               .addRule()
               .when(Direction.isInbound().and(Path.matches("/p/{project}/story/{id}")))
               .perform(Forward.to("/viewProject?p={project}&id={id}"))
               .where("project").matches("[a-zA-Z]+")
               .where("id").matches("[0-9]+")

               /*
                * Handle the forwarded request
                */
               .addRule()
               .when(Direction.isInbound().and(Path.matches("/viewProject"))
                        .and(RequestParameter.exists("p"))
                        .andNot(RequestParameter.exists("project"))
                        .and(RequestParameter.exists("id")))
               .perform(SendStatus.code(200))

               /*
                * Handle a request that fails to bind
                */
               .addRule()
               .when(Direction.isInbound().and(Path.matches("/{user}/profile")))
               .perform(new HttpOperation() {
                  @Override
                  public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
                  {
                     Response.addHeader("User-Name", event.getRequest().getParameter("uname")).perform(event, context);
                     Response.addHeader("Order-ID", event.getRequest().getParameter("oid")).perform(event, context);
                  }
               }.and(SendStatus.code(200)))
               .where("user").matches("[a-zA-Z]+").bindsTo(new MockFailedBinding());

      return config;
   }
}
