package org.ocpsoft.rewrite.adf;

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

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.QueryString;
import org.ocpsoft.rewrite.servlet.config.SendStatus;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class MockADFConfigurationProvider extends HttpConfigurationProvider
{
   @Override
   public int priority()
   {
      // Notice this priority occurs after ADFConfigProvider
      return 0;
   }

   @Override
   public Configuration getConfiguration(final ServletContext context)
   {

      // TODO implement a response confirming that the request was correctly handled by ADFConfigurationProvider

      Configuration config = ConfigurationBuilder.begin()
               .addRule()
               .when(QueryString.parameterExists("adf"))
               .perform(SendStatus.code(203));

      return config;
   }
}
