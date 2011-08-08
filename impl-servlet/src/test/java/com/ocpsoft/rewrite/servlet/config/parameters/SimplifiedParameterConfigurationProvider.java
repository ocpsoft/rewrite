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
package com.ocpsoft.rewrite.servlet.config.parameters;

import javax.servlet.ServletContext;

import com.ocpsoft.rewrite.config.Configuration;
import com.ocpsoft.rewrite.config.ConfigurationBuilder;
import com.ocpsoft.rewrite.config.Direction;
import com.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import com.ocpsoft.rewrite.servlet.config.Path;
import com.ocpsoft.rewrite.servlet.config.RequestParameter;
import com.ocpsoft.rewrite.servlet.config.Substitute;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class SimplifiedParameterConfigurationProvider extends HttpConfigurationProvider
{
   public static boolean performed = false;
   public static String userName = null;
   public static String orderId = null;

   public static void reset()
   {
      performed = false;
      userName = null;
      orderId = null;
   }

   @Override
   public int priority()
   {
      return 0;
   }

   @Override
   public Configuration getConfiguration(final ServletContext context)
   {
      Configuration config = ConfigurationBuilder.begin()

               .defineRule()
               .when(Direction.isInbound().and(Path.matches("/p/{project}/story/{id}")))
               .perform(Substitute.with("/viewProject?project={project}&id={id}"))

               .defineRule()
               .when(Direction.isOutbound().and(Path.matches("/viewProject"))
                        .and(RequestParameter.exists("project"))
                        .and(RequestParameter.exists("id")))
               .perform(Substitute.with("/p/{project}/story/{id}"));

      return config;
   }
}
