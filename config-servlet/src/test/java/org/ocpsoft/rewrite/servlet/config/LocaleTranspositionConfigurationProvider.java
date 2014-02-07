/*
 * Copyright 2014 Université de Montréal
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
import org.ocpsoft.rewrite.servlet.config.rule.Join;
import org.ocpsoft.rewrite.servlet.config.transposition.LocaleTransposition;

/**
 * 
 * @author Christian Gendreau
 *
 */
public class LocaleTranspositionConfigurationProvider extends HttpConfigurationProvider
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
	               .addRule()
	               .when(Path.matches("/{lang}/{path}"))
	               .perform(Join.path("/{lang}/{path}").to("/{path}"))
	               .where("path").transposedBy(LocaleTransposition.bundle("lang", "bundle"));
	      return config;
	   }
}
