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
package org.ocpsoft.rewrite.faces.resolver;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.faces.event.PhaseId;
import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.el.El;
import org.ocpsoft.rewrite.faces.config.PhaseAction;
import org.ocpsoft.rewrite.faces.config.PhaseBinding;
import org.ocpsoft.rewrite.servlet.config.Forward;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.Path;

/**
 * @author Christian Kaltepoth
 */
public class FacesBeanNameResolverConfigProvider extends HttpConfigurationProvider
{

   @Override
   public Configuration getConfiguration(final ServletContext context)
   {

      try {

         Field nameField = FacesBeanNameResolverBean.class.getDeclaredField("name");
         Method actionMethod = FacesBeanNameResolverBean.class.getMethod("action");

         return ConfigurationBuilder.begin()
                  .addRule()
                  .when(Path.matches("/name/{name}"))
                  .perform(PhaseAction.retrieveFrom(El.retrievalMethod(actionMethod)).after(PhaseId.RESTORE_VIEW)
                           .and(Forward.to("/resolver.xhtml")))
                  .where("name").bindsTo(PhaseBinding.to(El.property(nameField)).after(PhaseId.RESTORE_VIEW));
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
