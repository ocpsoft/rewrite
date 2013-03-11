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
import org.ocpsoft.rewrite.config.Or;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ResourceTestProvider extends HttpConfigurationProvider
{

   @Override
   public int priority()
   {
      return 0;
   }

   @Override
   public Configuration getConfiguration(final ServletContext context)
   {

      return ConfigurationBuilder
               .begin()

               .addRule()
               .when(Or.any(Path.matches("/exists.txt"), Path.matches("/missing.txt"))
                        .and(Resource.exists("/{file}.txt")))
               .perform(SendStatus.code(210))

               .addRule()
               .when(Or.any(Path.matches("/{file}.css"), Path.matches("/file.bah")).and(
                        Resource.exists("/{file}.bah")))
               .perform(SendStatus.code(211));

   }
}
