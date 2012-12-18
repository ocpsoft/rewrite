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
import org.ocpsoft.rewrite.servlet.config.rule.Join;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.urlbuilder.Address;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class JoinConfigurationProvider extends HttpConfigurationProvider
{
   @Override
   public int priority()
   {
      return 0;
   }

   /**
    * <b>Inbound:</b><br>
    * -----------<br>
    * If URL matches pattern, forward to resource with parameters converted into request parameters.
    * <p>
    * <b>Outbound:</b><br>
    * -----------<br>
    * If URL matches resource, and parameters specified in pattern exist as query parameters, convert the URL to the
    * form specified by pattern.
    */
   @Override
   public Configuration getConfiguration(final ServletContext context)
   {
      Configuration config = ConfigurationBuilder.begin()

               /*
                * Set up our rule (This does the work.)
                */
               .addRule(Join.path("/p/{project}").to("/viewProject.xhtml"))

               /*
                * Now send a verification to our test case that the rule worked correctly.
                */
               .addRule().when(Direction.isInbound().and(Path.matches("/viewProject.xhtml"))
                        .and(RequestParameter.exists("project")))
               .perform(new HttpOperation() {

                  @Override
                  public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
                  {
                     String projectName = event.getRequest().getParameter("project");
                     String encodedURL = event.getResponse().encodeURL(
                              event.getContextPath() + "/viewProject.xhtml?project=" + projectName);

                     Response.addHeader("Project", projectName)
                              .and(Response.addHeader("Encoded-URL", encodedURL))
                              .and(SendStatus.code(203)).perform(event, context);
                  }
               })

               /*
                * Test Inbound Correction
                */
               .addRule(Join.path("/{p1}/{p2}").to("/list.xhtml").withInboundCorrection())

               .addRule()
               .when(Path.matches("/list.xhtml"))
               .perform(SendStatus.code(204))

               /*
                *  Test Inbound/Outbound Query Handling
                */
               .addRule(Join.path("/{id}/querypath/{other}/").to("/{id}-query.xhtml").withInboundCorrection())

               .addRule().when(Direction.isInbound().and(Path.matches("/{id}-query.xhtml")))
               .perform(new HttpOperation() {

                  @Override
                  public void performHttp(HttpServletRewrite event, EvaluationContext context)
                  {
                     Address address = event.getAddress();
                     Response.addHeader("InboundURL", address.getPath() + "?" + address.getQuery())
                              .and(Response.addHeader("OutboundURL", event.getResponse().encodeURL(
                                       "/12345-query.xhtml?foo=bar&other=cab&kitty=meow")))
                              .and(SendStatus.code(207))
                              .perform(event, context);
                  }
               });

      return config;
   }
}
