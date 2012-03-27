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
package org.ocpsoft.rewrite.showcase.domain;

import javax.servlet.ServletContext;

import com.ocpsoft.rewrite.bind.El;
import com.ocpsoft.rewrite.config.Configuration;
import com.ocpsoft.rewrite.config.ConfigurationBuilder;
import com.ocpsoft.rewrite.config.Invoke;
import com.ocpsoft.rewrite.servlet.config.DispatchType;
import com.ocpsoft.rewrite.servlet.config.Domain;
import com.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import com.ocpsoft.rewrite.servlet.config.QueryString;
import com.ocpsoft.rewrite.servlet.config.rule.Join;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class DomainRewriteConfiguration extends HttpConfigurationProvider
{
   @Override
   public Configuration getConfiguration(final ServletContext context)
   {
      return ConfigurationBuilder
               .begin()

               /**
                * When a known sub-domain of example.com is accessed, it will be loaded by the application.
                */
               .defineRule()
               .when(Domain.matches("{domain}.example.com").where("domain")
                        .bindsTo(El.property("#{domains.currentName}"))
                        .and(DispatchType.isRequest())
                        .andNot(QueryString.parameterExists("disableDomainRewrite")))
               .perform(Invoke.binding(El.retrievalMethod("#{domains.load}")))

               .addRule(Join.path("/").to("/index.xhtml").withInboundCorrection())

               /**
                * Access http://{domain}.example.com:8080/context/literal and you should see a FileNotFoundException or
                * * 404 for "/{domain}.xhtml", where "{domain}" matches the sub-domain of example.com
                */
               .addRule(Join.path("/literal").to("/{domain}.xhtml")
                        .when(Domain.matches("{domain}.example.com")));
   }

   @Override
   public int priority()
   {
      return 0;
   }

}
