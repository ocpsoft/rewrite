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
import org.ocpsoft.rewrite.servlet.config.rule.Join;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class JoinRequestBindingConfigurationProvider extends HttpConfigurationProvider
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
                * Test Request Binding
                */
               .addRule(Join.path("/rb/{p1}").to("/rb.xhtml"))

               .addRule()
               .when(Path.matches("/rb.xhtml").and(RequestParameter.exists("p1")))
               .perform(SendStatus.code(201))

               /*
                * Test without Request Binding
                */
               .addRule(Join.pathNonBinding("/norb/{p1}").to("/norb.xhtml"))

               .addRule()
               .when(Path.matches("/norb.xhtml").andNot(RequestParameter.exists("p1")))
               .perform(SendStatus.code(202));

      return config;
   }
}
