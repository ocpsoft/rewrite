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
package com.ocpsoft.rewrite.servlet.config;

import javax.servlet.ServletContext;

import com.ocpsoft.rewrite.config.Configuration;
import com.ocpsoft.rewrite.config.ConfigurationBuilder;
import com.ocpsoft.rewrite.config.Direction;
import com.ocpsoft.rewrite.config.Operation;
import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class FluentHttpConfigurationTestProvider extends HttpConfigurationProvider
{
   public static boolean performed = false;

   @Override
   public int priority()
   {
      return 0;
   }

   @Override
   public Configuration getConfiguration(final ServletContext context)
   {
      return ConfigurationBuilder.begin()
               .addRule()
               .setCondition(
                        Direction.isInbound()
                                 .and(Path.matches("/path/{value}"))
                                 .and(Method.isGet().or(Method.isPost()))
                                 .orNot(
                                          Path.matches("/other")
                                                   .and(Method.isPost())
                                 )
               )
               .setOperation(new Operation() {
                  @Override
                  public void perform(final Rewrite event)
                  {
                     ((HttpInboundServletRewrite) event).sendStatusCode(200);
                     performed = true;
                  }
               });
   }

}
