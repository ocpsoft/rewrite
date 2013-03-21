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
package org.ocpsoft.rewrite.transform;

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.Forward;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.Path;

/**
 * 
 * {@link ConfigurationProvider} for {@link TransformPipelineTest}.
 * 
 * @author Christian Kaltepoth
 * 
 */
public class TransformPipelineTestProvider extends HttpConfigurationProvider
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

               // one single transformer
               .addRule()
               .when(Path.matches("/{basename}.one"))
               .perform(Forward.to("/{basename}.txt").and(Transform.with(new FooBarTransformer())))

               // multiple transformers
               .addRule()
               .when(Path.matches("/{basename}.two"))
               .perform(Forward.to("/{basename}.txt").and(
                        Transform.with(new FooBarTransformer()).then(new UppercaseTransformer())))

      ;
   }

}
