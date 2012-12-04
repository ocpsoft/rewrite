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
public class RequestQueryStringConfigurationProvider extends HttpConfigurationProvider
{
   @Override
   public int priority()
   {
      return 0;
   }

   /**
    * <b>Outbound:</b><br>
    * -----------<br>
    * If URL matches path convert the URL to the form specified by resource.
    */
   @Override
   public Configuration getConfiguration(final ServletContext context)
   {
      Configuration config = ConfigurationBuilder
               .begin()

               /*
                * Set up our rule (This does the work.)
                */
               .addRule()
               .when(Path.matches("/something")
                        .and(Query.matches("")))
               .perform(SendStatus.code(209))

               /*
                * #1: Test for correct handling of & characters
                */
               .addRule()
               .when(Path.matches("/ampersand").and(Query.valueExists("foo&bar")))
               .perform(SendStatus.code(209))

               /*
                * #2: Shows that requests to /ampersand?param=foo%26bar are not parsed correctly
                */
               .addRule()
               .when(Path.matches("/ampersand").and(Query.parameterExists("bar")))
               .perform(SendStatus.code(210));

      return config;
   }
}
