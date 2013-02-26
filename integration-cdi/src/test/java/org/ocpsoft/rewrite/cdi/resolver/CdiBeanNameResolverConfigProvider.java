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
package org.ocpsoft.rewrite.cdi.resolver;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Invoke;
import org.ocpsoft.rewrite.el.El;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.Redirect;
import org.ocpsoft.rewrite.servlet.config.SendStatus;

/**
 * @author Christian Kaltepoth
 */
public class CdiBeanNameResolverConfigProvider extends HttpConfigurationProvider
{

   @Override
   public Configuration getConfiguration(final ServletContext context)
   {

      try {

         Field nameField = CdiBeanNameResolverBean.class.getDeclaredField("name");
         Field uppercaseField = CdiBeanNameResolverBean.class.getDeclaredField("uppercase");
         Method actionMethod = CdiBeanNameResolverBean.class.getMethod("action");

         return ConfigurationBuilder
                  .begin()
                  .addRule()
                  .when(Path.matches("/name/{name}"))
                  .perform(Invoke.binding(El.retrievalMethod(actionMethod))
                           .and(Redirect.permanent(context.getContextPath() + "/hello/{uppercase}")))

                  // FIXME this needs to bind to a dual retrieval/submission binding
                  .where("name").bindsTo(El.property(nameField))
                  .where("uppercase").bindsTo(El.property(uppercaseField))

                  .addRule()
                  .when(Path.matches("/hello/{name}"))
                  .perform(SendStatus.code(200));

      }
      catch (Exception e) {
         throw new IllegalStateException(e);
      }
   }

   @Override
   public int priority()
   {
      return 0;
   }

}
