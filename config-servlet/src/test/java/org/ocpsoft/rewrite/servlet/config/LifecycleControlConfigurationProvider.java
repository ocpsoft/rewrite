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

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public class LifecycleControlConfigurationProvider extends HttpConfigurationProvider
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

               .addRule().when(Path.matches("/abort")).perform(Lifecycle.abort())
               .addRule().when(Path.matches("/abort{path}"))
               .perform(SendStatus.code(400))
               .where("path").matches(".*")

               .addRule().when(Path.matches("/handle")).perform(Lifecycle.handled())
               .addRule().when(Path.matches("/handle{path}"))
               .perform(SendStatus.code(401))
               .where("path").matches(".*")

               .addRule().when(Path.matches("/proceed")).perform(Lifecycle.proceed())
               .addRule().when(Path.matches("/proceed{path}"))
               .perform(SendStatus.code(402))
               .where("path").matches(".*");

      return config;
   }
}
