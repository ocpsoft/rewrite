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
package com.ocpsoft.rewrite.cdi.bridge;

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
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class CdiServiceEnricherTestConfigProvider extends HttpConfigurationProvider
{

   @Override
   public Configuration getConfiguration(final ServletContext context)
   {
      return ConfigurationBuilder
               .begin()
               .defineRule()
               .when(Path.matches("/{one}/{two}")
                        .where("one").bindsTo(El.property("bindingBean.one"))
                        .where("two").matches("[0-9]+").bindsTo(El.property("bindingBean.two")))
               .perform(Invoke.binding(El.retrievalMethod("bindingBean.action()"))
                        .and(Redirect.permanent(context.getContextPath() + "/{one}/{two}")
                                 .where("one").bindsTo(El.property("bindingBean.two"))
                                 .where("two").bindsTo(El.property("bindingBean.one"))))

               .defineRule()
               .when(Path.matches("/2/one"))
               .perform(SendStatus.code(200));
   }

   @Override
   public int priority()
   {
      return 0;
   }

}
