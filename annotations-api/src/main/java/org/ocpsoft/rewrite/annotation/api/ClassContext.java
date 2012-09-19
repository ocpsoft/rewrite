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
package org.ocpsoft.rewrite.annotation.api;

import org.ocpsoft.rewrite.annotation.spi.AnnotationHandler;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.config.RuleBuilder;
import org.ocpsoft.rewrite.context.Context;

/**
 * Context storing information for all {@link AnnotationHandler} implementations working on a given class visit.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ClassContext extends Context
{
   /**
    * Get the {@link ConfigurationBuilder} shared by all class visits for the entire annotation configuration.
    */
   ConfigurationBuilder getConfigurationBuilder();

   /**
    * Get the {@link RuleBuilder} for the current class visit. If no base rule has been set by calling
    * {@link #setBaseRule(Rule)}, the method will return a new {@link RuleBuilder} instance.
    */
   RuleBuilder getRuleBuilder();

   /**
    * Sets the basic rule that is built from the class. Subsequent calls of {@link #getRuleBuilder()} will return
    * builder initialized with this rule.
    */
   void setBaseRule(Rule rule);

   /**
    * Get the Java class that is currently processed
    */
   Class<?> getJavaClass();

}
