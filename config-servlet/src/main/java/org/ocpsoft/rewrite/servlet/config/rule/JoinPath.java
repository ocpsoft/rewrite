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
package org.ocpsoft.rewrite.servlet.config.rule;

import org.ocpsoft.rewrite.config.ConfigurationRuleParameterBuilder;
import org.ocpsoft.rewrite.param.ParameterizedPattern;
import org.ocpsoft.urlbuilder.Address;

/**
 * A partially configured {@link Join} instance.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface JoinPath
{
   /**
    * The internal server resource {@link Address} to be served when the specified {@link Join#path(String)} is
    * requested.
    * 
    * <p>
    * The given resource path may be parameterized:
    * <p>
    * <code>
    *    /example/{param}.html <br>
    *    /css/{value}.css <br>
    *    ... 
    * </code>
    * <p>
    * 
    * @param location {@link ParameterizedPattern} specifying the {@link Address} of the internal resource.
    * 
    * @see {@link ConfigurationRuleParameterBuilder#where(String)}
    */
   public Join to(String resource);
}
