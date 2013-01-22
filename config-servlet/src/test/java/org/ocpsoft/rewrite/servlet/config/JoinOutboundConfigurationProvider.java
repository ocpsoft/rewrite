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
import org.ocpsoft.rewrite.servlet.config.rule.Join;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public class JoinOutboundConfigurationProvider extends HttpConfigurationProvider
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
                * Valid case: Outbound URL is rewritten because it matches the
                * constraints on Join parameters
                */
               .addRule()
               .when(Direction.isInbound().and(Path.matches("/valid_outbound")))
               .perform(Redirect.temporary(context.getContextPath() + "/success?1=444&2=bar"))

               .addRule(Join.pathNonBinding("/{1}/{2}").to("/success"))
               .where("1").matches("\\d+")
               .where("2").matches("[a-z]+")

               .addRule()
               .when(Direction.isInbound().and(
                        Path.matches("/success"))
                        .andNot(Query.parameterExists("1"))
                        .andNot(Query.parameterExists("3")))
               .perform(SendStatus.code(200))

               /*
                * Invalid case: outbound redirect URL should not be rewritten since
                * it does not create a valid URL for the matching Join (above)
                */
               .addRule()
               .when(Direction.isInbound().and(Path.matches("/invalid_outbound")))
               .perform(Redirect.temporary(context.getContextPath() + "/norewrite?3=bar&4=444"))

               .addRule(Join.pathNonBinding("/{3}/{4}").to("/success"))
               .where("3").matches("\\d+")
               .where("4").matches("[a-z]+")

               .addRule()
               .when(Direction.isInbound().and(Path.matches("/norewrite")))
               .perform(SendStatus.code(201))

      ;

      return config;
   }
}
