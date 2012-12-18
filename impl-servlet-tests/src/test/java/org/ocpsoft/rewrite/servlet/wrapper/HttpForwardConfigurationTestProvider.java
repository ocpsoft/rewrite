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
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.servlet.config.Forward;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.*;
import org.ocpsoft.rewrite.servlet.config.SendStatus;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class HttpForwardConfigurationTestProvider extends HttpConfigurationProvider
{
   @Override
   public int priority()
   {
      return 0;
   }

   @Override
   public Configuration getConfiguration(final ServletContext context)
   {
      return ConfigurationBuilder.begin()
               .addRule()
               .when(Direction.isInbound().and(Path.matches("/forward")).and(RequestParameter.exists("foo")))
               .perform(Forward.to("/forward2?baz=cab"))

               .addRule()
               .when(Direction.isInbound()
                        .and(Path.matches("/forward2"))
                        .and(RequestParameter.exists("foo"))
                        .and(RequestParameter.exists("baz")))
               .perform(SendStatus.code(200))

               .addRule()
               .when(Direction.isInbound()
                        .and(Path.matches("/forward-fail"))
                        .and(RequestParameter.exists("foo")))
               .perform(Forward.to("/forward2"))

               // JSP Forward
               .addRule()
               .when(Direction.isInbound()
                        .and(Path.matches("/from-jsp"))
                        .and(RequestParameter.matches("test", "123")))
               .perform(SendStatus.code(201));
   }
}
