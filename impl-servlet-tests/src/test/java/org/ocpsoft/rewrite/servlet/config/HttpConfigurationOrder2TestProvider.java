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
public class HttpConfigurationOrder2TestProvider extends HttpConfigurationProvider
{
   @Override
   public int priority()
   {
      /*
       * Priority of overall configuration is lower than default.
       */
      return 10;
   }

   @Override
   public Configuration getConfiguration(final ServletContext context)
   {
      return ConfigurationBuilder.begin()
               .addRule(Join.path("/").to("/index.html"))
               .addRule(Join.path("/login").to("/test.html"))
               .addRule(Join.path("/registration").to("/test.html"))
               .addRule(Join.path("/myProfile").to("/test.html"))
               .addRule()
               .when(Direction.isInbound().and(DispatchType.isRequest()))
               .perform(SendStatus.error(403));
   }
}
