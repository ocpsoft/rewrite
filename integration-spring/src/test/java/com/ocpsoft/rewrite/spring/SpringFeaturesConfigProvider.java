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
package com.ocpsoft.rewrite.spring;

import javax.servlet.ServletContext;

import com.ocpsoft.rewrite.bind.El;
import com.ocpsoft.rewrite.config.Configuration;
import com.ocpsoft.rewrite.config.ConfigurationBuilder;
import com.ocpsoft.rewrite.config.Invoke;
import com.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import com.ocpsoft.rewrite.servlet.config.Path;
import com.ocpsoft.rewrite.servlet.config.Redirect;
import com.ocpsoft.rewrite.servlet.config.SendStatus;

/**
 * @author Christian Kaltepoth
 */
public class SpringFeaturesConfigProvider extends HttpConfigurationProvider
{

   @Override
   public Configuration getConfiguration(final ServletContext context)
   {
      return ConfigurationBuilder
               .begin()
               .defineRule()
               .when(Path.matches("/name-{name}")
                        .where("name").bindsTo(El.property("springFeaturesBean.name")))
               .perform(Invoke.binding(El.retrievalMethod("springFeaturesBean.action()"))
                        .and(Redirect.permanent(context.getContextPath() + "/hello/{name}")
                                 .where("name").bindsTo(El.property("springFeaturesBean.uppercase"))))

               .defineRule()
               .when(Path.matches("/hello/{name}"))
               .perform(SendStatus.code(200));
   }

   @Override
   public int priority()
   {
      return 0;
   }

}
