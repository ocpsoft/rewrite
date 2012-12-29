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
package org.ocpsoft.rewrite.showcase.bookstore.web;

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.Query;
import org.ocpsoft.rewrite.servlet.config.Redirect;
import org.ocpsoft.rewrite.servlet.config.rule.Join;

public class DemoConfigProvider extends HttpConfigurationProvider
{

   @Override
   public Configuration getConfiguration(ServletContext context)
   {
      return ConfigurationBuilder.begin()

               // initial redirect to /home
               .addRule()
               .when(Path.matches("/"))
               .perform(Redirect.temporary(context.getContextPath() + "/home"))

               // rule for keeping old URLs alive
               .addRule()
               .when(Path.matches("/book.php").and(Query.parameterExists("isbn")))
               .perform(Redirect.temporary(context.getContextPath() + "/book/{isbn}"))

               // the year page doesn't contain any Rewrite annotations and is configured here
               .addRule(Join.path("/year/{year}").to("/faces/year.xhtml"));

   }

   @Override
   public int priority()
   {
      return 0;
   }

}
